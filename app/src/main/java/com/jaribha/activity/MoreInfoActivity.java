package com.jaribha.activity;

import android.content.Intent;
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
import com.jaribha.fragments.MoreInfoFAQsFragment;
import com.jaribha.fragments.MoreInfoRiskChallengeFragment;
import com.jaribha.fragments.MoreInfoStoryFragment;
import com.jaribha.models.ProjectStory;
import com.jaribha.utility.Utils;

import java.util.ArrayList;

public class MoreInfoActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView iv_close;

    ProjectStory story;

    TextView tv_title;

    BezelImageView imageViewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moreinfo);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setImageResource(R.drawable.btn_back);
        imageViewUser = (BezelImageView) findViewById(R.id.img_user_image);
        imageViewUser.setOnClickListener(this);
        displayUserImage(imageViewUser);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.more_info));

        ArrayList<String> titles = new ArrayList<>();
        titles.add(getString(R.string.story));
        titles.add(getString(R.string.risk_and_challenges));
        titles.add(getString(R.string.faqs));

        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.more_info_tabs);

        tabStrip.setTypeface(Utils.getBoldFont(getActivity()), Typeface.BOLD);

        ViewPager viewPager = (ViewPager) findViewById(R.id.more_info_pager);
        viewPager.setAdapter(new MoreInfoPagerAdapter(getSupportFragmentManager(), titles));

        if (!isTablet())
            tabStrip.setTextSize(14);
        else {
            tabStrip.setTextSize(16);
        }

        if (getIntent().getExtras() != null) {
            story = (ProjectStory) getIntent().getSerializableExtra("more_info");
        }

        tabStrip.setViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_close:
                finish();
                break;
            case R.id.img_user_image:
                startActivity(new Intent(getActivity(), MyPortfolioActivity.class));
                break;

            default:
                break;
        }
    }

    public class MoreInfoPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> TITLES;

        public MoreInfoPagerAdapter(FragmentManager fm, ArrayList<String> TITLE) {
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
                fragment = MoreInfoStoryFragment.newInstance(story);
            }
            if (position == 1) {
                fragment = MoreInfoRiskChallengeFragment.newInstance(story);
            }
            if (position == 2) {
                fragment = MoreInfoFAQsFragment.newInstance(story);
            }

            return fragment;

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }
}
