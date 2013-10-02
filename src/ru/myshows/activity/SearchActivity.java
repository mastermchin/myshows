package ru.myshows.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import ru.myshows.fragments.ShowsFragment;
import ru.myshows.tasks.GetShowsTask;

/**
 * Created with IntelliJ IDEA.
 * User: GGobozov
 * Date: 10.07.12
 * Time: 13:36
 * To change this template use File | Settings | File Templates.
 */
public class SearchActivity extends ActionBarActivity {

    private String search;
    private EditText searchField;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

        search = (String) getBundleValue(getIntent(), "search", null);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getSupportActionBar().setIcon(R.drawable.ic_list_logo);
        getSupportActionBar().setTitle(getResources().getString(R.string.search) + ": " + search);

        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.stripe_red);
        bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(bg);

        if (search != null)
            executeSearch(search);

    }

    private void executeSearch(String search) {

        ShowsFragment showsFragment = (ShowsFragment) getSupportFragmentManager().findFragmentById(R.id.shows_fragment);
        GetShowsTask task = null;

        if (search.equals("top")) {
            task = new GetShowsTask(this, GetShowsTask.SHOWS_TOP);
            showsFragment.setAction(GetShowsTask.SHOWS_TOP);
            task.setTaskListener(showsFragment);
            task.execute();
        }

        if (search.equals("all")) {
            task = new GetShowsTask(this, GetShowsTask.SHOWS_ALL);
            showsFragment.setAction(GetShowsTask.SHOWS_ALL);
            task.setTaskListener(showsFragment);
            task.execute();
        }

        if (task == null) {
            task = new GetShowsTask(this, GetShowsTask.SHOWS_SEARCH);
            showsFragment.setAction(GetShowsTask.SHOWS_SEARCH);
            task.setTaskListener(showsFragment);
            task.execute(search);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0, 1, 1, R.string.menu_update).setIcon(R.drawable.ic_navigation_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        if (MyShows.isLoggedIn)
            menu.add(0, 2, 2, R.string.menu_settings).setIcon(R.drawable.ic_action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, 3, 3, R.string.menu_search).setIcon(R.drawable.ic_action_search).setActionView(R.layout.action_search).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case 1:
                if (search != null) {
                    finish();
                    Intent intent = new Intent();
                    intent.putExtra("search", search);
                    intent.setClass(this, SearchActivity.class);
                    startActivity(intent);
                }
                break;
            case 2:
               // startActivity(new Intent(this, SettingsAcrivity.class));
                break;
            case 3:
                searchField = (EditText) item.getActionView();
                searchField.addTextChangedListener(filterTextWatcher);
                searchField.requestFocus();
                InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                break;
        }
        return true;
    }


    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ShowsFragment showsFragment = (ShowsFragment) getSupportFragmentManager().findFragmentById(R.id.shows_fragment);
            showsFragment.getAdapter().getFilter().filter(s);
        }

    };

    private Object getBundleValue(Intent intent, String key, Object defaultValue) {
        if (intent == null) return defaultValue;
        if (intent.getExtras() == null) return defaultValue;
        if (intent.getExtras().get(key) == null) return defaultValue;
        return intent.getExtras().get(key);
    }
}