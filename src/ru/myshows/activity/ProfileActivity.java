package ru.myshows.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import ru.myshows.api.MyShowsApi;
import ru.myshows.client.MyShowsClient;
import ru.myshows.components.TextProgressBar;
import ru.myshows.domain.*;
import ru.myshows.prefs.Prefs;
import ru.myshows.util.MyShowsUtil;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 09.06.2011
 * Time: 15:19:22
 * To change this template use File | Settings | File Templates.
 */
public class ProfileActivity extends Activity {

    MyShowsClient client = MyShowsClient.getInstance();

    private Button logoutButton;
    private ImageView avatar;
    private TextProgressBar episodesBar;
    private TextProgressBar hoursBar;
    private TextProgressBar daysBar;
    private TextView nickName;
    private String currentUser;
    MyShows app;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        app = (MyShows) getApplication();
        String login = getBundleValue(getIntent(), "login", Prefs.getStringPrefs(this, Prefs.KEY_LOGIN));
        currentUser = login;
        new GetProfileTask(this).execute(login);


        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutButton.setEnabled(false);
                Prefs.setStringPrefs(ProfileActivity.this, Prefs.KEY_LOGIN, null);
                Prefs.setStringPrefs(ProfileActivity.this, Prefs.KEY_PASSWORD, null);
                app.setLoggedIn(false);
                app.clearShows();
                finish();
                logoutButton.setEnabled(true);
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
    }

    private void populateUI(Profile profile, ProfileStats stats) {
        avatar = (ImageView) findViewById(R.id.avatar);
        if (profile.getAvatarUrl() != null) {
            avatar.setImageBitmap(MyShowsUtil.getAvatar(profile.getAvatarUrl()));
        }

        nickName = (TextView) findViewById(R.id.profile_name);
        nickName.setText(profile.getLogin());


        episodesBar = (TextProgressBar) findViewById(R.id.episodes_bar);
        episodesBar.setMax(stats.getWatchedEpisodes() + stats.getRemainingEpisodes());
        episodesBar.setProgress(stats.getWatchedEpisodes());
        episodesBar.setText(stats.getWatchedEpisodes() + "/" + (stats.getWatchedEpisodes() + stats.getRemainingEpisodes()));


        hoursBar = (TextProgressBar) findViewById(R.id.hours_bar);
        hoursBar.setMax((int) stats.getWatchedHours().doubleValue() + (int) stats.getRemainingHours().doubleValue());
        hoursBar.setProgress((int) stats.getWatchedHours().doubleValue());
        hoursBar.setText((int) stats.getWatchedHours().doubleValue() + "/" + (int) (stats.getWatchedHours() + stats.getRemainingHours()));

        daysBar = (TextProgressBar) findViewById(R.id.days_bar);
        daysBar.setMax(stats.getWatchedDays() + stats.getRemainingDays());
        daysBar.setProgress(stats.getWatchedDays());
        daysBar.setText(stats.getWatchedDays() + "/" + (stats.getWatchedDays() + stats.getRemainingDays()));

        if (currentUser.equals(Prefs.getStringPrefs(ProfileActivity.this, Prefs.KEY_LOGIN))) {
            List<UserShow> shows = app.getUserShows();

            if (shows != null) {
                findViewById(R.id.profile_shows_info).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.profile_watching_label)).setText
                        (getResources().getString(R.string.status_watching) + " " +
                                MyShowsUtil.getUserShowsByWatchStatus(shows, MyShowsApi.STATUS.watching).size());

                ((TextView) findViewById(R.id.profile_will_watch_label)).setText
                        (getResources().getString(R.string.status_will_watch) + " " +
                                MyShowsUtil.getUserShowsByWatchStatus(shows, MyShowsApi.STATUS.later).size());


                ((TextView) findViewById(R.id.profile_cancelled_label)).setText
                        (getResources().getString(R.string.status_cancelled) + " " +
                                MyShowsUtil.getUserShowsByWatchStatus(shows, MyShowsApi.STATUS.cancelled).size());

                ((TextView) findViewById(R.id.profile_finished_label)).setText(
                        getResources().getString(R.string.status_finished) + " " +
                                MyShowsUtil.getUserShowsByWatchStatus(shows, MyShowsApi.STATUS.finished).size());
            }

        } else {
            findViewById(R.id.profile_shows_info).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String login = getBundleValue(intent, "login", Prefs.getStringPrefs(this, Prefs.KEY_LOGIN));
        if (currentUser != null && !currentUser.equals(login)) {
            currentUser = login;
            new GetProfileTask(this).execute(login);
        }
    }


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
                profile = client.getProfile(login);
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
