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

import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.PagerSlidingTabStrip;
import com.jaribha.models.GetProjects;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditProjectReviewFragment extends BaseFragment {

    JSONObject mObject;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public static EditProjectReviewFragment newInstance(JSONObject object) {
        Bundle args = new Bundle();
        args.putString(Constants.DATA, object.toString());
        EditProjectReviewFragment fragment = new EditProjectReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mString = getArguments().getString(Constants.DATA);
        try {
            mObject = new JSONObject(mString);
            GetProjects projects = new Gson().fromJson(mObject.optJSONObject("Project").toString(), GetProjects.class);
            JaribhaPrefrence.setPref(activity, Constants.PROJECT_ID, projects.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project_review, null);

        ArrayList<String> titles = new ArrayList<>();

        if (isAdded()) {
            if (isTabletDevice()) {
                titles.add(getString(R.string.info));
                titles.add(getString(R.string.supporters));
                titles.add(getString(R.string.update));
                titles.add(getString(R.string.comments));
            } else {
                titles.add(getString(R.string.info));
                titles.add(getString(R.string.rewards));
                titles.add(getString(R.string.supporters));
                titles.add(getString(R.string.update));
                titles.add(getString(R.string.comments));
            }
        }
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.projectInfoTabs);

        tabStrip.setTypeface(Utils.getBoldFont(getActivity()), Typeface.BOLD);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.projectInfoPager);

        viewPager.setAdapter(new CreateProjectReviewPagerAdapter(getChildFragmentManager(), titles));
        if (!isTabletDevice())
            tabStrip.setTextSize(14);
        else {
            tabStrip.setTextSize(16);
        }

        tabStrip.setViewPager(viewPager);

        return view;
    }

    public class CreateProjectReviewPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> TITLES;

        public CreateProjectReviewPagerAdapter(FragmentManager fm, ArrayList<String> TITLE) {
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

            if (isTabletDevice()) {
                if (position == 0) {
                    fragment = new CreateProjectDetailsInfoFragment();
                }
                if (position == 1) {
                    fragment = new ProjectDetailSupportersFragment();
                }
                if (position == 2) {
                    fragment = new ProjectDetailUpdateFragment();
                }
                if (position == 3) {
                    fragment = new ProjectDetailCommentsFragment();
                }
            } else {
                if (position == 0) {
                    fragment = new CreateProjectDetailsInfoFragment();
                }
                if (position == 1) {
                    fragment = new ProjectDetailRewardsFragment();
                }
                if (position == 2) {
                    fragment = new ProjectDetailSupportersFragment();
                }
                if (position == 3) {
                    fragment = new ProjectDetailUpdateFragment();
                }
                if (position == 4) {
                    fragment = new ProjectDetailCommentsFragment();
                }
            }

            return fragment;

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }
}
