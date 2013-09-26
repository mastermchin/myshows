package ru.myshows.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.widget.IcsSpinner;
import ru.myshows.adapters.TabsAdapter;
import ru.myshows.api.MyShowsApi;
import ru.myshows.domain.Show;
import ru.myshows.domain.UserShow;
import ru.myshows.fragments.EpisodesFragment;
import ru.myshows.fragments.ShowFragment;
import ru.myshows.tasks.GetShowTask;
import ru.myshows.tasks.TaskListener;

import java.util.ArrayList;
import java.util.List;

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
    private PagerTabStrip pagerTabStrip;
    private TabsAdapter tabsAdapter;
    private LinearLayout indicatorLayout;
    private ProgressBar progress;
    private Bundle savedInstanceState;

    public ShowActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_info);
        this.savedInstanceState = savedInstanceState;

        tabsAdapter = new TabsAdapter(getSupportFragmentManager(), true);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(tabsAdapter);

        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setTabIndicatorColorResource(R.color.light_red);

        progress = (ProgressBar) findViewById(R.id.progress_show);
        indicatorLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        title = (String) getBundleValue(getIntent(), "title", null);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getSupportActionBar().setIcon(R.drawable.ic_list_logo);
        if (title != null)
            getSupportActionBar().setTitle(title);

        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.stripe_red);
        bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(bg);


        showId = (Integer) getBundleValue(getIntent(), "showId", null);
        if (savedInstanceState != null) {
            showId = savedInstanceState.getInt("showId");
        }

        watchStatus = (MyShowsApi.STATUS) getBundleValue(getIntent(), "watchStatus", MyShowsApi.STATUS.remove);
        yoursRating = (Double) getBundleValue(getIntent(), "yoursRating", null);


        GetShowTask getShowTask = new GetShowTask(this);
        getShowTask.setTaskListener(this);
        getShowTask.execute(showId);



    }


    @Override
    public void onTaskComplete(Show result) {

        UserShow us = MyShows.getUserShow(showId);
        if (us != null)
            watchStatus = us.getWatchStatus();

        result.setWatchStatus(watchStatus);
        progress.setVisibility(View.GONE);
        indicatorLayout.setVisibility(View.VISIBLE);

        if (savedInstanceState == null) {
            tabsAdapter.addFragment(new ShowFragment(result, yoursRating), getResources().getString(R.string.tab_show));
            tabsAdapter.addFragment(new EpisodesFragment(result), getResources().getString(R.string.tab_episodes));
        } else {
            Integer count = savedInstanceState.getInt("tabsCount");
            String[] titles = savedInstanceState.getStringArray("titles");
            for (int i = 0; i < count; i++) {
                tabsAdapter.addFragment(getFragment(i), titles[i]);
            }
            pager.setCurrentItem(savedInstanceState.getInt("currentTab"));
        }

        populateExternalLinkActions(result);
        //indicator.notifyDataSetChanged();
        tabsAdapter.notifyDataSetChanged();

    }

    @Override
    public void onTaskFailed(Exception e) {
        if (e != null) {
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


    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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


    public void changeShowStatus(View v) {
        ShowFragment showFragment = (ShowFragment) tabsAdapter.getItem(0);
        showFragment.changeShowStatus(v);
    }

    private void populateExternalLinkActions(Show show) {

        List<SiteLink> links = new ArrayList<SiteLink>();
        links.add(new SiteLink("MyShows", "http://myshows.ru/view/" + show.getShowId() + "/"));
        if (show.getKinopoiskId() != null && !show.getKinopoiskId().equals("null"))
            links.add(new SiteLink("Kinopoisk", "http://www.kinopoisk.ru/film/" + show.getKinopoiskId() + "/"));
        if (show.getImdbId() != null && !show.getImdbId().equals("null"))
            links.add(new SiteLink("IMDB", "http://www.imdb.com/title/" + show.getImdbId() + "/"));
        if (show.getTvrageId() != null && !show.getTvrageId().equals("null"))
            links.add(new SiteLink("TV Rage", "http://www.tvrage.com/shows/id-" + show.getTvrageId()));
        LinksAdapter linkAdapter = new LinksAdapter(this, R.layout.external_link, links);


        View customNav = LayoutInflater.from(this).inflate(R.layout.custom_show_action_bar, null);
        IcsSpinner spinner = (IcsSpinner) customNav.findViewById(R.id.spinner);
        spinner.setAdapter(linkAdapter);



        ImageView refresh = (ImageView) customNav.findViewById(R.id.refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetShowTask getShowTask = new GetShowTask(ShowActivity.this, true);
                getShowTask.setTaskListener(new TaskListener<Show>() {
                    @Override
                    public void onTaskComplete(Show result) {
                        UserShow us = MyShows.getUserShow(showId);
                        if (us != null)
                            watchStatus = us.getWatchStatus();
                        result.setWatchStatus(watchStatus);
                        progress.setVisibility(View.GONE);
                        indicatorLayout.setVisibility(View.VISIBLE);
                        ShowFragment showFragment = (ShowFragment) getFragment(0);
                        showFragment.refresh(result);
                        EpisodesFragment episodesFragment = (EpisodesFragment) getFragment(1);
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
            }
        });

        ImageView settings = (ImageView) customNav.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShowActivity.this, SettingsAcrivity.class));
            }
        });

        getSupportActionBar().setCustomView(customNav, new ActionBar.LayoutParams(Gravity.RIGHT));
        getSupportActionBar().setDisplayShowCustomEnabled(true);

    }


    public static class SiteLink {
        private String name;
        private String url;

        public SiteLink(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }


    }

    private static class LinksAdapter extends ArrayAdapter<SiteLink> {

        private List<SiteLink> strings;
        private Context context;

        private LinksAdapter(Context context, int textViewResourceId, List<SiteLink> objects) {
            super(context, textViewResourceId, objects);
            this.strings = objects;
            this.context = context;
        }

        @Override
        public int getCount() {
            if (strings == null) return 0;
            return strings.size();
        }

        @Override
        public SiteLink getItem(int position) {
            return super.getItem(position);
        }


        // return views of drop down items
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            final SiteLink siteLink = strings.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // at 0 position show only icon

            TextView site = (TextView) inflater.inflate(R.layout.external_link, null);
            site.setText(siteLink.getName());

            site.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(siteLink.getUrl()));
                    context.startActivity(i);
                }
            });
            return site;


        }


        // return header view of drop down
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(R.layout.icon, null);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("showId", showId);
        if (tabsAdapter != null) {
            outState.putInt("tabsCount", tabsAdapter.getCount());
            List<String> titles = tabsAdapter.getTitles();
            outState.putStringArray("titles", titles.toArray(new String[titles.size()]));
        }
        if (pager != null)
            outState.putInt("currentTab", pager.getCurrentItem());

    }

    private Fragment getFragment(int position) {
        return savedInstanceState == null ? tabsAdapter.getItem(position) : getSupportFragmentManager().findFragmentByTag(getFragmentTag(position));
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.pager + ":" + position;
    }

}
