package ru.myshows.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.MenuItem;
import android.widget.*;
import android.widget.Button;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.*;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.viewpagerindicator.TitlePageIndicator;
import ru.myshows.api.MyShowsApi;
import ru.myshows.api.MyShowsClient;
import ru.myshows.components.RatingDialog;
import ru.myshows.domain.*;
import ru.myshows.util.Settings;
import ru.myshows.util.EpisodeComparator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 15.06.2011
 * Time: 17:53:42
 * To change this template use File | Settings | File Templates.
 */
public class ShowActivity extends SherlockFragmentActivity {


    private TextView showTitle;
    private ImageView showLogo;
    private LinearLayout showInfo;
    private LinearLayout dateLayout;
    private LinearLayout genresLayoyt;
    private LinearLayout watchingLayoyt;
    private LinearLayout myShowsRatingLayoyt;
    private LinearLayout yoursRatingLayoyt;
    private RatingBar myShowsRatingBar;
    private RatingBar yoursRatingBar;
    private LinearLayout statusButtonsLayoyt;
    private ExpandableListView episodesList;
    private MyExpandableListAdapter adapter;
    private Button activeWatchButton;
    private Button watchingButton;
    private Button willWatchButton;
    private Button cancelledButton;
    private Button removeButton;

    private Button saveButton;

    private Integer showId;
    private MyShowsApi.STATUS watchStatus;
    private Double yoursRating;
    private RelativeLayout rootView;
    private boolean isSaveButtonShowing = false;

    MyShowsClient client = MyShowsClient.getInstance();
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    MyShows app;
    Show currentShow;
    ActionMode mMode;


