package com.jaribha.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.eyeem.recyclerviewtools.Log;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.fragments.EditProjectAboutYouFragment;
import com.jaribha.fragments.EditProjectAccountFragment;
import com.jaribha.fragments.EditProjectBasicFragment;
import com.jaribha.fragments.EditProjectGuideLine;
import com.jaribha.fragments.EditProjectReviewFragment;
import com.jaribha.fragments.EditProjectRewardsFragment;
import com.jaribha.fragments.EditProjectStoryFragment;
import com.jaribha.models.TestData;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditProjectActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private HorizontalScrollView viewPager;

    TextView textView0, textView1, textView2, textView3, textView4, textView5, textView6, tv_title;

    private ArrayList<TestData> titles = new ArrayList<>();

    private LinearLayout mLinearLayout;

    ImageView imageView0, imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, iv_close;

    BezelImageView img_user_image;

    String project_id;

    public boolean firstLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        project_id = getIntent().getStringExtra(Constants.PROJECT_ID);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.edit_project));

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.VISIBLE);
        img_user_image.setOnClickListener(this);
        displayUserImage(img_user_image);
        if (!isTablet())
            viewPager = (HorizontalScrollView) findViewById(R.id.viewPager);

        mLinearLayout = (LinearLayout) findViewById(R.id.parentContainer);

        titles.add(new TestData(getString(R.string.guidelines), 1));
        titles.add(new TestData(getString(R.string.basics), 0));
        titles.add(new TestData(getString(R.string.story), 0));
        titles.add(new TestData(getString(R.string.about_you), 0));
        titles.add(new TestData(getString(R.string.account), 0));
        titles.add(new TestData(getString(R.string.rewards), 0));
        titles.add(new TestData(getString(R.string.review), 0));

        textView0 = (TextView) findViewById(R.id.textView0);
        textView0.setText(titles.get(0).getTitle());
        textView0.setTextColor(Color.WHITE);

        textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(titles.get(1).getTitle());
        textView1.setTextColor(Color.BLACK);

        textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText(titles.get(2).getTitle());
        textView2.setTextColor(Color.BLACK);

        textView3 = (TextView) findViewById(R.id.textView3);
        textView3.setText(titles.get(3).getTitle());
        textView3.setTextColor(Color.BLACK);

        textView4 = (TextView) findViewById(R.id.textView4);
        textView4.setText(titles.get(4).getTitle());
        textView4.setTextColor(Color.BLACK);

        textView5 = (TextView) findViewById(R.id.textView5);
        textView5.setText(titles.get(5).getTitle());
        textView5.setTextColor(Color.BLACK);

        textView6 = (TextView) findViewById(R.id.textView6);
        textView6.setText(titles.get(6).getTitle());
        textView6.setTextColor(Color.BLACK);

        imageView0 = (ImageView) findViewById(R.id.imageView0);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);
        imageView5 = (ImageView) findViewById(R.id.imageView5);
        imageView6 = (ImageView) findViewById(R.id.imageView6);

        textView0.setOnClickListener(this);
        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
        textView3.setOnClickListener(this);
        textView4.setOnClickListener(this);
        textView5.setOnClickListener(this);
        textView6.setOnClickListener(this);
        firstLoad = true;
        loadUpdates();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frameCointainer);
        if (f != null) {
            String fragClassName = f.getClass().getName();
            if (fragClassName.equalsIgnoreCase(EditProjectGuideLine.class.getName())) {
                finish();
            } else if (fragClassName.equalsIgnoreCase(EditProjectBasicFragment.class.getName())) {
                GuidelineFragment();
            } else {
                super.onBackPressed();
            }
        } else {
            finish();
        }
    }

    public void displayTab(int position, boolean isProjectLoad) {
        View selectedChild = mLinearLayout.getChildAt(position);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft();
            viewPager.scrollTo(targetScrollX, 0);
            displayFragment(position, isProjectLoad);
        }
    }

    public void displayFragment(int position, boolean isProjectLoad) {
        switch (position) {
            case 0:
                textView0.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_dot);
                fragment = new EditProjectGuideLine();
                break;
            case 1:
                textView1.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_dot);
                fragment = EditProjectBasicFragment.newInstance(mObject);
                break;
            case 2:
                textView2.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_dot);
                fragment = EditProjectStoryFragment.newInstance(mObject);
                break;
            case 3:
                textView3.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_complete);
                imageView3.setImageResource(R.drawable.img_dot);
                fragment = EditProjectAboutYouFragment.newInstance(mObject);
                break;
            case 4:
                textView4.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_complete);
                imageView3.setImageResource(R.drawable.img_complete);
                imageView4.setImageResource(R.drawable.img_dot);
                fragment = EditProjectAccountFragment.newInstance(mObject);
                break;
            case 5:
                textView5.setTextColor(Color.WHITE);
                imageView0.setImageResource(R.drawable.img_complete);
                imageView1.setImageResource(R.drawable.img_complete);
                imageView2.setImageResource(R.drawable.img_complete);
                imageView3.setImageResource(R.drawable.img_complete);
                imageView4.setImageResource(R.drawable.img_complete);
                imageView5.setImageResource(R.drawable.img_dot);
                fragment = EditProjectRewardsFragment.newInstance(mObject);
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
                fragment = EditProjectReviewFragment.newInstance(mObject);
                break;

            default:
                break;
        }

        if (fragment != null) {
            Fragment myFragment = getSupportFragmentManager().findFragmentByTag(fragment.getClass().getName());
            if (myFragment != null && myFragment.isVisible()) {
                Log.d(getClass().getName(), "Already on this fragment");
            } else {
                addFragment(fragment, fragment.getClass().getName(), true);
            }
        }

        if (isProjectLoad) {
            loadUpdates();
        }
    }

    private void addFragment(Fragment fragment, String tag, Boolean addStack) {
        FragmentManager manager = getSupportFragmentManager();
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

    private void GuidelineFragment() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(R.id.frameCointainer, new EditProjectGuideLine());
        ft.addToBackStack(EditProjectGuideLine.class.getName());
        ft.commit();
    }

    Fragment fragment = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.img_user_image:
                startActivity(new Intent(getActivity(), MyPortfolioActivity.class));
                break;
            case R.id.textView0:
                displayFragment(0, false);
                break;
            case R.id.textView1:
                displayFragment(1, false);
                break;
            case R.id.textView2:
                displayFragment(2, false);
                break;
            case R.id.textView3:
                displayFragment(3, false);
                break;
            case R.id.textView4:
                displayFragment(4, false);
                break;
            case R.id.textView5:
                displayFragment(5, false);
                break;
            case R.id.textView6:
                displayFragment(6, false);
                break;

            default:
                break;
        }
    }

    public void loadUpdates() {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);
            projectJson.put("user_id", getUser().id);
            projectJson.put("user_token", getUser().user_token);
            projectJson.put("project_id", project_id);

            GetProjectInfo mAuthTask = new GetProjectInfo(projectJson);
            mAuthTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JSONObject mObject;

    public class GetProjectInfo extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetProjectInfo(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_INCOMPLETE_PROJECT_DETAILS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject response) {
            hideLoading();
            if (response != null) {
                try {
                    JSONObject jsonObject = response.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        mObject = jsonObject.optJSONObject("data");
                        if (firstLoad)
                            displayFragment(1, false);
                    } else {
                        hideLoading();
                        switch (msg) {
                            case "Data Not Found":
                                showDataNotFoundDialog();
                                break;
                            case "User Not Found":
                                showSessionDialog();
                                break;
                            case "Project not Found":
//                            dialogWithTwoButton();
                                break;
                            default:
                                showServerDialogDialog();
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showServerDialogDialog();
                }
            } else {
                showServerDialogDialog();
            }
        }

        @Override
        protected void onCancelled() {
            hideLoading();
        }
    }
}
