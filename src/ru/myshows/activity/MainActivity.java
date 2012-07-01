package ru.myshows.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.EditText;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;
import ru.myshows.adapters.SectionedAdapter;
import ru.myshows.fragments.*;
import ru.myshows.tasks.GetShowsTask;
import ru.myshows.util.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 12.05.2011
 * Time: 15:47:52
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends SherlockFragmentActivity {

    private static final int TAB_SHOWS = 0;
    private static final int TAB_NEW_EPISODES = 1;
    private static final int TAB_NEWS = 2;
    private static final int TAB_PROFILE = 3;
    private static final int TAB_SEARCH = 4;
    private static final int TAB_LOGIN = 5;


    private ViewPager pager;
    private TitlePageIndicator indicator;
    private TabsAdapter adapter;
    private EditText search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler("/sdcard/MyShows", null));

        adapter = new TabsAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        indicator.setTypeface(MyShows.font);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                System.out.println("position = " + position);
                switch (position) {
                    case TAB_SHOWS:
                        GetShowsTask task = new GetShowsTask(MainActivity.this, false, GetShowsTask.SHOWS_USER);
                        task.setShowsLoadingListener((GetShowsTask.ShowsLoadingListener)adapter.getItem(position));
                        task.execute();
                        break;
                    case TAB_NEW_EPISODES:

                }

            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        new LoginTask().execute();

    }


    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        menu.add(0, 1, 1, "Refresh").setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 2, "Settings").setIcon(R.drawable.ic_action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 3, 3, "Search").setIcon(R.drawable.ic_action_search).setActionView(R.layout.action_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                int currentItem = pager.getCurrentItem();

                //((RadioFragment) mAdapter.getItem(mPager.getCurrentItem())).updateChannels();
                break;
            case 2:
                startActivity(new Intent(this, SettingsAcrivity.class));
                break;
            case 3:
                search = (EditText) item.getActionView();
                //search.addTextChangedListener(filterTextWatcher);
                break;
        }
        return true;
    }

    private void getPrivateTabs() {

        adapter.addFragment(new ShowsFragment(ShowsFragment.SHOWS_USER), getResources().getString(R.string.tab_shows_title));
        adapter.addFragment(new NewEpisodesFragment(), getResources().getString(R.string.tab_new));
        adapter.addFragment(new NewsFragment(), getResources().getString(R.string.tab_news_title));
        adapter.addFragment(new ProfileFragment(Settings.getString(Settings.KEY_LOGIN)), getResources().getString(R.string.tab_profile_title));
        adapter.addFragment(new SearchFragment(), getResources().getString(R.string.tab_search_title));

        // fire first task manually
        GetShowsTask task = new GetShowsTask(MainActivity.this, false, GetShowsTask.SHOWS_USER);
        task.setShowsLoadingListener((GetShowsTask.ShowsLoadingListener)adapter.getItem(0));
        task.execute();
    }

    private void getPublicTabs() {
        adapter.addFragment(new SearchFragment(), getResources().getString(R.string.tab_search_title));
        adapter.addFragment(new LoginFragment(), getResources().getString(R.string.tab_login_title));
    }


    public class TabsAdapter extends FragmentPagerAdapter {

        public List<Fragment> fragments;
        public List<String> titles;

        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            if (fragments == null) return 0;
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title) {
            if (fragments == null)
                fragments = new ArrayList<Fragment>();
            if (titles == null)
                titles = new ArrayList<String>();
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    private class LoginTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            if (MyShows.isLoggedIn()) return true;
            if (Settings.getBoolean(Settings.KEY_LOGGED_IN)) {
                String login = Settings.getString(Settings.KEY_LOGIN);
                String pass = Settings.getString(Settings.KEY_PASSWORD);
                return MyShows.getClient().login(login, pass);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) getPrivateTabs();
            else getPublicTabs();
            indicator.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
        }

    }


}
