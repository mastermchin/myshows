package ru.myshows.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import ru.myshows.fragments.ShowsFragment;
import ru.myshows.tasks.GetShowsTask;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 10.07.12
 * Time: 13:36
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends SherlockFragmentActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.stripe_red);
        bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(bg);

        String search = (String ) getBundleValue(getIntent(), "search", null);
        ShowsFragment showsFragment = (ShowsFragment) getSupportFragmentManager().findFragmentById(R.id.shows_fragment);
        GetShowsTask task = null;

        if (search.equals("top")){
            task = new GetShowsTask(this, GetShowsTask.SHOWS_TOP);
            showsFragment.setAction(GetShowsTask.SHOWS_TOP);
        }

        if (search.equals("all")){
            task = new GetShowsTask(this, GetShowsTask.SHOWS_ALL);
            showsFragment.setAction(GetShowsTask.SHOWS_ALL);
        }

        if (task != null){
            task.setShowsLoadingListener(showsFragment);
            task.execute();
        }



    }

    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {

        menu.add(0, 1, 1, "Refresh").setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 2, 2, "Settings").setIcon(R.drawable.ic_action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 3, 3, "Search").setIcon(R.drawable.ic_action_search).setActionView(R.layout.action_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return super.onCreateOptionsMenu(menu);
    }


    private Object getBundleValue(Intent intent, String key, Object defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key);
    }
}