    private ViewPager pager;
    private TitlePageIndicator indicator;
    private MainActivity.TabsAdapter tabsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.show);
        setContentView(R.layout.main);


        tabsAdapter = new MainActivity.TabsAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        pager.setAdapter(tabsAdapter);
        indicator.setViewPager(pager);
        indicator.setTypeface(MyShows.font);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.stripe_red);
        bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(bg);




        app = (MyShows) getApplication();
        showId = (Integer) getBundleValue(getIntent(), "showId", null);
        watchStatus = (MyShowsApi.STATUS) getBundleValue(getIntent(), "watchStatus", MyShowsApi.STATUS.remove);
        yoursRating = (Double) getBundleValue(getIntent(), "yoursRating", 0.0);
        UserShow u = app.getUserShow(showId);
        if (u != null) {
            watchStatus = u.getWatchStatus();
            yoursRating = u.getRating();
        }
        new GetShowInfoTask(this).execute();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showId = (Integer) getBundleValue(intent, "showId", null);
        watchStatus = (MyShowsApi.STATUS) getBundleValue(intent, "watchStatus", MyShowsApi.STATUS.remove);
        yoursRating = (Double) getBundleValue(intent, "yoursRating", 0.0);
        new GetShowInfoTask(this).execute();
    }

    private void populateUI() {
        rootView = (RelativeLayout) findViewById(R.id.show_root_view);
        showLogo = (ImageView) findViewById(R.id.show_logo);
        showTitle = (TextView) findViewById(R.id.show_title);
        dateLayout = (LinearLayout) findViewById(R.id.show_date_layout);
        genresLayoyt = (LinearLayout) findViewById(R.id.show_genres_layout);
        watchingLayoyt = (LinearLayout) findViewById(R.id.show_watching_layout);
        myShowsRatingLayoyt = (LinearLayout) findViewById(R.id.show_rating_myshows_layout);
        yoursRatingLayoyt = (LinearLayout) findViewById(R.id.show_rating_yours_layout);
        statusButtonsLayoyt = (LinearLayout) findViewById(R.id.show_status_buttons_layout);
        episodesList = (ExpandableListView) findViewById(R.id.episodes_list);
        watchingButton = (Button) statusButtonsLayoyt.findViewById(R.id.button_watching);
        willWatchButton = (Button) statusButtonsLayoyt.findViewById(R.id.button_will_watch);
        cancelledButton = (Button) statusButtonsLayoyt.findViewById(R.id.button_cancelled);
        removeButton = (Button) statusButtonsLayoyt.findViewById(R.id.button_remove);


        ImageLoader.getInstance().displayImage(currentShow.getImageUrl(), showLogo);


        if (currentShow.getTitle() != null) {
            showTitle.setText(currentShow.getTitle());
        }

        // date
        if (currentShow.getStartedAt() != null) {
            dateLayout.setVisibility(View.VISIBLE);
            String date = currentShow.getStartedAt();
            if (currentShow.getEndedAt() != null)
                date += " - " + currentShow.getEndedAt();
            ((TextView) dateLayout.findViewById(R.id.show_date_value)).setText(date);
        }

        // genres
        if (currentShow.getGenres() != null) {
            genresLayoyt.setVisibility(View.VISIBLE);
            ((TextView) genresLayoyt.findViewById(R.id.show_genres_valye)).setText(currentShow.getGenres());
        }
        //watching
        if (currentShow.getWatching() != null) {
            watchingLayoyt.setVisibility(View.VISIBLE);
            ((TextView) watchingLayoyt.findViewById(R.id.show_watching_value)).setText(String.valueOf(currentShow.getWatching()));
        }


        // myshows  rating
        if (currentShow.getRating() != null) {
            myShowsRatingLayoyt.setVisibility(View.VISIBLE);
            myShowsRatingBar = ((RatingBar) myShowsRatingLayoyt.findViewById(R.id.show_rating_myshows_value));
            myShowsRatingBar.setRating((float) currentShow.getRating().doubleValue());
        }

        // yours rating
        if (yoursRating != null) {
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
                        UserShow us = app.getUserShow(showId);
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
                        boolean result = client.changeShowRatio(showId, r);
                        handler.sendEmptyMessage(result ? r : 0);
                    }
                };


                dialog = ProgressDialog.show(ShowActivity.this, "", getResources().getString(R.string.loading));
                handler.postDelayed(changeShowRatioTask, 500);


            }
        });


        //status buttons show if client is logged in otherwise remove view
       // if (isLoggedIn()) {
        if (Settings.getBoolean(Settings.KEY_LOGGED_IN)) {

            statusButtonsLayoyt.setVisibility(View.VISIBLE);

            if (watchStatus.equals(MyShowsApi.STATUS.watching) || watchStatus.equals(MyShowsApi.STATUS.finished)) {
                changeButtonStyleToActive(watchingButton);
                activeWatchButton = watchingButton;
            }

            if (watchStatus.equals(MyShowsApi.STATUS.later)) {
                changeButtonStyleToActive(willWatchButton);
                activeWatchButton = willWatchButton;
            }
            if (watchStatus.equals(MyShowsApi.STATUS.cancelled)) {
                changeButtonStyleToActive(cancelledButton);
                activeWatchButton = cancelledButton;
            }

            if (watchStatus.equals(MyShowsApi.STATUS.remove)) {
                changeButtonStyleToActive(removeButton);
                activeWatchButton = removeButton;
            }

        } else {
            rootView.removeView(statusButtonsLayoyt);
            RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            listParams.addRule(RelativeLayout.BELOW, R.id.show_info);
            episodesList.setLayoutParams(listParams);
        }


        Collection<Episode> episodes = currentShow.getEpisodes();
        Episode o = (Episode) Collections.max(episodes, new EpisodeComparator());
        adapter = new MyExpandableListAdapter(episodes, o.getSeasonNumber());
        episodesList.setAdapter(adapter);
        registerForContextMenu(episodesList);


    }

    // status buttons handler, defined in show.xml

    public void changeShowStatus(View v) {
        switch (v.getId()) {
            case R.id.button_watching:
                watchStatus = MyShowsApi.STATUS.watching;
                break;
            case R.id.button_will_watch:
                watchStatus = MyShowsApi.STATUS.later;
                break;
            case R.id.button_cancelled:
                watchStatus = MyShowsApi.STATUS.cancelled;
                break;
            case R.id.button_remove:
                watchStatus = MyShowsApi.STATUS.remove;
                break;
        }
        if (v.getId() != activeWatchButton.getId())
            new ChangeShowStatusTask(ShowActivity.this.getParent()).execute(v);

    }


    View.OnClickListener saveButtonListener = new View.OnClickListener() {
        ProgressDialog dialog = null;
        Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                Toast.makeText(ShowActivity.this, msg.what, Toast.LENGTH_SHORT).show();
                removeSaveButton();
            }
        };

        Runnable checkEpisodesTask = new Runnable() {
            public void run() {
                ArrayList<Episode> episodes = (ArrayList<Episode>) adapter.getAllChildrenAsList();
                StringBuilder checkedIds = new StringBuilder();
                StringBuilder uncheckedIds = new StringBuilder();
                for (Episode e : episodes) {
                    if (e.isChecked()) checkedIds.append(e.getEpisodeId() + ",");
                    if (!e.isChecked()) uncheckedIds.append(e.getEpisodeId() + ",");
                }

                String checked = checkedIds.toString();
                if (checked.endsWith(",")) checked = checked.substring(0, checked.length() - 1);
                String unchecked = uncheckedIds.toString();
                if (unchecked.endsWith(",")) unchecked = unchecked.substring(0, unchecked.length() - 1);

                boolean result = client.syncAllShowEpisodes(showId, checked, unchecked);
                int message;
                if (result) {
                    message = R.string.changes_saved;
                    // update watched episodes in cache
                    UserShow us = app.getUserShow(showId);
                    if (us != null) {
                        us.setWatchedEpisodes(checked.split(",").length);
                       // app.setUserShowsChanged(true);
                    }
                } else {
                    message = R.string.changes_not_saved;
                }
                handler.sendEmptyMessage(message);
            }

        };

        @Override
        public void onClick(View v) {
            saveButton.setEnabled(false);
            dialog = ProgressDialog.show(getParent(), "", getResources().getString(R.string.loading));
            handler.postDelayed(checkEpisodesTask, 1000);
        }

    };

    private Button getSaveButton() {
        if (saveButton == null) {
            saveButton = new Button(ShowActivity.this);
            saveButton.setText(R.string.save);
            saveButton.setId(1);
            saveButton.setTextColor(Color.WHITE);
            saveButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
            saveButton.setOnClickListener(saveButtonListener);

        }
        return saveButton;
    }

    private void addSaveButton() {
        rootView = (RelativeLayout) findViewById(R.id.show_root_view);
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rootView.addView(getSaveButton(), buttonParams);
        // change list layout parameters
        RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        listParams.addRule(RelativeLayout.ABOVE, getSaveButton().getId());
        listParams.addRule(RelativeLayout.BELOW, R.id.show_status_buttons_layout);
        episodesList.setLayoutParams(listParams);

    }

    private void removeSaveButton() {
        rootView.removeView(saveButton);
        saveButton = null;
        isSaveButtonShowing = false;
    }


    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        menu.add(0, 1, 1, "Refresh").setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 2, "Settings").setIcon(R.drawable.ic_action_settings).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }


