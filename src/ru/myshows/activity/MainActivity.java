package ru.myshows.activity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import ru.myshows.adapters.FragmentAdapter;
import ru.myshows.fragments.LoginFragment;
import ru.myshows.fragments.NewEpisodesFragment;
import ru.myshows.fragments.NextEpisodesFragment;
import ru.myshows.tasks.Taskable;
import ru.myshows.util.Settings;
import ru.myshows.util.TwitterUtil;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: Georgy Gobozov
 * @Date: 12.05.2011
 */
public class MainActivity extends MenuActivity {

    private ViewPager pager;
    private PagerTabStrip pagerTabStrip;
    private FragmentAdapter adapter;

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

        Uri uri = getIntent().getData();

        boolean twitterIsLoggedIn = Settings.getBoolean(Settings.TWITTER_IS_LOGGED_IN);
        boolean facebookIsLoggedIn = Settings.getBoolean(Settings.FACEBOOK_IS_LOGGED_IN);

//       // twitter oauth
//        if (uri != null && uri.toString().startsWith(Settings.TWITTER_CALLBACK_URL) || twitterIsLoggedIn ) {
//
//            String arg = twitterIsLoggedIn ? null :  uri.getQueryParameter(Settings.TWITTER_OAUTH_VERIFIER);
//            new TwitterGetAccessTokenTask().execute(arg);
//
//         // facebook oauth
//        }else if (facebookIsLoggedIn){
//
//
//
//
//        }else {
//            new LoginTask().execute();
//        }

        new LoginTask().execute();

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

                List<Fragment> fragments = new LinkedList<Fragment>();
                fragments.add(new NewEpisodesFragment());
                fragments.add(new NextEpisodesFragment());

                adapter = new FragmentAdapter(MainActivity.this, getSupportFragmentManager(), fragments, null, R.array.episodes_titles);
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
