package ru.myshows.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TitlePageIndicator;
import ru.myshows.prefs.Settings;
import ru.myshows.util.CustomExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 12.05.2011
 * Time: 15:47:52
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends FragmentActivity {

    MyShows app;
    ViewPager pager;
    PageIndicator indicator;
    TabsAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler("/sdcard/MyShows", null));

        adapter = new TabsAdapter(getSupportFragmentManager());
        pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);
        indicator = (TitlePageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);

        initTabs();

     }




    private void initTabs() {
        if (Settings.getBoolean(Settings.IS_LOGGED_IN))
            getPrivateTabs();
        else
            //new LoginTask(this).execute(Settings.getString(Settings.KEY_LOGIN), Settings.getString(Settings.KEY_PASSWORD));
            getPublicTabs();

    }


    private void getPrivateTabs() {
        Log.d("MyShows", "get private tabs");
        adapter.addFragment(new ShowsFragment("top"), getResources().getString(R.string.tab_shows_title) );
        adapter.addFragment(new NewEpisodesFragment(), getResources().getString(R.string.tab_new) );
        adapter.addFragment(new NewsFragment(), getResources().getString(R.string.tab_news_title) );
        adapter.addFragment(new ProfileFragment(Settings.getString(Settings.KEY_LOGIN)), getResources().getString(R.string.tab_profile_title) );
        adapter.addFragment(new SearchFragment(), getResources().getString(R.string.tab_search_title));
    }

    private void getPublicTabs() {
        Log.d("MyShows", "get public tabs");
        adapter.addFragment(new SearchFragment(), getResources().getString(R.string.tab_search_title));
        adapter.addFragment(new LoginFragment(),  getResources().getString(R.string.tab_profile_title));
    }



    private Object getBundleValue(Intent intent, String key, Object defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key);
    }



//    private class LoginTask extends AsyncTask {
//        private ProgressDialog dialog;
//
//        private LoginTask(Context context) {
//            this.dialog = new ProgressDialog(context);
//        }
//
//        @Override
//        protected void onPreExecute() {
//            this.dialog.setMessage(getResources().getString(R.string.loading));
//            this.dialog.show();
//
//        }
//
//        @Override
//        protected Object doInBackground(Object... objects) {
//            Boolean result = false;
//            String login = (String) objects[0];
//            String pass = (String) objects[1];
//            if (login != null && pass != null)
//                result = MyShows.getClient().login(login, pass);
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Object result) {
//            if (this.dialog.isShowing())
//                this.dialog.dismiss();
//
//            if ((Boolean) result)
//                getPrivateTabs();
//            else
//                getPublicTabs();
//
//        }
//
//    }


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
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
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

}
