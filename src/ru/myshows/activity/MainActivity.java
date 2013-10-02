package ru.myshows.activity;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import ru.myshows.adapters.EpisodesAdapter;
import ru.myshows.adapters.TabsAdapter;
import ru.myshows.domain.Searchable;
import ru.myshows.fragments.*;
import ru.myshows.tasks.Taskable;
import ru.myshows.util.Settings;

import java.util.List;

/**
 * @Author: Georgy Gobozov
 * @Date: 12.05.2011
 */
public class MainActivity extends MenuActivity {

    private ViewPager pager;
    private PagerTabStrip pagerTabStrip;
    private EpisodesAdapter adapter;
    private SearchView search;

    @Override
    protected int getContentViewId() {
        return R.layout.main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setTabIndicatorColorResource(R.color.light_red);

        new LoginTask().execute();

    }


//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.action_search).setVisible(MyShows.isLoggedIn);
//        menu.findItem(R.id.action_refresh).setVisible(MyShows.isLoggedIn);
//
//        final MenuItem searchItem = menu.findItem(R.id.action_search);
//        search = (SearchView) MenuItemCompat.getActionView(searchItem);
//        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean queryTextFocused) {
//                if (!queryTextFocused) {
//                    MenuItemCompat.collapseActionView(searchItem);
//                    search.setQuery("", false);
//                }
//            }
//        });
//
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);
//        switch (item.getItemId()) {
//            case R.id.action_refresh:
//                int position = pager.getCurrentItem();
//                Fragment currentFragment = getFragment(position);
//                if (currentFragment != null)
//                    ((Taskable) currentFragment).executeUpdateTask();
//                break;
//            case R.id.action_search:
//                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//                    @Override
//                    public boolean onQueryTextSubmit(String s) {
//                        // search here
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onQueryTextChange(String s) {
//                        return false;
//                    }
//                });
//                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//                search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//                break;
//
//        }
//        return true;
//    }

//
//    private Fragment getFragment(int position) {
//        return  getSupportFragmentManager().findFragmentByTag(getFragmentTag(position));
//    }
//
//    private String getFragmentTag(int position) {
//        return "android:switcher:" + R.id.pager + ":" + position;
//    }


    private class LoginTask extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... objects) {
            if (MyShows.isLoggedIn) return true;
            // if credentials exists in preferences, try to login
            if (Settings.getBoolean(Settings.KEY_LOGGED_IN)) {
                String login = Settings.getString(Settings.KEY_LOGIN);
                String pass = Settings.getString(Settings.KEY_PASSWORD);
                return MyShows.client.login(login, pass);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            supportInvalidateOptionsMenu();
            if (result) {
                adapter = new EpisodesAdapter(MainActivity.this, getSupportFragmentManager());
                pager.setAdapter(adapter);
                setupDrawer();
            } else {
                pager.setVisibility(View.GONE);
                FragmentManager fragmentManager = getSupportFragmentManager();
                  fragmentManager.beginTransaction().add(R.id.main, new LoginFragment()).commitAllowingStateLoss();

            }
        }

    }

}
