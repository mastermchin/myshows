package ru.myshows.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ru.myshows.activity.R;
import ru.myshows.fragments.NewEpisodesFragment;
import ru.myshows.fragments.NextEpisodesFragment;

/**
 * Created with IntelliJ IDEA.
 * User: dell
 * Date: 10/1/13
 * Time: 1:31 AM
 * To change this template use File | Settings | File Templates.
 */
public class EpisodesAdapter extends FragmentPagerAdapter {

    private String[] titles;

    public EpisodesAdapter(Context context, FragmentManager fm) {
        super(fm);
        titles = context.getResources().getStringArray(R.array.episodes_titles);
    }

    @Override
    public int getCount() {
        return 2;
    }

    public Fragment getItem(int position) {
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new NewEpisodesFragment();
                break;
            case 1:
                fragment = new NextEpisodesFragment();
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
