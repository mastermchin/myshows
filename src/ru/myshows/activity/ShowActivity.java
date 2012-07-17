package ru.myshows.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.TitlePageIndicator;
import ru.myshows.adapters.TabsAdapter;
import ru.myshows.api.MyShowsApi;
import ru.myshows.domain.*;
import ru.myshows.fragments.EpisodesFragment;
import ru.myshows.fragments.ShowFragment;
import ru.myshows.tasks.GetShowTask;
import ru.myshows.tasks.TaskListener;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 15.06.2011
 * Time: 17:53:42
 * To change this template use File | Settings | File Templates.
 */
public class ShowActivity extends SherlockFragmentActivity implements TaskListener<Show> {


    private Integer showId;
    private MyShowsApi.STATUS watchStatus;
    private Double yoursRating;
    private String title;

    private ViewPager pager;
    private TitlePageIndicator indicator;
    private TabsAdapter tabsAdapter;
    private LinearLayout indicatorLayout;
    private ProgressBar progress;


    public ShowActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_info);

        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), true);
        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        pager.setAdapter(tabsAdapter);
        indicator.setViewPager(pager);
        indicator.setTypeface(MyShows.font);
        progress = (ProgressBar) findViewById(R.id.progress_show);
        indicatorLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        title = (String) getBundleValue(getIntent(), "title", null);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        if (title != null)
            getSupportActionBar().setTitle(title);

        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.stripe_red);
        bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(bg);


        showId = (Integer) getBundleValue(getIntent(), "showId", null);
        watchStatus = (MyShowsApi.STATUS) getBundleValue(getIntent(), "watchStatus",  MyShowsApi.STATUS.remove);
        yoursRating = (Double) getBundleValue(getIntent(), "yoursRating", null);


        GetShowTask getShowTask = new GetShowTask(this);
        getShowTask.setTaskListener(this);
        getShowTask.execute(showId);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {}

            @Override
            public void onPageSelected(int i) {
                if (i == 1){
                    EpisodesFragment episodesFragment = (EpisodesFragment) tabsAdapter.getItem(1);
                    if (episodesFragment.getAdapter() != null)
                        episodesFragment.getAdapter().notifyDataSetChanged();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {}
        });

    }


    @Override
    public void onTaskComplete(Show result) {

        UserShow us = MyShows.getUserShow(showId);
        if (us != null)
            watchStatus = us.getWatchStatus();


        result.setWatchStatus(watchStatus);
        progress.setVisibility(View.GONE);
        indicatorLayout.setVisibility(View.VISIBLE);
        tabsAdapter.addFragment(new ShowFragment(result, yoursRating), getResources().getString(R.string.tab_show));
        tabsAdapter.addFragment(new EpisodesFragment(result), getResources().getString(R.string.tab_episodes));
        indicator.notifyDataSetChanged();
        tabsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskFailed(Exception e) {
        if (e != null){
            progress.setVisibility(View.GONE);
        }
        final AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this)
                .setTitle(R.string.something_wrong)
                .setMessage(R.string.try_again)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GetShowTask getShowTask = new GetShowTask(ShowActivity.this);
                        getShowTask.setTaskListener(ShowActivity.this);
                        getShowTask.execute(showId);
                    }
                })
                .setNegativeButton(R.string.no, null);
        alert = builder.create();
        alert.show();
    }


    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        menu.add(0, 1, 1, R.string.menu_update).setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 2, R.string.menu_view_on_site).setIcon(R.drawable.ic_web_site).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
        if (MyShows.isLoggedIn)
            menu.add(0, 3, 3, R.string.menu_settings).setIcon(R.drawable.ic_action_settings).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 1:

                //http://code.google.com/p/android/issues/detail?id=19211#makechanges
                //http://stackoverflow.com/questions/9727173/support-fragmentpageradapter-holds-reference-to-old-fragments/9744146#9744146
                //http://stackoverflow.com/questions/10022179/fragmentpageradapter-with-two-fragments-go-to-the-first-from-the-second-and-upd

                GetShowTask getShowTask = new GetShowTask(ShowActivity.this, true);
                getShowTask.setTaskListener(new TaskListener<Show>() {
                    @Override
                    public void onTaskComplete(Show result) {
                        UserShow us = MyShows.getUserShow(showId);
                        if (us != null)
                            watchStatus = us.getWatchStatus();
                        progress.setVisibility(View.GONE);
                        indicatorLayout.setVisibility(View.VISIBLE);
                        ShowFragment showFragment = (ShowFragment) tabsAdapter.getItem(0);
                        showFragment.refresh(result);
                        EpisodesFragment episodesFragment = (EpisodesFragment) tabsAdapter.getItem(1);
                        episodesFragment.refresh(result);
                        tabsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onTaskFailed(Exception e) {

                    }
                });
                progress.setVisibility(View.VISIBLE);
                indicatorLayout.setVisibility(View.GONE);
                //tabsAdapter = new TabsAdapter(getSupportFragmentManager(), true);
                getShowTask.execute(showId);
                break;
            case 2:
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://myshows.ru/view/" + showId + "/"));
                startActivity(i);
                break;
            case 3:
                startActivity(new Intent(this, SettingsAcrivity.class));
                break;
        }
        return true;
    }


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


}
