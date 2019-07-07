package com.jaribha.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jaribha.fragments.BrowseProjectFragment;
import com.jaribha.fragments.CommunitiesFragment;
import com.jaribha.fragments.StaffPicksFragment;
import com.jaribha.fragments.SponsorsFragment;

import java.util.ArrayList;

public class HomePagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<String> TITLES;

    public HomePagerAdapter(FragmentManager fm, ArrayList<String> TITLE) {
        super(fm);
        this.TITLES = TITLE;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES.get(position);
    }

    @Override
    public int getCount() {
        return TITLES.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        if (position == 0) {
            fragment = new StaffPicksFragment();// Staff picks
        }
        if (position == 1) {
            fragment = new BrowseProjectFragment();
        }
        if (position == 2) {
            fragment = new CommunitiesFragment();
        }
        if (position == 3) {
            fragment = new SponsorsFragment();
        }

        return fragment;

    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
