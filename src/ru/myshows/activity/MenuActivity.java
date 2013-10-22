package ru.myshows.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ru.myshows.fragments.ShowsFragment;
import ru.myshows.util.Settings;

import java.util.List;

/**
 * Author: Georgy Gobozov
 * Date: 29.09.13
 */
public abstract class MenuActivity extends ActionBarActivity {


    private DrawerLayout mDrawerLayout;
    protected ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private String[] menu;
    private MenuAdapter adapter;
    protected SearchView search;

   protected abstract int getContentViewId();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        setContentView(getContentViewId());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        getSupportActionBar().setIcon(R.drawable.ic_list_logo);

        BitmapDrawable bg = (BitmapDrawable) getResources().getDrawable(R.drawable.stripe_red);
        bg.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        getSupportActionBar().setBackgroundDrawable(bg);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
                if (adapter != null)
                    adapter.notifyDataSetChanged();
            }
        };

    }

    protected void setupDrawer() {

        if (MyShows.isLoggedIn) {
            menu = getResources().getStringArray(R.array.left_menu);
            // set a custom shadow that overlays the main content when the drawer opens
            mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            adapter = new MenuAdapter(this, R.layout.drawer_list_item, menu);
            mDrawerList.setAdapter(adapter);
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

            getSupportActionBar().setIcon(R.drawable.ic_list_logo);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);


            mDrawerLayout.setDrawerListener(mDrawerToggle);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_search).setVisible(MyShows.isLoggedIn);
        menu.findItem(R.id.action_refresh).setVisible(MyShows.isLoggedIn);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        search = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean queryTextFocused) {
                if (!queryTextFocused) {
                    MenuItemCompat.collapseActionView(searchItem);
                    search.setQuery("", false);
                }
            }
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {
            case R.id.action_search:
                search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        // search here
                        Intent intent = new Intent(MenuActivity.this, ShowsActivity.class);
                        intent.putExtra("action", ShowsFragment.SHOWS_SEARCH);
                        intent.putExtra("search", s);
                        startActivity(intent);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                });
                SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
                search.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                break;

        }
        //return true;

        return super.onOptionsItemSelected(item);
    }


    /* The click listner for ListView in the navigation drawer */
    protected class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            mDrawerLayout.closeDrawer(mDrawerList);
            mDrawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Context context = MenuActivity.this;
                    Intent intent;
                        switch (position) {
                            // Episodes
                            case 0:
                                if (!(MenuActivity.this instanceof MainActivity)) {
                                    if (getActivityDepth() == 2) {
                                        finish();
                                    } else {
                                        intent = new Intent(context, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                }
                                break;
                            //profile
                            case 1:
                                intent = new Intent(context, ProfileActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                break;
                            //News
                            case 2:
                                intent = new Intent(context, NewsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                                break;
                            //Shows Rating
                            case 3:
                                intent = new Intent(context, ShowsActivity.class);
                                intent.putExtra("action", ShowsFragment.SHOWS_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                break;
                            //Favourites

                            case 4:
                                final AlertDialog alert;
                                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this)
                                        .setTitle(R.string.request_exit)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                // clear all preferences
                                                Settings.getPreferences().edit().clear().commit();

                                                MyShows.isLoggedIn = false;
                                                MyShows.invalidateUserData();
                                                finish();
                                                startActivity(new Intent(MenuActivity.this, MainActivity.class));
                                            }
                                        })
                                        .setNegativeButton(R.string.no, null);
                                alert = builder.create();
                                alert.show();
                                break;
                        }


                }
            }, 200);
        }
    }

    private int getActivityDepth() {

        Context context = this.getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(10);
        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (task.baseActivity.getClassName().equals("ru.myshows.activity.MainActivity")) {
//                try {
//                    Intent launchTopIntent = new Intent(context, Class.forName(task.topActivity.getClassName()));
//                    context.startActivity(launchTopIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                } catch (ClassNotFoundException ex) {
//                    ex.printStackTrace();;
//                }
                int depth = task.numRunning;
                Log.d("Autokadabra", "Depth = " + depth);
                return depth;
            }
        }
        return 0;

    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (mDrawerToggle != null)
            mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        if (mDrawerToggle != null)
            mDrawerToggle.onConfigurationChanged(newConfig);
    }


    public class MenuAdapter extends ArrayAdapter<String> {

        private LayoutInflater inflater;

        public MenuAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            inflater = LayoutInflater.from(context);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.drawer_list_item, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.count = (TextView) convertView.findViewById(R.id.count);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }



                switch (position) {
                    case 0:
                        holder.image.setImageResource(R.drawable.ic_action_film2);
                        break;
                    // profile
                    case 1:
                        holder.image.setImageResource(R.drawable.ic_action_social_person);
                        break;
                    // news
                    case 2:
                        holder.image.setImageResource(R.drawable.ic_action_device_access_storage_1);
                        break;
                    // rating
                    case 3:
                        holder.image.setImageResource(R.drawable.ic_rating_important);
                        break;
                    //logout
                    case 4:
                        holder.image.setImageResource(R.drawable.ic_action_gnome_session_logout);
                        break;
                }

            holder.title.setText(getItem(position));

            return convertView;

        }

        protected class ViewHolder {
            protected ImageView image;
            protected TextView title;
            protected TextView count;
        }
    }



}