//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.check_all:
//                adapter.checkAll();
//                return true;
//            case R.id.view_on_site:
//                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://myshows.ru/view/" + showId + "/"));
//                startActivity(i);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }






    private void changeButtonStyleToActive(Button button) {
        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_label));
    }

    private void changeButtonStyleToInactive(Button button) {
        button.setBackgroundDrawable(null);

    }

    private Object getBundleValue(Intent intent, String key, Object defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key);
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {

        private ArrayList<Season> groups = new ArrayList<Season>();
        private ArrayList<ArrayList<Episode>> children = new ArrayList<ArrayList<Episode>>();


        public MyExpandableListAdapter(Collection<Episode> eps, int totalSeasons) {
            for (int i = 1; i <= totalSeasons; i++) {
                boolean isAllEpisodesWatched = true;
                ArrayList<Episode> seasonEpisodes = new ArrayList<Episode>();
                for (Iterator<Episode> iter = eps.iterator(); iter.hasNext(); ) {
                    Episode e = iter.next();
                    if (e.getSeasonNumber() == i) {
                        seasonEpisodes.add(e);
                        if (!e.isChecked())
                            isAllEpisodesWatched = false;
                    }
                }
                groups.add(new Season(getResources().getString(R.string.season) + " " + i, isAllEpisodesWatched));
                Collections.sort(seasonEpisodes, new EpisodeComparator("episode"));
                children.add(seasonEpisodes);

            }


        }

        public Object getAllChildrenAsList() {
            ArrayList<Episode> episodes = new ArrayList<Episode>();
            for (Iterator<ArrayList<Episode>> i = children.iterator(); i.hasNext(); ) {
                ArrayList<Episode> list = i.next();
                episodes.addAll(list);
            }
            return episodes;
        }


        public Object getChild(int groupPosition, int childPosition) {
            return children.get(groupPosition).get(childPosition);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        public int getChildrenCount(int groupPosition) {
            return children.get(groupPosition).size();
        }

        public List getGroupChildren(int groupPosition) {
            return children.get(groupPosition);
        }


        public void checkAll() {
            for (Season s : groups) {
                s.setChecked(true);
            }
            for (Episode e : (List<Episode>) getAllChildrenAsList()) {
                e.setChecked(true);
            }
            adapter.notifyDataSetChanged();
            if (!watchStatus.equals(MyShowsApi.STATUS.remove) && saveButton == null) {
                isSaveButtonShowing = true;
                addSaveButton();
            }
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            final int gp = groupPosition;
            final ViewHolder holder;
            final Season season = (Season) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.season, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.season_title);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.season_check_box);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText(season.getTitle());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    season.setChecked(isChecked);
                    mMode = startActionMode(new AnActionModeOfEpicProportions());
                }
            });
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox) v;
                    boolean isChecked = checkBox.isChecked();
                    for (Episode e : (List<Episode>) getGroupChildren(gp)) {
                        e.setChecked(isChecked);
                    }
                    adapter.notifyDataSetChanged();
                    if (!watchStatus.equals(MyShowsApi.STATUS.remove) && saveButton == null) {
                        isSaveButtonShowing = true;
                        addSaveButton();
                    }
                }
            });
            holder.checkBox.setChecked(season.isChecked());
            return convertView;
        }

        protected class ViewHolder {
            protected TextView title;
            protected CheckBox checkBox;
            protected TextView shortTitle;
            private TextView airDate;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final Episode episode = (Episode) getChild(groupPosition, childPosition);
            final int gp = groupPosition;
            final ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.episode, parent, false);
                holder = new ViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.episode_title);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.episode_check_box);
                holder.shortTitle = (TextView) convertView.findViewById(R.id.episode_short_title);
                holder.airDate = (TextView) convertView.findViewById(R.id.episode_air_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(episode.getTitle());
            holder.shortTitle.setText(episode.getShortName() != null ? episode.getShortName() : "");
            holder.airDate.setText(episode.getAirDate() != null ? df.format(episode.getAirDate()) : "unknown");

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Season season = (Season) getGroup(gp);
                    episode.setChecked(isChecked);
                    if (!isChecked && season.isChecked()) {
                        season.setChecked(isChecked);
                        adapter.notifyDataSetChanged();
                    }

                    boolean isAllEpisodesChecked = true;
                    for (Episode e : (List<Episode>) getGroupChildren(gp)) {
                        if (!e.isChecked()) {
                            isAllEpisodesChecked = false;
                            break;
                        }
                    }
                    if (isAllEpisodesChecked) {
                        season.setChecked(true);
                        notifyDataSetChanged();
                    }


                }
            });

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!watchStatus.equals(MyShowsApi.STATUS.remove) && saveButton == null) {
                        isSaveButtonShowing = true;
                        addSaveButton();
                    }
                }
            });


            holder.checkBox.setChecked(episode.isChecked());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.checkBox.setChecked(!holder.checkBox.isChecked());
                    if (!watchStatus.equals(MyShowsApi.STATUS.remove) && saveButton == null) {
                        isSaveButtonShowing = true;
                        addSaveButton();
                    }
                }
            });

            convertView.setOnCreateContextMenuListener(null);

            return convertView;
        }

        public Object getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        public int getGroupCount() {
            return groups.size();
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);

        final Episode episode = (Episode) adapter.getChild(group, child);

        if (type == 1) {
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    int rating = msg.arg1;
                    if (episode != null) {
                        boolean result = client.changeEpisodeRatio(rating, episode.getEpisodeId());
                    }
                }
            };
            RatingDialog rate = new RatingDialog(getParent(), handler);
            rate.setTitle(R.string.episode_rating);
            rate.show();

        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        return super.onContextItemSelected(menuItem);
    }


    private class GetShowInfoTask extends AsyncTask {
        private Context context;
        private ProgressDialog dialog;

        private GetShowInfoTask(Context context) {
            this.context = context;
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getResources().getString(R.string.loading));
            this.dialog.show();

        }


        @Override
        protected Show doInBackground(Object... objects) {
            currentShow = client.getShowInfo(showId);
            if (currentShow != null) {
                populateGenres(currentShow, client.getGenresListAsMap());
                populateWatchedEpisodes(currentShow, client.getSeenEpisodes(showId));
            }
            return currentShow;


        }

        @Override
        protected void onPostExecute(Object result) {
            if (this.dialog.isShowing()) this.dialog.dismiss();
            if (result != null) {
                populateUI();
            }

        }


        private void populateWatchedEpisodes(Show show, List<WatchedEpisode> episodes) {
            if (episodes == null || episodes.size() == 0) return;
            Iterator<Episode> i = show.getEpisodes().iterator();
            while (i.hasNext()) {
                Episode e = i.next();
                Iterator<WatchedEpisode> iter = episodes.iterator();
                while (iter.hasNext()) {
                    WatchedEpisode we = iter.next();
                    if (e.getEpisodeId().equals(we.getWatchedId())) {
                        e.setChecked(true);
                        break;
                    }
                }

            }
        }


        private void populateGenres(Show show, Map<Integer, Genre> allGenres) {
            Collection<Integer> showGenres = show.getGenresIds();
            String genresString = "";
            if (showGenres != null) {
                for (Iterator<Integer> iter = showGenres.iterator(); iter.hasNext(); ) {
                    Integer genreId = iter.next();
                    genresString += " " + allGenres.get(genreId).getTitle();
                }
            }
            show.setGenres(genresString);

        }


    }

    private class ChangeShowStatusTask extends AsyncTask {

        private Context context;
        private ProgressDialog dialog;
        private Button button;

        private ChangeShowStatusTask(Context context) {
            this.context = context;
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getResources().getString(R.string.loading));
            this.dialog.show();

        }


        @Override
        protected Boolean doInBackground(Object... objects) {
            button = (Button) objects[0];
            return client.changeShowStatus(showId, watchStatus);
        }

        @Override
        protected void onPostExecute(Object result) {
            if (this.dialog.isShowing()) this.dialog.dismiss();
            if ((Boolean) result) {
                boolean isRemove = watchStatus.equals(MyShowsApi.STATUS.remove);
                // delete user show from cache
                if (isRemove) {
                    app.removeUserShow(showId);
                  //  app.setUserShowsChanged(true);
                } else {
                    // add new show to cache
                    app.addOrUpdateUserShow(new UserShow(currentShow, watchStatus));
                  //  app.setUserShowsChanged(true);
                }

                // disable yours rating if clicked status = remove , enable if != remove
                yoursRatingBar.setIsIndicator(isRemove);
                System.out.println("Disable rating bar");
                // show/hide save button
                if (!isRemove && saveButton == null && isSaveButtonShowing)
                    addSaveButton();
                else if (isRemove && saveButton != null)
                    removeSaveButton();
                // change active button style
                changeButtonStyleToActive(button);
                changeButtonStyleToInactive(activeWatchButton);
                activeWatchButton = button;

            }

        }


    }

    private final class AnActionModeOfEpicProportions implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //Used to put dark icons on light action bar



            menu.add("Search")
                    .setIcon(R.drawable.ic_action_search)
                    .setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menu.add("Settings")
                    .setIcon(R.drawable.ic_action_settings )
                    .setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
            Toast.makeText(ShowActivity.this, "Got click: " + item, Toast.LENGTH_SHORT).show();
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }
}
