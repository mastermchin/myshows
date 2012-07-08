package ru.myshows.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import ru.myshows.activity.MyShows;
import ru.myshows.activity.R;
import ru.myshows.api.MyShowsApi;
import ru.myshows.domain.Episode;
import ru.myshows.domain.Show;
import ru.myshows.domain.UserShow;
import ru.myshows.tasks.ChangeShowStatusTask;
import ru.myshows.util.EpisodeComparator;
import ru.myshows.util.Settings;

import java.util.Collection;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 07.07.12
 * Time: 1:04
 * To change this template use File | Settings | File Templates.
 */
public class ShowFragment extends Fragment implements ChangeShowStatusTask.ChangeShowStatusListener {


    private TextView showTitle;
    private ImageView showLogo;
    private LinearLayout dateLayout;
    private LinearLayout genresLayoyt;
    private LinearLayout watchingLayoyt;
    private LinearLayout myShowsRatingLayoyt;
    private LinearLayout yoursRatingLayoyt;
    private RatingBar myShowsRatingBar;
    private RatingBar yoursRatingBar;
    private LinearLayout statusButtonsLayoyt;
    private Button activeWatchButton;
    private Button watchingButton;
    private Button willWatchButton;
    private Button cancelledButton;
    private Button removeButton;
    private RelativeLayout rootView;
    private boolean isSaveButtonShowing = false;

    private Show show;
    private MyShowsApi.STATUS watchStatus;
    private Double yoursRating;

    public ShowFragment(Show show, MyShowsApi.STATUS watchStatus, Double yoursRating) {
        this.show = show;
        this.watchStatus = watchStatus;
        this.yoursRating = yoursRating;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show, container, false);
        populateUI(view);
        return view;
    }


    private void populateUI(View view) {
        rootView = (RelativeLayout) view.findViewById(R.id.show_root_view);
        showLogo = (ImageView) view.findViewById(R.id.show_logo);
        showTitle = (TextView) view.findViewById(R.id.show_title);
        dateLayout = (LinearLayout) view.findViewById(R.id.show_date_layout);
        genresLayoyt = (LinearLayout) view.findViewById(R.id.show_genres_layout);
        watchingLayoyt = (LinearLayout) view.findViewById(R.id.show_watching_layout);
        myShowsRatingLayoyt = (LinearLayout) view.findViewById(R.id.show_rating_myshows_layout);
        yoursRatingLayoyt = (LinearLayout) view.findViewById(R.id.show_rating_yours_layout);
        statusButtonsLayoyt = (LinearLayout) view.findViewById(R.id.show_status_buttons_layout);
        watchingButton = (Button) statusButtonsLayoyt.findViewById(R.id.button_watching);
        willWatchButton = (Button) statusButtonsLayoyt.findViewById(R.id.button_will_watch);
        cancelledButton = (Button) statusButtonsLayoyt.findViewById(R.id.button_cancelled);
        removeButton = (Button) statusButtonsLayoyt.findViewById(R.id.button_remove);


        ImageLoader.getInstance().displayImage(show.getImageUrl(), showLogo);


        if (show.getTitle() != null) {
            showTitle.setText(show.getTitle());
        }

        // date
        if (show.getStartedAt() != null) {
            dateLayout.setVisibility(View.VISIBLE);
            String date = show.getStartedAt();
            if (show.getEndedAt() != null)
                date += " - " + show.getEndedAt();
            ((TextView) dateLayout.findViewById(R.id.show_date_value)).setText(date);
        }

        // genres
        if (show.getGenres() != null) {
            genresLayoyt.setVisibility(View.VISIBLE);
            ((TextView) genresLayoyt.findViewById(R.id.show_genres_valye)).setText(show.getGenres());
        }
        //watching
        if (show.getWatching() != null) {
            watchingLayoyt.setVisibility(View.VISIBLE);
            ((TextView) watchingLayoyt.findViewById(R.id.show_watching_value)).setText(String.valueOf(show.getWatching()));
        }


        // myshows  rating
        if (show.getRating() != null) {
            myShowsRatingLayoyt.setVisibility(View.VISIBLE);
            myShowsRatingBar = ((RatingBar) myShowsRatingLayoyt.findViewById(R.id.show_rating_myshows_value));
            myShowsRatingBar.setRating((float) show.getRating().doubleValue());
        }

        // yours rating
        if (show != null) {
            yoursRatingLayoyt.setVisibility(View.VISIBLE);

            yoursRatingBar = ((RatingBar) yoursRatingLayoyt.findViewById(R.id.show_rating_yours_value));
            yoursRatingBar.setRating((float) yoursRating.doubleValue());

            // disable rating changing if remove status
            if (watchStatus.equals(MyShowsApi.STATUS.remove))
                yoursRatingBar.setIsIndicator(true);

        }

        yoursRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            ProgressDialog dialog = null;

            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    if (msg.what != 0) {
                        // update show rating in cache
                        UserShow us = MyShows.getUserShow(show.getShowId());
                        if (us != null) {
                            us.setRating((double) msg.what);
                            //app.setUserShowsChanged(true);
                        }
                    }
                }
            };


            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                final int r = (int) rating;
                Runnable changeShowRatioTask = new Runnable() {
                    @Override
                    public void run() {
                        boolean result = MyShows.client.changeShowRatio(show.getShowId(), r);
                        handler.sendEmptyMessage(result ? r : 0);
                    }
                };


                dialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.loading));
                handler.postDelayed(changeShowRatioTask, 500);


            }
        });


        //status buttons show if client is logged in otherwise remove view
        // if (isLoggedIn()) {
