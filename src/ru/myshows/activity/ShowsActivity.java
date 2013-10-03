package ru.myshows.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import ru.myshows.domain.Searchable;
import ru.myshows.fragments.ShowsFragment;
import ru.myshows.tasks.GetShowsTask;
import ru.myshows.tasks.Taskable;

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
        pager.setVisibility(View.GONE);



        Bundle args = new Bundle();
        args.putInt("action", getIntent().getIntExtra("action", GetShowsTask.SHOWS_USER));
        Fragment showsFragment = new ShowsFragment();
        showsFragment.setArguments(args);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main, showsFragment, "shows").commitAllowingStateLoss();

        setupDrawer();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.action_search:
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        // search here
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        Fragment fragment = getSupportFragmentManager().findFragmentByTag("shows");
                        if (fragment instanceof Searchable) {
                            ((Searchable) fragment).getAdapter().getFilter().filter(s);
                        }
                        return false;
                    }
                });
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                break;
            case R.id.action_refresh:
                Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("shows");
                if (currentFragment != null)
                    ((Taskable) currentFragment).executeUpdateTask();
                break;
        }



        return true;
    }






}
