package ru.myshows.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.actionbarsherlock.app.ActionBar;
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
import ru.myshows.fragments.EpisodesFragment;
import ru.myshows.fragments.ShowFragment;
import ru.myshows.tasks.ChangeShowStatusTask;
import ru.myshows.tasks.GetShowTask;
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
public class ShowActivity extends SherlockFragmentActivity implements GetShowTask.ShowLoadingListener {


    private Integer showId;
    private MyShowsApi.STATUS watchStatus;
    private Double yoursRating;
    private String title;

    ActionMode mMode;


    private ViewPager pager;
    private TitlePageIndicator indicator;
    private MainActivity.TabsAdapter tabsAdapter;
    private LinearLayout indicatorLayout;
    private ProgressBar progress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_info);


        tabsAdapter = new MainActivity.TabsAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        pager.setAdapter(tabsAdapter);
        indicator.setViewPager(pager);
        indicator.setTypeface(MyShows.font);
        progress = (ProgressBar) findViewById(R.id.progress);
        indicatorLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        title = (String) getBundleValue(getIntent(), "title", null);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getSupportActionBar().setTitle(title);

        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.stripe_red);
        bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(bg);


        showId = (Integer) getBundleValue(getIntent(), "showId", null);
        watchStatus = (MyShowsApi.STATUS) getBundleValue(getIntent(), "watchStatus", null);
        yoursRating = (Double) getBundleValue(getIntent(), "yoursRating", null);



        GetShowTask getShowTask = new GetShowTask(this);
        getShowTask.setShowLoadingListener(this);
        getShowTask.execute(showId);


//        UserShow u = app.getUserShow(showId);
//        if (u != null) {
//            watchStatus = u.getWatchStatus();
//            yoursRating = u.getRating();
//        }
        //new GetShowInfoTask(this).execute(showId);

    }

    @Override
    public boolean onShowLoaded(Show show) {
        progress.setVisibility(View.GONE);
        indicatorLayout.setVisibility(View.VISIBLE);

        tabsAdapter.addFragment(new ShowFragment(show, watchStatus, yoursRating), "Show");
        tabsAdapter.addFragment(new EpisodesFragment(show), "Episodes");
        indicator.notifyDataSetChanged();
        tabsAdapter.notifyDataSetChanged();

        return true;
    }


    // status buttons handler, defined in show.xml


//    private Button getSaveButton() {
//        if (saveButton == null) {
//            saveButton = new Button(ShowActivity.this);
//            saveButton.setText(R.string.save);
//            saveButton.setId(1);
//            saveButton.setTextColor(Color.WHITE);
//            saveButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_button));
//            saveButton.setOnClickListener(saveButtonListener);
//
//        }
//        return saveButton;
//    }

//    private void addSaveButton() {
//        rootView = (RelativeLayout) findViewById(R.id.show_root_view);
//        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
//        rootView.addView(getSaveButton(), buttonParams);
//        // change list layout parameters
//        RelativeLayout.LayoutParams listParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        listParams.addRule(RelativeLayout.ABOVE, getSaveButton().getId());
//        listParams.addRule(RelativeLayout.BELOW, R.id.show_status_buttons_layout);
//        episodesList.setLayoutParams(listParams);
//
//    }

//    private void removeSaveButton() {
//        rootView.removeView(saveButton);
//        saveButton = null;
//        isSaveButtonShowing = false;
//    }
//

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        menu.add(0, 1, 1, "Refresh").setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 2, "Settings").setIcon(R.drawable.ic_action_settings).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
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


    //
//    private void changeButtonStyleToActive(Button button) {
//        button.setBackgroundDrawable(getResources().getDrawable(R.drawable.red_label));
//    }
//
//    private void changeButtonStyleToInactive(Button button) {
//        button.setBackgroundDrawable(null);
//
//    }
//
    private Object getBundleValue(Intent intent, String key, Object defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key);
    }


//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
//        int type = ExpandableListView.getPackedPositionType(info.packedPosition);
//        int group = ExpandableListView.getPackedPositionGroup(info.packedPosition);
//        int child = ExpandableListView.getPackedPositionChild(info.packedPosition);
//
//        final Episode episode = (Episode) adapter.getChild(group, child);
//
//        if (type == 1) {
//            Handler handler = new Handler() {
//                @Override
//                public void handleMessage(Message msg) {
//                    int rating = msg.arg1;
//                    if (episode != null) {
//                        boolean result = client.changeEpisodeRatio(rating, episode.getEpisodeId());
//                    }
//                }
//            };
//            RatingDialog rate = new RatingDialog(getParent(), handler);
//            rate.setTitle(R.string.episode_rating);
//            rate.show();
//
//        }
//    }
//

    public void changeShowStatus(View v) {
        ShowFragment showFragment = (ShowFragment) tabsAdapter.getItem(0);
        showFragment.changeShowStatus(v);
    }

    private final class AnActionModeOfEpicProportions implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            //Used to put dark icons on light action bar


            menu.add("Search")
                    .setIcon(R.drawable.ic_action_search)
                    .setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);

            menu.add("Settings")
                    .setIcon(R.drawable.ic_action_settings)
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
