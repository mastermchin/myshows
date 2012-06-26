package ru.myshows.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.myshows.api.MyShowsApi;
import ru.myshows.client.MyShowsClient;
import ru.myshows.components.TextProgressBar;
import ru.myshows.domain.*;
import ru.myshows.prefs.Settings;
import ru.myshows.util.MyShowsUtil;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 09.06.2011
 * Time: 15:19:22
 * To change this template use File | Settings | File Templates.
 */
public class ProfileFragment extends Fragment {


    private Button logoutButton;
    private ImageView avatar;
    private TextProgressBar episodesBar;
    private TextProgressBar hoursBar;
    private TextProgressBar daysBar;
    private TextView nickName;
    private String currentUser;
    private String login;
    MyShows app;


    public ProfileFragment(String login) {
        this.login = login;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.profile, container, false);
        app = (MyShows) getActivity().getApplication();

        if (login != null) {
            currentUser = login;
            new GetProfileTask(getActivity()).execute(login);


            logoutButton = (Button) view.findViewById(R.id.logout_button);
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logoutButton.setEnabled(false);
                    Settings.setString(Settings.KEY_LOGIN, null);
                    Settings.setString(Settings.KEY_PASSWORD, null);
                    Settings.setBoolean(Settings.IS_LOGGED_IN, false);
                    app.clearShows();
                    logoutButton.setEnabled(true);
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
            });
        }

        return view;
    }

    private void populateUI(Profile profile, ProfileStats stats) {
        avatar = (ImageView) getView().findViewById(R.id.avatar);
        if (profile.getAvatarUrl() != null) {
            avatar.setImageBitmap(MyShowsUtil.getAvatar(profile.getAvatarUrl()));
        }

        nickName = (TextView) getView().findViewById(R.id.profile_name);
        nickName.setText(profile.getLogin());


        episodesBar = (TextProgressBar) getView().findViewById(R.id.episodes_bar);
        episodesBar.setMax(stats.getWatchedEpisodes() + stats.getRemainingEpisodes());
        episodesBar.setProgress(stats.getWatchedEpisodes());
        episodesBar.setText(stats.getWatchedEpisodes() + "/" + (stats.getWatchedEpisodes() + stats.getRemainingEpisodes()));


        hoursBar = (TextProgressBar) getView().findViewById(R.id.hours_bar);
        hoursBar.setMax((int) stats.getWatchedHours().doubleValue() + (int) stats.getRemainingHours().doubleValue());
        hoursBar.setProgress((int) stats.getWatchedHours().doubleValue());
        hoursBar.setText((int) stats.getWatchedHours().doubleValue() + "/" + (int) (stats.getWatchedHours() + stats.getRemainingHours()));

        daysBar = (TextProgressBar) getView().findViewById(R.id.days_bar);
        daysBar.setMax(stats.getWatchedDays() + stats.getRemainingDays());
        daysBar.setProgress(stats.getWatchedDays());
        daysBar.setText(stats.getWatchedDays() + "/" + (stats.getWatchedDays() + stats.getRemainingDays()));

        if (currentUser.equals(Settings.getString(Settings.KEY_LOGIN))) {
            List<UserShow> shows = app.getUserShows();

            if (shows != null) {
                getView().findViewById(R.id.profile_shows_info).setVisibility(View.VISIBLE);
                ((TextView) getView().findViewById(R.id.profile_watching_label)).setText
                        (getResources().getString(R.string.status_watching) + " " +
                                MyShowsUtil.getUserShowsByWatchStatus(shows, MyShowsApi.STATUS.watching).size());

                ((TextView) getView().findViewById(R.id.profile_will_watch_label)).setText
                        (getResources().getString(R.string.status_will_watch) + " " +
                                MyShowsUtil.getUserShowsByWatchStatus(shows, MyShowsApi.STATUS.later).size());


                ((TextView) getView().findViewById(R.id.profile_cancelled_label)).setText
                        (getResources().getString(R.string.status_cancelled) + " " +
                                MyShowsUtil.getUserShowsByWatchStatus(shows, MyShowsApi.STATUS.cancelled).size());

                ((TextView) getView().findViewById(R.id.profile_finished_label)).setText(
                        getResources().getString(R.string.status_finished) + " " +
                                MyShowsUtil.getUserShowsByWatchStatus(shows, MyShowsApi.STATUS.finished).size());
            }

        } else {
            getView().findViewById(R.id.profile_shows_info).setVisibility(View.INVISIBLE);
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        String login = getBundleValue(intent, "login", Settings.getString(Settings.KEY_LOGIN));
//        if (currentUser != null && !currentUser.equals(login)) {
//            currentUser = login;
//            new GetProfileTask(this).execute(login);
//        }
//    }


    private String getBundleValue(Intent intent, String key, String defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key).toString();
    }


    private class GetProfileTask extends AsyncTask {
        private Context context;
        private ProgressDialog dialog;

        private GetProfileTask(Context context) {
            this.context = context;
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getResources().getString(R.string.loading));
            this.dialog.show();

        }


        @Override
        protected Profile doInBackground(Object... objects) {
            String login = (String) objects[0];
            Profile profile = null;
            if (login != null) {
                profile = MyShows.getClient().getProfile(login);
            }
            return profile;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (this.dialog.isShowing()) this.dialog.dismiss();
            if (result != null) {
                Profile profile = (Profile) result;
                populateUI(profile, profile.getStats());
            }

        }

    }
}
