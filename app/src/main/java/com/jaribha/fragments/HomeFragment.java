package com.jaribha.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaribha.R;
import com.jaribha.adapters.HomePagerAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.PagerSlidingTabStrip;
import com.jaribha.interfaces.CommunityTabListener;
import com.jaribha.utility.Utils;

import java.util.ArrayList;


public class HomeFragment extends BaseFragment implements CommunityTabListener {

    PagerSlidingTabStrip tabStrip;

    int tabPos = 0;

    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ArrayList<String> titles = new ArrayList<>();
        titles.add(getActivity().getResources().getString(R.string.staff_picks));
        titles.add(getActivity().getResources().getString(R.string.browse_projects));
        titles.add(getActivity().getResources().getString(R.string.communities));
        titles.add(getActivity().getResources().getString(R.string.sponsors));

        tabPos = getArguments().getInt("tab_pos");

        tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        tabStrip.setTypeface(Utils.getBoldFont(getActivity()), Typeface.BOLD);

        viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new HomePagerAdapter(getChildFragmentManager(), titles));
        viewPager.setCurrentItem(tabPos, true);

        if (!isTabletDevice())
            tabStrip.setTextSize(14);
        else {
            tabStrip.setTextSize(16);
        }

        tabStrip.setViewPager(viewPager);

        return view;
    }

    @Override
    public void setTab() {
    }
}
