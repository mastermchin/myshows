package ru.myshows.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import java.util.List;

/**
 * Author: Georgy Gobozov
 * Date: 29.09.13
 */
public abstract class MenuActivity extends SherlockFragmentActivity {


    private DrawerLayout mDrawerLayout;
    protected ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] menu;
    private MenuAdapter adapter;

    protected abstract int getContentViewId();


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setupDrawer();
    }

    protected void setupDrawer() {
        menu = getResources().getStringArray(R.array.left_menu_anonymous);

        if (MyShows.isLoggedIn)
            menu = getResources().getStringArray(R.array.left_menu);


        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        adapter = new MenuAdapter(this, R.layout.drawer_list_item, menu);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setIcon(R.drawable.ic_list_logo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                //getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

                if (adapter != null)
                    adapter.notifyDataSetChanged();

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }


    //    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(getMenuItem(item))) {
            return true;
        }
//        switch (item.getItemId()) {
//
//
//
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        //MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* The click listner for ListView in the navigation drawer */
    protected class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            mDrawerLayout.closeDrawer(mDrawerList);
            mDrawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    Context context = MenuActivity.this;
//                    Intent intent;
//                    if (MyShows.isLoggedIn) {
//
//                        switch (position) {
//                            // main page
//                            case 0:
//                                if (!(MenuActivity.this instanceof MainActivity)) {
//                                    if (getActivityDepth() == 2) {
//                                        finish();
//                                    } else {
//                                        intent = new Intent(context, MainActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivity(intent);
//                                    }
//                                }
//                                break;
//                            //profile
//                            case 1:
//                                intent = new Intent(context, ProfileActivity.class);
//                                intent.putExtra("profileUrl", Settings.getString(Settings.KEY_PROFILE_LINK));
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                break;
//                            //Tracker
//                            case 2:
//                                intent = new Intent(context, TrackerActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                break;
//                            //Messages
//                            case 3:
//                                intent = new Intent(context, DiscussionsActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                break;
//                            //Favourites
//                            case 4:
//                                intent = new Intent(context, PostsActivity.class);
//                                intent.putExtra("url", " http://autokadabra.ru/shouts/favorites");
//                                intent.putExtra("title", getResources().getString(R.string.favourites));
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                break;
//                            //PDD
//                            case 5:
//                                intent = new Intent(context, TextListActivity.class);
//                                intent.putExtra("title", getResources().getString(R.string.pdds));
//                                intent.putExtra("type", TextListActivity.CATEGORY_PDD);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                break;
//                            //sings
//                            case 6:
//                                intent = new Intent(context, TextListActivity.class);
//                                intent.putExtra("title", getResources().getString(R.string.signs));
//                                intent.putExtra("type", TextListActivity.CATEGORY_SIGNS);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                break;
//
//                            //KOAP
//                            case 7:
//                                intent = new Intent(context, TextListActivity.class);
//                                intent.putExtra("title", getResources().getString(R.string.koaps));
//                                intent.putExtra("type", TextListActivity.CATEGORY_KOAP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                break;
//                            //exit
//                            case 8:
//                                CookieSyncManager.createInstance(context);
//                                CookieManager.getInstance().removeAllCookie();
//                                Settings.setString(Settings.KEY_COOKIE_VALUE, "");
//                                Autokadabra.isLoggedIn = false;
//                                finish();
//
//                                intent = new Intent(context, MainActivity.class);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(intent);
//                                break;
//                        }
//
//                    } else {
//
//                        intent = new Intent(context, TextListActivity.class);
//                        switch (position) {
//                            // main page
//                            case 0:
//                                if (!(MenuDrawerActivity.this instanceof MainActivity)) {
//                                    if (getActivityDepth() == 2) {
//                                        finish();
//                                    } else {
//                                        intent = new Intent(context, MainActivity.class);
//                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                        startActivity(intent);
//                                    }
//                                }
//                                break;
//                            //login
//                            case 1:
//                                intent = new Intent(context, LoginActivity.class);
//                                startActivity(intent);
//                                break;
//                            //PDD
//                            case 2:
//                                intent.putExtra("title", getResources().getString(R.string.pdds));
//                                intent.putExtra("type", TextListActivity.CATEGORY_PDD);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                break;
//                            //signs
//                            case 3:
//
//                                intent.putExtra("title", getResources().getString(R.string.signs));
//                                intent.putExtra("type", TextListActivity.CATEGORY_SIGNS);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                break;
//                            //KOAP
//                            case 4:
//                                intent.putExtra("title", getResources().getString(R.string.koaps));
//                                intent.putExtra("type", TextListActivity.CATEGORY_KOAP);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                startActivity(intent);
//                                break;
//
//                        }
//
//
//                    }
                }
            }, 200);
        }
    }

    private int getActivityDepth() {

        Context context = this.getApplicationContext();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(10);
        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (task.baseActivity.getClassName().equals("ru.autokadabra.ui.activities.MainActivity")) {
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


//            if (MyShows.isLoggedIn) {
//
//                switch (position) {
//                    case 0:
//                        holder.image.setImageResource(R.drawable.ic_action_ic_menu_home);
//                        break;
//                    case 1:
//                        holder.image.setImageResource(R.drawable.ic_action_social_person);
//                        break;
//                    case 2:
//                        holder.image.setImageResource(R.drawable.ic_action_collections_cloud);
//                        break;
//                    case 3:
//                        holder.image.setImageResource(R.drawable.content_email);
//                        break;
//                    case 4:
//                        holder.image.setImageResource(R.drawable.ic_action_rating_important);
//                        break;
//                    case 5:
//                        holder.image.setImageResource(R.drawable.ic_action_collections_view_as_list);
//                        break;
//                    case 6:
//                        holder.image.setImageResource(R.drawable.ic_action_alerts_and_states_warning);
//                        break;
//                    case 7:
//                        holder.image.setImageResource(R.drawable.ic_action_device_access_storage_1);
//                        break;
//                    case 8:
//                        holder.image.setImageResource(R.drawable.ic_action_social_reply);
//                        break;
//                }
//            } else {
//                switch (position) {
//                    case 0:
//                        holder.image.setImageResource(R.drawable.ic_action_ic_menu_home);
//                        break;
//                    case 1:
//                        holder.image.setImageResource(R.drawable.ic_action_social_forward);
//                        break;
//                    case 2:
//                        holder.image.setImageResource(R.drawable.ic_action_collections_view_as_list);
//                        break;
//                    case 3:
//                        holder.image.setImageResource(R.drawable.ic_action_alerts_and_states_warning);
//                        break;
//                    case 4:
//                        holder.image.setImageResource(R.drawable.ic_action_device_access_storage_1);
//                        break;
//                }
//            }

            holder.title.setText(getItem(position));

            return convertView;

        }

        protected class ViewHolder {
            protected ImageView image;
            protected TextView title;
            protected TextView count;
        }
    }

    private android.view.MenuItem getMenuItem(final MenuItem item) {
        return new android.view.MenuItem() {
            @Override
            public int getItemId() {
                return item.getItemId();
            }

            public boolean isEnabled() {
                return true;
            }

            @Override
            public boolean collapseActionView() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean expandActionView() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public ActionProvider getActionProvider() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public View getActionView() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public char getAlphabeticShortcut() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getGroupId() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Drawable getIcon() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public Intent getIntent() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ContextMenu.ContextMenuInfo getMenuInfo() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public char getNumericShortcut() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public int getOrder() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public SubMenu getSubMenu() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public CharSequence getTitle() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public CharSequence getTitleCondensed() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public boolean hasSubMenu() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isActionViewExpanded() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isCheckable() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isChecked() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean isVisible() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public android.view.MenuItem setActionProvider(ActionProvider actionProvider) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setActionView(View view) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setActionView(int resId) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setAlphabeticShortcut(char alphaChar) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setCheckable(boolean checkable) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setChecked(boolean checked) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setEnabled(boolean enabled) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setIcon(Drawable icon) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setIcon(int iconRes) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setIntent(Intent intent) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setNumericShortcut(char numericChar) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setShortcut(char numericChar, char alphaChar) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void setShowAsAction(int actionEnum) {
                // TODO Auto-generated method stub

            }

            @Override
            public android.view.MenuItem setShowAsActionFlags(int actionEnum) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setTitle(CharSequence title) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setTitle(int title) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setTitleCondensed(CharSequence title) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public android.view.MenuItem setVisible(boolean visible) {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }


}