//        if (Settings.getBoolean(Settings.KEY_LOGGED_IN)) {
//
//            statusButtonsLayoyt.setVisibility(View.VISIBLE);
//
//            if (watchStatus.equals(MyShowsApi.STATUS.watching) || watchStatus.equals(MyShowsApi.STATUS.finished)) {
//                changeButtonStyleToActive(watchingButton);
//                activeWatchButton = watchingButton;
//            }
//
//            if (watchStatus.equals(MyShowsApi.STATUS.later)) {
//                changeButtonStyleToActive(willWatchButton);
//                activeWatchButton = willWatchButton;
//            }
//            if (watchStatus.equals(MyShowsApi.STATUS.cancelled)) {
//                changeButtonStyleToActive(cancelledButton);
//                activeWatchButton = cancelledButton;
//            }
//
//            if (watchStatus.equals(MyShowsApi.STATUS.remove)) {
//                changeButtonStyleToActive(removeButton);
//                activeWatchButton = removeButton;
//            }
//
//        } else {
//            rootView.removeView(statusButtonsLayoyt);
////            RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
////            listParams.addRule(RelativeLayout.BELOW, R.id.show_info);
////            episodesList.setLayoutParams(listParams);
//        }


//        Collection<Episode> episodes = currentShow.getEpisodes();
//        Episode o = (Episode) Collections.max(episodes, new EpisodeComparator());
//        adapter = new MyExpandableListAdapter(episodes, o.getSeasonNumber());
//        episodesList.setAdapter(adapter);
//        registerForContextMenu(episodesList);


    }


    private void changeButtonStyleToActive(Button button) {
        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_label));
    }

    @Override
    public boolean onShowStatusChanged(boolean result) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }


    //    public void changeShowStatus(View v) {
//        switch (v.getId()) {
//            case R.id.button_watching:
//                watchStatus = MyShowsApi.STATUS.watching;
//                break;
//            case R.id.button_will_watch:
//                watchStatus = MyShowsApi.STATUS.later;
//                break;
//            case R.id.button_cancelled:
//                watchStatus = MyShowsApi.STATUS.cancelled;
//                break;
//            case R.id.button_remove:
//                watchStatus = MyShowsApi.STATUS.remove;
//                break;
//        }
//        if (v.getId() != activeWatchButton.getId())
//            new ChangeShowStatusTask(ShowActivity.this.getParent()).execute(v);
//
//    }



}
