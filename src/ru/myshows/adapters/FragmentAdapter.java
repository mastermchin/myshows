package ru.myshows.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ru.myshows.activity.R;

import java.util.List;

/**
 * Author: Georgy Gobozov
 * Date: 03.10.13
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    private String[] titles;
    private Bundle args;
    private List<Fragment> fragments;

    public FragmentAdapter(Context context, FragmentManager fm, List<Fragment> fragments, Bundle args, int titlesId) {
        super(fm);
        this.fragments = fragments;
        this.args = args;
        this.titles = context.getResources().getStringArray(titlesId);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments.get(position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        if (fragments != null && !fragments.isEmpty())
            return fragments.size();
        else
            return 0;
    }

    public CharSequence getPageTitle(int position) {
        if (titles != null)
            return titles[position];
        return "";
    }
}
