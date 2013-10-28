package ru.myshows.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.*;
import android.widget.*;
import ru.myshows.adapters.FragmentAdapter;
import ru.myshows.api.MyShowsApi;
import ru.myshows.domain.Show;
import ru.myshows.domain.UserShow;
import ru.myshows.fragments.EpisodesFragment;
import ru.myshows.fragments.ShowFragment;
import ru.myshows.tasks.GetShowTask;
import ru.myshows.tasks.TaskListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 15.06.2011
 * Time: 17:53:42
 * To change this template use File | Settings | File Templates.
 */
public class ShowActivity extends MenuActivity implements TaskListener<Show> {


    private Integer showId;
    private MyShowsApi.STATUS watchStatus;
    private Double yoursRating;
    private String title;

    private ViewPager pager;
    private PagerTabStrip pagerTabStrip;
    private FragmentAdapter adapter;
    private LinearLayout indicatorLayout;
    private ProgressBar progress;
    private Bundle savedInstanceState;

    public ShowActivity() {
    }


    @Override
    protected int getContentViewId() {
        return R.layout.show_info;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;

        pager = (ViewPager) findViewById(R.id.pager);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setTabIndicatorColorResource(R.color.light_red);

        progress = (ProgressBar) findViewById(R.id.progress_show);
        indicatorLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        title = (String) getBundleValue(getIntent(), "title", null);
        if (title != null)
            getSupportActionBar().setTitle(title);

        showId = (Integer) getBundleValue(getIntent(), "showId", null);
        if (savedInstanceState != null) {
            showId = savedInstanceState.getInt("showId");
        }

        watchStatus = (MyShowsApi.STATUS) getBundleValue(getIntent(), "watchStatus", MyShowsApi.STATUS.remove);
        yoursRating = (Double) getBundleValue(getIntent(), "yoursRating", null);


        GetShowTask getShowTask = new GetShowTask(this);
        getShowTask.setTaskListener(this);
        getShowTask.execute(showId);

        setupDrawer();


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("showId", showId);
    }

    @Override
    public void onTaskComplete(Show result) {

        UserShow us = MyShows.getUserShow(showId);
        if (us != null)
            watchStatus = us.getWatchStatus();

        result.setWatchStatus(watchStatus);
        progress.setVisibility(View.GONE);
        indicatorLayout.setVisibility(View.VISIBLE);

        populateExternalLinkActions(result);

        List<Fragment> fragments = new LinkedList<Fragment>();
        fragments.add(new ShowFragment());
        fragments.add(new EpisodesFragment());

        Bundle args = new Bundle();
        args.putSerializable("show", result);
        if (yoursRating != null)
            args.putDouble("rating", yoursRating);

        adapter = new FragmentAdapter(this, getSupportFragmentManager(), fragments, args, R.array.show_titles );
        pager.setAdapter(adapter);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_refresh) {
            indicatorLayout.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            GetShowTask getShowTask = new GetShowTask(ShowActivity.this);
            getShowTask.setTaskListener(ShowActivity.this);
            getShowTask.execute(showId);
        }
        return true;
    }


    private Object getBundleValue(Intent intent, String key, Object defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
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


        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(linkAdapter, new ActionBar.OnNavigationListener() {
            @Override
            public boolean onNavigationItemSelected(int i, long l) {

                    return true;
            }
        });


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

            TextView site = (TextView) inflater.inflate(R.layout.external_link, parent, false);
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

}
