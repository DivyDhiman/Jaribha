package com.jaribha.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eyeem.recyclerviewtools.Log;
import com.jaribha.R;
import com.jaribha.activity.MyPortfolioActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.interfaces.MyTabInterface;
import com.jaribha.models.TestData;
import com.jaribha.utility.Constants;

import java.util.ArrayList;

public class CreateProjectFragment extends BaseFragment implements View.OnClickListener, MyTabInterface {

    public HorizontalScrollView viewPager;

    public TextView textView0, textView1, textView2, textView3, textView4, textView5, textView6;

    public ArrayList<TestData> titles = new ArrayList<>();

    public LinearLayout mLinearLayout;

    public ImageView imageView0, imageView1, imageView2, imageView3, imageView4, imageView5, imageView6;

    public int position;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_create_project, null);

        position = activity.getIntent().getIntExtra(Constants.DATA, 0);

        if (!isTabletDevice())
            viewPager = (HorizontalScrollView) view.findViewById(R.id.viewPager);

        mLinearLayout = (LinearLayout) view.findViewById(R.id.parentContainer);

        titles.add(new TestData(getString(R.string.guidelines), 1));
        titles.add(new TestData(getString(R.string.basics), 0));
        titles.add(new TestData(getString(R.string.story), 0));
        titles.add(new TestData(getString(R.string.about_you), 0));
        titles.add(new TestData(getString(R.string.account), 0));
        titles.add(new TestData(getString(R.string.rewards), 0));
        titles.add(new TestData(getString(R.string.review), 0));

        textView0 = (TextView) view.findViewById(R.id.textView0);
        textView0.setText(titles.get(0).getTitle());
        textView0.setTextColor(Color.WHITE);
        textView0.setOnClickListener(this);

        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView1.setText(titles.get(1).getTitle());
        textView1.setTextColor(Color.BLACK);
        textView1.setOnClickListener(this);

        textView2 = (TextView) view.findViewById(R.id.textView2);
        textView2.setText(titles.get(2).getTitle());
        textView2.setTextColor(Color.BLACK);
        textView2.setOnClickListener(this);

        textView3 = (TextView) view.findViewById(R.id.textView3);
        textView3.setText(titles.get(3).getTitle());
        textView3.setTextColor(Color.BLACK);
        textView3.setOnClickListener(this);

        textView4 = (TextView) view.findViewById(R.id.textView4);
        textView4.setText(titles.get(4).getTitle());
        textView4.setTextColor(Color.BLACK);
        textView4.setOnClickListener(this);

        textView5 = (TextView) view.findViewById(R.id.textView5);
        textView5.setText(titles.get(5).getTitle());
        textView5.setTextColor(Color.BLACK);
        textView5.setOnClickListener(this);

        textView6 = (TextView) view.findViewById(R.id.textView6);
        textView6.setText(titles.get(6).getTitle());
        textView6.setTextColor(Color.BLACK);
        textView6.setOnClickListener(this);

        imageView0 = (ImageView) view.findViewById(R.id.imageView0);
        imageView1 = (ImageView) view.findViewById(R.id.imageView1);
        imageView2 = (ImageView) view.findViewById(R.id.imageView2);
        imageView3 = (ImageView) view.findViewById(R.id.imageView3);
        imageView4 = (ImageView) view.findViewById(R.id.imageView4);
        imageView5 = (ImageView) view.findViewById(R.id.imageView5);
        imageView6 = (ImageView) view.findViewById(R.id.imageView6);

        displayFragment(position);

        return view;
    }

    public void displayTab(int position) {
        View selectedChild = mLinearLayout.getChildAt(position);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft();
            viewPager.scrollTo(targetScrollX, 0);
            displayFragment(position);
        }
    }

    public void displayFragment(int position) {
        switch (position) {
            case 0:
                Constants.firse = true;
                textView0.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_dot);
                fragment = new CreateProjectGuideLine();
                break;
            case 1:
                Constants.second = true;
                textView1.setTextColor(Color.WHITE);

                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_dot);
                fragment = new CreateProjectBasicFragment();
                break;
            case 2:
                Constants.third = true;
                textView2.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_dot);
                fragment = new CreateProjectStoryFragment();
                break;
            case 3:
                textView3.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_complete);
                imageView3.setImageResource(R.drawable.img_dot);
                fragment = new CreateProjectAboutYouFragment();
                break;
            case 4:
                textView4.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_complete);
                imageView3.setImageResource(R.drawable.img_complete);
                imageView4.setImageResource(R.drawable.img_dot);
                fragment = new CreateProjectAccountFragment();
                break;
            case 5:
                textView5.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_complete);
                imageView3.setImageResource(R.drawable.img_complete);
                imageView4.setImageResource(R.drawable.img_complete);
                imageView5.setImageResource(R.drawable.img_dot);
                fragment = new CreateProjectRewardsFragment();
                break;
            case 6:
                textView6.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_complete);
                imageView3.setImageResource(R.drawable.img_complete);
                imageView4.setImageResource(R.drawable.img_complete);
                imageView5.setImageResource(R.drawable.img_complete);
                imageView6.setImageResource(R.drawable.img_dot);
                fragment = new CreateProjectReviewFragment();
                break;

            default:
                break;
        }

        if (fragment != null) {
            Fragment myFragment = getChildFragmentManager().findFragmentByTag(fragment.getClass().getName());
            if (myFragment != null && myFragment.isVisible()) {
                Log.d(getClass().getName(), "Already on this fragment");
            } else {
                addFragment(fragment, fragment.getClass().getName(), true);
            }
        }
    }

    private void addFragment(Fragment fragment, String tag, Boolean addStack) {
        FragmentManager manager = getChildFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(tag, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(R.id.frameCointainer, fragment);
            //ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            if (addStack) {
                ft.addToBackStack(tag);
            }
            ft.commit();
        }
    }

    public void backToFragment(final Fragment fragment) {
        // go back to something that was added to the backstack
        getChildFragmentManager().popBackStackImmediate(fragment.getClass().getName(), 0);
        // use 0 or the below constant as flag parameter
        // FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    Fragment fragment = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

           /* case R.id.iv_close:
                //startActivity(new Intent(this, HomeScreenActivity.class));
                finish();
                break;*/

            case R.id.img_user_image:
                startActivity(new Intent(activity, MyPortfolioActivity.class));
                break;

            case R.id.textView0:
                if (Constants.firse && Constants.second && Constants.third)
                    backToFragment(new CreateProjectGuideLineFragment());
                break;
            case R.id.textView1:
                if (Constants.firse && Constants.second && Constants.third)
                    backToFragment(new CreateProjectBasicFragment());
                break;
            case R.id.textView2:
                if (Constants.firse && Constants.second && Constants.third)
                    backToFragment(new CreateProjectStoryFragment());
                break;
            case R.id.textView3:
                if (Constants.firse && Constants.second && Constants.third)
                    backToFragment(new CreateProjectAboutYouFragment());
                break;
            case R.id.textView4:
                if (Constants.firse && Constants.second && Constants.third)
                    backToFragment(new CreateProjectAccountFragment());
                break;
            case R.id.textView5:
                if (Constants.firse && Constants.second && Constants.third)
                    backToFragment(new CreateProjectRewardsFragment());
                break;
            case R.id.textView6:
                if (Constants.firse && Constants.second && Constants.third)
                    backToFragment(new CreateProjectReviewFragment());
                break;

            default:
                break;
        }

    }

    @Override
    public void setMyTab(int pos) {
        if (!isTabletDevice()) {
            displayTab(pos);
        } else {
            displayFragment(pos);
        }
    }
}
