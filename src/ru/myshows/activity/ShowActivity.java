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
        getShowTask.setShowLoadingListener(this);
        getShowTask.execute(showId);


    }

    @Override
    public boolean onShowLoaded(Show show) {
        progress.setVisibility(View.GONE);
        indicatorLayout.setVisibility(View.VISIBLE);

        tabsAdapter.addFragment(new ShowFragment(show, watchStatus, yoursRating), getResources().getString(R.string.tab_show));
        tabsAdapter.addFragment(new EpisodesFragment(show), getResources().getString(R.string.tab_episodes));
        indicator.notifyDataSetChanged();
        tabsAdapter.notifyDataSetChanged();

        return true;
    }



    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        menu.add(0, 1, 1, R.string.menu_update).setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 2, R.string.menu_view_on_site).setIcon(R.drawable.ic_web_site).setShowAsAction(com.actionbarsherlock.view.MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
                GetShowTask getShowTask = new GetShowTask(this, true);
                getShowTask.setShowLoadingListener(this);
                progress.setVisibility(View.VISIBLE);
                indicatorLayout.setVisibility(View.GONE);
                tabsAdapter = new MainActivity.TabsAdapter(getSupportFragmentManager());
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
