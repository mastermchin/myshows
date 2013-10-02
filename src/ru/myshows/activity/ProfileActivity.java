package ru.myshows.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.MenuItem;
import android.view.View;
import ru.myshows.adapters.EpisodesAdapter;
import ru.myshows.adapters.ProfileAdapter;
import ru.myshows.fragments.ProfileFragment;
import ru.myshows.tasks.GetProfileTask;
import ru.myshows.tasks.GetShowsTask;
import ru.myshows.util.Settings;


public class ProfileActivity extends MenuActivity {

    private ViewPager pager;
    private PagerTabStrip pagerTabStrip;
    private ProfileAdapter adapter;
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



//        String login = (String) getBundleValue(getIntent(), "login", Settings.getString(Settings.KEY_LOGIN));
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


        adapter = new ProfileAdapter(ProfileActivity.this, getSupportFragmentManager());
        pager.setAdapter(adapter);
        setupDrawer();
    }

//    private Object getBundleValue(Intent intent, String key, Object defaultValue) {
//        if (intent == null) return defaultValue;
//        if (intent.getExtras() == null) return defaultValue;
//        if (intent.getExtras().get(key) == null) return defaultValue;
//        return intent.getExtras().get(key);
//    }


}
