package ru.myshows.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import ru.myshows.fragments.*;
import ru.myshows.prefs.Settings;

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

    private ViewPager pager;
    private PageIndicator indicator;
    private TabsAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       // Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler("/sdcard/MyShows", null));

        adapter = new TabsAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        indicator = (TitlePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {}
            @Override
            public void onPageScrollStateChanged(int i) {}
        });


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

      //  initTabs();
        new LoginTask().execute();

    }


    private void initTabs() {
        if (Settings.getBoolean(Settings.KEY_LOGGED_IN))
            getPrivateTabs();
        else
            getPublicTabs();

    }


    private void getPrivateTabs() {
        adapter.addFragment(new ShowsFragment(), getResources().getString(R.string.tab_shows_title));
        adapter.addFragment(new NewEpisodesFragment(), getResources().getString(R.string.tab_new));
        adapter.addFragment(new NewsFragment(), getResources().getString(R.string.tab_news_title));
        adapter.addFragment(new ProfileFragment(Settings.getString(Settings.KEY_LOGIN)), getResources().getString(R.string.tab_profile_title));
        adapter.addFragment(new SearchFragment(), getResources().getString(R.string.tab_search_title));
    }

    private void getPublicTabs() {
        adapter.addFragment(new SearchFragment(), getResources().getString(R.string.tab_search_title));
        adapter.addFragment(new LoginFragment(), getResources().getString(R.string.tab_login_title));
        adapter.notifyDataSetChanged();
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
            if (Settings.getBoolean(Settings.KEY_LOGGED_IN)){
                String login = Settings.getString(Settings.KEY_LOGIN);
                String pass = Settings.getString(Settings.KEY_PASSWORD);
                return MyShows.getClient().login(login, pass);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result)  getPrivateTabs();
            else         getPublicTabs();
            indicator.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
        }

    }





}
