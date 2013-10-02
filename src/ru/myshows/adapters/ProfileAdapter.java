package ru.myshows.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ru.myshows.activity.R;
import ru.myshows.fragments.NewEpisodesFragment;
import ru.myshows.fragments.NextEpisodesFragment;
import ru.myshows.fragments.ProfileFragment;
import ru.myshows.fragments.ShowsFragment;
import ru.myshows.util.Settings;

/**
 * Created with IntelliJ IDEA.
 * User: dell
 * Date: 10/1/13
 * Time: 1:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProfileAdapter extends FragmentPagerAdapter {

    private String[] titles;

    public ProfileAdapter(Context context, FragmentManager fm) {
        super(fm);
        titles = context.getResources().getStringArray(R.array.profile_titles);
    }

    @Override
    public int getCount() {
        return 2;
    }

    public Fragment getItem(int position) {
        Fragment fragment = null;
        Bundle args = new Bundle();

        switch (position){
            case 0:
                args.putInt("action", ShowsFragment.SHOWS_USER);
                fragment = new ShowsFragment();
                fragment.setArguments(args);
                break;
            case 1:
                args.putString("login", Settings.getString(Settings.KEY_LOGIN));
                fragment = new ProfileFragment();
                fragment.setArguments(args);
                break;
        }

        return fragment;
    }

    public CharSequence getPageTitle(int position) {
        if (titles != null)
            return titles[position];
        return "";
    }

}
