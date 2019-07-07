package com.jaribha.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.PagerSlidingTabStrip;
import com.jaribha.fragments.ProjectDetailCommentsFragment;
import com.jaribha.fragments.ProjectDetailInfoFragmentNew;
import com.jaribha.fragments.ProjectDetailReportFragment;
import com.jaribha.fragments.ProjectDetailRewardsFragment;
import com.jaribha.fragments.ProjectDetailSupportersFragment;
import com.jaribha.fragments.ProjectDetailUpdateFragment;
import com.jaribha.interfaces.DetailTabUpdateListener;
import com.jaribha.interfaces.MyTabInterface;
import com.jaribha.interfaces.SponsersInterface;
import com.jaribha.models.GetProjects;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectDetailsTabs extends BaseAppCompatActivity implements View.OnClickListener, MyTabInterface, DetailTabUpdateListener, SponsersInterface {

    ImageView iv_close, iv_favourite, iv_share;

    TextView tv_title;

    BezelImageView img_user_image;

    GetProjects getProjects;

    ViewPager viewPager;

    PagerSlidingTabStrip tabStrip;

    ArrayList<String> titles;

    ProjectDetailPagerAdapter adapter;

    boolean comment, update,back_press;

    /*changed by pratik 18-5*/
    ArrayList<String> sponserfromInterface = new ArrayList<>();

    private ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_detail_tabs);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        arrayList.clear();
        titles = new ArrayList<>();

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.project_details));

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(this);

        iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_share.setVisibility(View.VISIBLE);
        iv_share.setOnClickListener(this);

        iv_favourite = (ImageView) findViewById(R.id.iv_favourite);
        iv_favourite.setVisibility(View.VISIBLE);
        iv_favourite.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.VISIBLE);
        img_user_image.setOnClickListener(this);

        displayUserImage(img_user_image);

        adapter = new ProjectDetailPagerAdapter(getSupportFragmentManager(), titles, arrayList);

        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.projectInfoTabs);
        tabStrip.setTypeface(Utils.getBoldFont(getActivity()), Typeface.BOLD);

        viewPager = (ViewPager) findViewById(R.id.projectInfoPager);
        viewPager.setOffscreenPageLimit(0);

        back_press = JaribhaPrefrence.getPref(ProjectDetailsTabs.this,"back_press",false);

        if (getIntent().getExtras() != null) {
            getProjects = (GetProjects) getIntent().getSerializableExtra("detail_object");
            comment = getIntent().getBooleanExtra("comment_push", false);
            update = getIntent().getBooleanExtra("update_push", false);
        }

        if (isInternetConnected())
            loadCounters();
        else
            showNetworkDialog();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                if (Constants.supportSelected) {
                    Constants.supportSelected = false;
                    viewPager.setCurrentItem(0, true);
                } else {
                    if(back_press){
                        JaribhaPrefrence.deleteKey(ProjectDetailsTabs.this, "back_press");

                        Intent intent = new Intent(ProjectDetailsTabs.this, HomeScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        finish();
                    }
                }
                break;

            case R.id.iv_share:
//                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                sharingIntent.setType("text/plain");
//                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_using)));
                break;

            case R.id.iv_favourite:
                Intent intent = new Intent(this, HomeScreenActivity.class);
                intent.putExtra("pos", 9);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

            case R.id.img_user_image:
                if (getUser() != null)
                    startActivity(new Intent(getActivity(), MyPortfolioActivity.class));
                else
                    startActivity(new Intent(getActivity(), LoginScreenActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    public void setMyTab(int pos) {
        viewPager.setCurrentItem(pos, true);
    }

    @Override
    public void setTabCount(int update, int comment) {

        if (isTablet()) {
            if (update != 0 && update != -1)
                titles.set(2, getString(R.string.update) + "(" + update + ")");
            if (comment != 0 && comment != -1)
                titles.set(3, getString(R.string.comments) + "(" + comment + ")");
        } else {
            if (update != 0 && update != -1)
                titles.set(3, getString(R.string.update) + "(" + update + ")");
            if (comment != 0 && comment != -1)
                titles.set(4, getString(R.string.comments) + "(" + comment + ")");
        }
        //adapter.notifyDataSetChanged();
        tabStrip.notifyDataSetChanged();
    }

    public void guest_check_save(){
        if (getUser() == null){
            JaribhaPrefrence.set_utils(ProjectDetailsTabs.this,getProjects);
            JaribhaPrefrence.setPref(ProjectDetailsTabs.this,"comment_push",comment);
            JaribhaPrefrence.setPref(ProjectDetailsTabs.this,"update_push",update);
        }
    }

    @Override
    public void getSponsers(ArrayList<String> ar) {
        System.out.println("SPONSERS IN TAB ACTIVITY" + ar.size());
        sponserfromInterface = ar;
    }

    public class ProjectDetailPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> TITLES;

        /*changed by pratik 18-5*/
        ArrayList<String> supportersesarray;

        public ProjectDetailPagerAdapter(FragmentManager fm, ArrayList<String> TITLE, ArrayList<String> supportersesarray) {
            super(fm);
            this.TITLES = TITLE;
            this.supportersesarray = supportersesarray;
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

            if (!isTablet()) {
                if (position == 0) {
                    fragment = new ProjectDetailInfoFragmentNew();
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
                    /*changed by pratik 18-5*/
                    System.out.println("Size in pos++++++++" + supportersesarray);
                    return ProjectDetailCommentsFragment.newInstance(supportersesarray, sponserfromInterface);
                }
                if (position == 5) {
                    fragment = new ProjectDetailReportFragment();
                }
            } else {
                if (position == 0) {
                    fragment = new ProjectDetailInfoFragmentNew();
                }
                if (position == 1) {
                    fragment = new ProjectDetailSupportersFragment();
                }
                if (position == 2) {
                    fragment = new ProjectDetailUpdateFragment();
                }
                if (position == 3) {
                    /*changed by pratik 18-5*/
                    System.out.println("Size in pos++++++++" + supportersesarray);
                    return ProjectDetailCommentsFragment.newInstance(supportersesarray, sponserfromInterface);
                }
                if (position == 4) {
                    fragment = new ProjectDetailReportFragment();
                }
            }

            return fragment;

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    public static ProjectDetailCommentsFragment newInstance(int index) {
        ProjectDetailCommentsFragment f = new ProjectDetailCommentsFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public void loadCounters() {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            if (getUser() != null) {
                projectJson.put("user_id", getUser().id);
                projectJson.put("user_token", getUser().user_token);
            } else {
                projectJson.put("user_id", "");
                projectJson.put("user_token", "");
            }
            projectJson.put("project_id", JaribhaPrefrence.getPref(getActivity(), Constants.PROJECT_ID, ""));

            GetProjectInfo mAuthTask = new GetProjectInfo(projectJson);
            mAuthTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String suppCount = "0";
    String commentCount = "0";
    String updateCount = "0";

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
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECT_DETAIL, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            hideLoading();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {

                        JSONObject data = jsonObject.optJSONObject("data");

                        if (data.optJSONArray("Projectupdates") != null)
                            updateCount = String.valueOf(data.optJSONArray("Projectupdates").length());
                        if (data.optJSONArray("Projectcomments") != null)
                            commentCount = String.valueOf(data.optJSONArray("Projectcomments").length());
                        if (jsonObject.optJSONObject("data").optJSONArray("Supporters") != null) {
                            suppCount = String.valueOf(jsonObject.optJSONObject("data").optJSONArray("Supporters").length());
                            JSONArray data1 = jsonObject.optJSONObject("data").optJSONArray("Supporters");
                            System.out.println("dataaaa1size+++++" + data1.length());
                            if (data1 != null) {
                                for (int j = 0; j < data1.length(); j++) {
                                    /*changed by pratik 18-5*/
                                    //String id = data1.optJSONObject(j).getString("id");
                                    JSONObject dataPledge = data1.optJSONObject(j).optJSONObject("Pledge");
                                    String supporterID = dataPledge.optString("supporter_id");
                                    arrayList.add(supporterID);
                                }
                            }
                        }
                    } else {
                        hideLoading();
                        if (msg.equals("Data Not Found")) {
                            showDataNotFoundDialog();
                        } else if (msg.equals("User Not Found")) {
                            showSessionDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideLoading();
                    showServerDialogDialog();

                }
                if (isTablet()) {
                    titles.add(getString(R.string.info));
                    titles.add(getString(R.string.supporters) + "(" + suppCount + ")");
                    titles.add(getString(R.string.update) + "(" + updateCount + ")");
                    titles.add(getString(R.string.comments) + "(" + commentCount + ")");
                    titles.add(getString(R.string.report));
                } else {
                    titles.add(getString(R.string.info));
                    titles.add(getString(R.string.rewards));
                    titles.add(getString(R.string.supporters) + "(" + suppCount + ")");
                    titles.add(getString(R.string.update) + "(" + updateCount + ")");
                    titles.add(getString(R.string.comments) + "(" + commentCount + ")");
                    titles.add(getString(R.string.report));
                }

                if (!isTablet())
                    tabStrip.setTextSize(14);
                else {
                    tabStrip.setTextSize(16);
                }

                if (comment) {
                    if (isTablet()) {
                        viewPager.setCurrentItem(4);
                    } else {
                        viewPager.setCurrentItem(5);
                    }
                } else if (update) {
                    if (isTablet()) {
                        viewPager.setCurrentItem(3);
                    } else {
                        viewPager.setCurrentItem(4);
                    }
                }

                viewPager.setAdapter(adapter);
                tabStrip.setViewPager(viewPager);
            } else {
                showServerDialogDialog();
                hideLoading();
            }
        }

        @Override
        protected void onCancelled() {
            hideLoading();
        }
    }

    @Override
    public void onBackPressed() {
        if(back_press){
            JaribhaPrefrence.deleteKey(ProjectDetailsTabs.this, "back_press");
            Intent intent = new Intent(ProjectDetailsTabs.this, HomeScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            super.onBackPressed();
        }
    }
}
