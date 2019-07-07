package com.jaribha.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.PagerSlidingTabStrip;
import com.jaribha.utility.Utils;

import java.util.ArrayList;

public class HowItWorksFragment extends BaseFragment {

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_how_it_works, container, false);

        ArrayList<String> titles = new ArrayList<>();
        titles.add(activity.getResources().getString(R.string.creators));
        titles.add(activity.getResources().getString(R.string.supporters));
        titles.add(activity.getResources().getString(R.string.sponsors));
        titles.add(activity.getResources().getString(R.string.community));
        //titles.add("Free");

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.how_it_works_tabs);
        tabStrip.setTypeface(Utils.getBoldFont(getActivity()), Typeface.BOLD);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.how_it_works_pager);
        viewPager.setAdapter(new HowItWorksPagerAdapter(getChildFragmentManager(), titles));


        if (!isTabletDevice())
            tabStrip.setTextSize(14);
        else {
            tabStrip.setTextSize(16);
        }

        tabStrip.setViewPager(viewPager);

        return view;
    }

    public class HowItWorksPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> TITLES;

        public HowItWorksPagerAdapter(FragmentManager fm, ArrayList<String> TITLE) {
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
                fragment = new HowWorksCreatorsFragment();
            }
            if (position == 1) {
                fragment = new HowWorksSupportersFragment();
            }
            if (position == 2) {
                fragment = new HowWorksSponsersFragment();
            }
            if (position == 3) {
                fragment = new HowWorksCommunityFragment();
            }
            /*if (position == 4) {
                fragment = new HowWorksFreeFragment();
            }*/

            return fragment;

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }
}
