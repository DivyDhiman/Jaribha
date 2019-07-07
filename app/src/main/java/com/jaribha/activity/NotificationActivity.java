package com.jaribha.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.PagerSlidingTabStrip;
import com.jaribha.fragments.NotificationEmailFragment;
import com.jaribha.fragments.NotificationPushFragment;
import com.jaribha.models.UserData;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView iv_close;

    TextView tv_title;

    PagerSlidingTabStrip tabStrip;

    ViewPager viewPager;

    BezelImageView img_user;

    ArrayList<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        iv_close = (ImageView) findViewById(R.id.iv_close);
        img_user = (BezelImageView) findViewById(R.id.img_user_image);
        displayUserImage(img_user);

        img_user.setOnClickListener(this);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.notification));

        titles.add(getString(R.string.email));
        titles.add(getString(R.string.push));

        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.filter_sliding_tabs);
        tabStrip.setTypeface(Utils.getBoldFont(getActivity()), Typeface.BOLD);

        viewPager = (ViewPager) findViewById(R.id.filter_pager);

        if (!isTablet())
            tabStrip.setTextSize(14);
        else {
            tabStrip.setTextSize(16);
        }

        loadSettings();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_close:
                finish();
                break;
            case R.id.img_user_image:
                startActivity(new Intent(this, MyPortfolioActivity.class));
                break;

            default:
                break;

        }
    }

    public class NotificationAdapter extends FragmentStatePagerAdapter {

        private ArrayList<String> TITLES;

        public NotificationAdapter(FragmentManager fm, ArrayList<String> TITLE) {
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
                fragment = NotificationEmailFragment.newInstance(userData);
            }
            if (position == 1) {
                fragment = NotificationPushFragment.newInstance(userData);
            }

            return fragment;

        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    UserData userData;

    private void loadSettings() {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showLoading();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject object = new JSONObject();
                try {
                    object.put("apikey", Urls.API_KEY);
                    object.put("user_id", getUser().id);
                    object.put("user_token", getUser().user_token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.GET_USER_INFO, object);
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
                            userData = new Gson().fromJson(jsonObject.optJSONObject("data").toString(), UserData.class);
                            viewPager.setAdapter(new NotificationAdapter(getSupportFragmentManager(), titles));
                            tabStrip.setViewPager(viewPager);
//                        if (!TextUtils.isNullOrEmpty(userData.creator_new_supporter_push)) {
//                            email_push_Radio_btn1.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.creator_new_sponser_push)) {
//                            email_push_Radio_btn2.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.creator_new_comments_push)) {
//                            email_push_Radio_btn3.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.supporter_project_deadline_push)) {
//                            supporter_push_Radio_btn1.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.supporter_project_updated_push)) {
//                            supporter_push_Radio_btn2.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.sponsor_platinum_space_push)) {
//                            sponser_push_Radio_btn1.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.newsletter_message_chat_push)) {
//                            General_push_Radio_btn1.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.newsletter_proeject_sucessfull_push)) {
//                            General_push_Radio_btn2.setChecked(true);
//                        }
                        } else {
                            switch (msg) {
                                case "Data Not Found":
                                    showDataNotFoundDialog();
                                    break;
                                case "User Not Found":
                                    showSessionDialog();
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
        }.execute();
    }
}
