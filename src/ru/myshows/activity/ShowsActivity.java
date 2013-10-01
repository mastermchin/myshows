package ru.myshows.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import ru.myshows.adapters.EpisodesAdapter;
import ru.myshows.fragments.LoginFragment;
import ru.myshows.fragments.ShowsFragment;
import ru.myshows.tasks.GetShowsTask;
import ru.myshows.tasks.Taskable;
import ru.myshows.util.Settings;

/**
 * @Author: Georgy Gobozov
 * @Date: 12.05.2011
 */
public class ShowsActivity extends MenuActivity {

    private ViewPager pager;

    @Override
    protected int getContentViewId() {
        return R.layout.main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pager = (ViewPager) findViewById(R.id.pager);


        Bundle args = new Bundle();
        args.putInt("action", getIntent().getIntExtra("action", GetShowsTask.SHOWS_USER));
        Fragment showsFragment = new ShowsFragment();
        showsFragment.setArguments(args);

        pager.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main, showsFragment).commitAllowingStateLoss();

        setupDrawer();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1, R.string.menu_update).setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(1).setVisible(MyShows.isLoggedIn);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case 1:
                int position = pager.getCurrentItem();
                if (!MyShows.isLoggedIn)
                    break;
                Fragment currentFragment = getFragment(position);
                if (currentFragment != null)
                    ((Taskable) currentFragment).executeUpdateTask();

                break;

        }
        return true;
    }

//    private TextWatcher filterTextWatcher = new TextWatcher() {
//        public void afterTextChanged(Editable s) {
//        }
//
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        }
//
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            Fragment fragment = getFragment(pager.getCurrentItem());
//            if (fragment instanceof Searchable) {
//                ((Searchable) fragment).getAdapter().getFilter().filter(s);
//            }
//        }
//
//    };
//

    private Fragment getFragment(int position) {
        return  getSupportFragmentManager().findFragmentByTag(getFragmentTag(position));
    }
//
    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.pager + ":" + position;
    }




}
