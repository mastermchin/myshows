package ru.myshows.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import ru.myshows.fragments.ProfileFragment;
import ru.myshows.tasks.GetProfileTask;


public class ProfileActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String login = (String) getBundleValue(getIntent(), "login", null);

        if (login != null) {

            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            getSupportActionBar().setIcon(R.drawable.ic_list_logo);
            getSupportActionBar().setTitle(login);


            if (savedInstanceState == null) {
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(android.R.id.content, profileFragment).commit();
                GetProfileTask getProfileTask = new GetProfileTask(this);
                getProfileTask.setTaskListener(profileFragment);
                getProfileTask.execute(login);
            }
        } else {
            finish();
        }

    }

    private Object getBundleValue(Intent intent, String key, Object defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}
