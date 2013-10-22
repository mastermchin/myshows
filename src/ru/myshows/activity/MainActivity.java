package ru.myshows.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import ru.myshows.adapters.FragmentAdapter;
import ru.myshows.fragments.LoginFragment;
import ru.myshows.fragments.NewEpisodesFragment;
import ru.myshows.fragments.NextEpisodesFragment;
import ru.myshows.tasks.Taskable;
import ru.myshows.util.Settings;

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
    private ListView menu;

    @Override
    protected int getContentViewId() {
        return R.layout.main;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = (ListView) findViewById(R.id.left_drawer);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
        pagerTabStrip.setTabIndicatorColorResource(R.color.light_red);

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

            if (Settings.getBoolean(Settings.FACEBOOK_IS_LOGGED_IN)){
                String token = Settings.getString(Settings.FACEBOOK_TOKEN);
                String userId = Settings.getString(Settings.FACEBOOK_USER_ID);
                return MyShows.client.loginSocial(OAuthActivity.OAUTH_FACEBOOK, token, userId, null);
            }

            if (Settings.getBoolean(Settings.VK_IS_LOGGED_IN)){
                String token = Settings.getString(Settings.VK_TOKEN);
                String userId = Settings.getString(Settings.VK_USER_ID);
                return MyShows.client.loginSocial(OAuthActivity.OAUTH_VK, token, userId, null);
            }

            if (Settings.getBoolean(Settings.TWITTER_IS_LOGGED_IN)){
                String token = Settings.getString(Settings.TWITTER_TOKEN);
                String userId = Settings.getString(Settings.TWITTER_USER_ID);
                String secret = Settings.getString(Settings.TWITTER_SECRET);
                return MyShows.client.loginSocial(OAuthActivity.OAUTH_TWITTER, token, userId, secret);
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
//                ImageView imageView = new ImageView(MainActivity.this);
//                imageView.setBackgroundResource(R.drawable.heisenberg);
//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);


                menu.setBackgroundResource(R.drawable.heisenberg);

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().add(R.id.main, new LoginFragment()).commitAllowingStateLoss();

            }
        }

    }


}
