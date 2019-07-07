package com.jaribha.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.PagerSlidingTabStrip;
import com.jaribha.fragments.FilterCategoryFragment;
import com.jaribha.fragments.FilterCountryFragment;
import com.jaribha.fragments.FilterFeaturedFragment;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Utils;

import java.util.ArrayList;

public class FilterActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView iv_close;

    TextView tv_title, tv_sub_title, tv_apply;

    ViewPager viewPager;

    BezelImageView img_user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.GONE);
        displayUserImage(img_user_image);
        
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setImageResource(R.drawable.close_icon);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.filter));

        tv_sub_title = (TextView) findViewById(R.id.tv_sub_title);
        tv_sub_title.setVisibility(View.VISIBLE);
        tv_sub_title.setText(JaribhaPrefrence.getPref(this, Constants.FILTER_COUNT, "0") + " " + getString(R.string.results));

        tv_apply = (TextView) findViewById(R.id.tv_apply);
        tv_apply.setVisibility(View.VISIBLE);
        tv_apply.setOnClickListener(this);
        tv_apply.setText(getString(R.string.apply));

        ArrayList<String> titles = new ArrayList<>();
        titles.add(getString(R.string.categories));
        titles.add(getString(R.string.features));
        titles.add(getString(R.string.country));

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.filter_sliding_tabs);
        tabStrip.setTypeface(Utils.getBoldFont(getActivity()), Typeface.BOLD);

        viewPager = (ViewPager) findViewById(R.id.filter_pager);

        viewPager.setAdapter(new FilterPagerAdapter(getSupportFragmentManager(), titles));
        if (!isTablet())
            tabStrip.setTextSize(14);
        else {
            tabStrip.setTextSize(16);
        }

        tabStrip.setViewPager(viewPager);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

        // Intent intent;
        switch (v.getId()) {

            case R.id.iv_close:
//                JaribhaPrefrence.setPref(this, Constants.CATEGORY_NAME, getString(R.string.all_cat));
//                JaribhaPrefrence.setPref(this, Constants.CATEGORY_ID, "");
//                JaribhaPrefrence.setPref(this, Constants.COUNTRY_ID, "");
//                JaribhaPrefrence.setPref(this, Constants.FEATURE_ID_SEARCH, "");
//                JaribhaPrefrence.setPref(this, Constants.SEARCH, "");
                JaribhaPrefrence.setPref(this, Constants.FILTER_SEARCH, true);
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.tv_apply:
                JaribhaPrefrence.deleteKey(this, Constants.SEARCH);
                JaribhaPrefrence.setPref(this, Constants.FILTER_SEARCH, true);
                setResult(RESULT_OK);
                finish();
                break;

            default:
                break;

        }
    }

    public class FilterPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> TITLES;

        public FilterPagerAdapter(FragmentManager fm, ArrayList<String> TITLE) {
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
                fragment = new FilterCategoryFragment();
            }
            if (position == 1) {
                fragment = new FilterFeaturedFragment();
            }
            if (position == 2) {
                fragment = new FilterCountryFragment();
            }

            return fragment;

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewPager.setCurrentItem(0);
    }
}
