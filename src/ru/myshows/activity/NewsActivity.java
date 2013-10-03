package ru.myshows.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import ru.myshows.fragments.NewsFragment;
import ru.myshows.fragments.ShowsFragment;
import ru.myshows.tasks.GetShowsTask;
import ru.myshows.tasks.Taskable;

/**
 * @Author: Georgy Gobozov
 * @Date: 12.05.2011
 */
public class NewsActivity extends MenuActivity {

    private ViewPager pager;

    @Override
    protected int getContentViewId() {
        return R.layout.main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main, new NewsFragment(), "news").commitAllowingStateLoss();
        setupDrawer();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_refresh) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("news");
            if (currentFragment != null)
                ((Taskable) currentFragment).executeUpdateTask();
        }
        return true;
    }






}
