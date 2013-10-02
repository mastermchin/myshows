package ru.myshows.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import ru.myshows.adapters.FragmentAdapter;
import ru.myshows.fragments.ProfileFragment;
import ru.myshows.fragments.ShowsFragment;
import ru.myshows.tasks.Taskable;
import ru.myshows.util.Settings;

import java.util.LinkedList;
import java.util.List;


public class ProfileActivity extends MenuActivity {

    private ViewPager pager;
    private PagerTabStrip pagerTabStrip;
    private FragmentAdapter adapter;
    //private SearchView search;

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


        String login = (String) getBundleValue(getIntent(), "login", Settings.getString(Settings.KEY_LOGIN));


//
//        if (login != null) {
//
//            getSupportActionBar().setTitle(login);
//
//            Bundle args = new Bundle();
//            args.putString("login", login);
//
//            ProfileFragment profileFragment = new ProfileFragment();
//            profileFragment.setArguments(args);
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.add(R.id.main, profileFragment).commit();
////            GetProfileTask getProfileTask = new GetProfileTask(this);
////            getProfileTask.setTaskListener(profileFragment);
////            getProfileTask.execute(login);
//            setupDrawer();
//        }


        //adapter = new ProfileAdapter(ProfileActivity.this, getSupportFragmentManager());

         boolean isOwner = login.equalsIgnoreCase(Settings.getString(Settings.KEY_LOGIN));

            List<Fragment> fragments = new LinkedList<Fragment>();
            if (isOwner)
                fragments.add(new ShowsFragment());
            fragments.add(new ProfileFragment());

            Bundle args = new Bundle();
            if (isOwner)
                args.putInt("action", ShowsFragment.SHOWS_USER);
            args.putString("login", login);

            adapter = new FragmentAdapter(ProfileActivity.this, getSupportFragmentManager(), fragments, args, isOwner ? R.array.profile_titles : R.array.stats_titles);
            pager.setAdapter(adapter);
            setupDrawer();
    }

    private Object getBundleValue(Intent intent, String key, Object defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.action_refresh) {
            int position = pager.getCurrentItem();
            Fragment currentFragment = getFragment(position);
            if (currentFragment != null)
                ((Taskable) currentFragment).executeUpdateTask();
        }
        return true;
    }

    private Fragment getFragment(int position) {
        return getSupportFragmentManager().findFragmentByTag(getFragmentTag(position));
    }

    private String getFragmentTag(int position) {
        return "android:switcher:" + R.id.pager + ":" + position;
    }
}
