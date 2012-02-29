package ru.myshows.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import ru.myshows.client.MyShowsClient;
import ru.myshows.prefs.Prefs;
import ru.myshows.util.CustomExceptionHandler;
import ru.myshows.util.MyShowsUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: GGobozov
 * Date: 12.05.2011
 * Time: 15:47:52
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends TabActivity {

    public static final int TAB_SHOWS = 0;
    public static final int TAB_NEWS = 2;
    public static final int TAB_PROFILE = 3;
    public static final int TAB_SEARCH = 4;
    public static final int TAB_LOGIN = 5;
    public static final int TAB_NEW = 1;
    MyShows app;

    /**
     * Called when the activity is first created.
     */

    private TabHost tabHost;
    private MyShowsClient client = MyShowsClient.getInstance();
    private Map<Integer, TabHost.TabSpec> tabs = new HashMap<Integer, TabHost.TabSpec>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // MyShowsUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler("/sdcard/MyShows", null));
        app = (MyShows) getApplication();
        initTabs();
    }


    private int getThemeAttribute(int attr) {
        TypedValue typedvalueattr = new TypedValue();
        getTheme().resolveAttribute(attr, typedvalueattr, true);
        return typedvalueattr.resourceId;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void initTabs() {
        tabHost = getTabHost();
        tabHost.setCurrentTab(0);
        tabHost.clearAllTabs();

        if (app.isLoggedIn())
            getPrivateTabs();
        else
            new LoginTask(this).execute(Prefs.getStringPrefs(this, Prefs.KEY_LOGIN), Prefs.getStringPrefs(this, Prefs.KEY_PASSWORD));

    }


    private void getPrivateTabs() {
        tabHost.clearAllTabs();
        addTab(R.string.tab_shows_title, R.drawable.dark_tab_shows, ActivityStack.class, ShowsActivity.class, TAB_SHOWS);
        addTab(R.string.tab_new, R.drawable.dark_tab_new_episodes, NewEpisodesActivity.class, null, TAB_NEW);
        addTab(R.string.tab_news_title, R.drawable.dark_tab_news, NewsActivity.class, null, TAB_NEWS);
        addTab(R.string.tab_profile_title, R.drawable.dark_tab_profile, ProfileActivity.class, null, TAB_PROFILE);
        addTab(R.string.tab_search_title, R.drawable.dark_tab_search, ActivityStack.class, SearchActivity.class, TAB_SEARCH);
    }

    private void getPublicTabs() {
        tabHost.clearAllTabs();
        addTab(R.string.tab_search_title, R.drawable.dark_tab_search, ActivityStack.class, SearchActivity.class, TAB_SEARCH);
        addTab(R.string.tab_login_title, R.drawable.dark_tab_login, LoginActivity.class, null, TAB_LOGIN);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("MainActivity", "OnNewIntent!");
        // if user name clicked in NewsActivity
        if (getBundleValue(intent, "login", null) != null) {
            String login = getBundleValue(intent, "login", null).toString();
            Intent newIntent = new Intent(this, ProfileActivity.class);
            newIntent.putExtra("login", login);
            TabHost.TabSpec spec = tabs.get(TAB_PROFILE);
            spec.setContent(newIntent);
            switchTab(TAB_PROFILE);

            // if show clicked in NewsActivity
        } else if (getBundleValue(intent, "showId", null) != null) {
            Integer showId = (Integer) getBundleValue(intent, "showId", null);
            Intent newIntent = new Intent(this, ShowActivity.class);
            newIntent.putExtra("showId", showId);
            ActivityStack activityStack = (ActivityStack) getLocalActivityManager().getActivity("tab" + R.string.tab_shows_title);
            activityStack.push("test", newIntent);
            switchTab(TAB_SHOWS);

        } else {
            initTabs();
        }

    }

    private void addTab(int labelId, int drawableId, Class activitClass, Class defaultStackClass, Integer tab) {
        Intent intent = new Intent(this, activitClass);
        if (defaultStackClass != null) {
            intent.putExtra("defaultStackClass", defaultStackClass);
        }

        TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);

        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_indicator, getTabWidget(), false);

        TextView title = (TextView) tabIndicator.findViewById(R.id.title);
        title.setText(labelId);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
        icon.setImageResource(drawableId);

        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabs.put(tab, spec);
        tabHost.addTab(spec);


    }


    public void switchTab(int tab) {
        tabHost.setCurrentTab(tab);
    }

    private Object getBundleValue(Intent intent, String key, Object defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key);
    }



    private class LoginTask extends AsyncTask {
        private Context context;
        private ProgressDialog dialog;

        private LoginTask(Context context) {
            this.context = context;
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage(getResources().getString(R.string.loading));
            this.dialog.show();

        }

        @Override
        protected Object doInBackground(Object... objects) {
            Boolean result = false;
            String login = (String) objects[0];
            String pass = (String) objects[1];
            if (login != null && pass != null)
                result = client.login(login, pass);
            return result;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (this.dialog.isShowing()) this.dialog.dismiss();
            app.setLoggedIn((Boolean) result);
            if (app.isLoggedIn()) getPrivateTabs();
            else getPublicTabs();

        }

    }

}
