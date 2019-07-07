package com.jaribha.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaribha.R;
import com.jaribha.activity.HomeScreenActivity;
import com.jaribha.activity.MoreInfoActivity;
import com.jaribha.adapters.QueAnsAdapter;
import com.jaribha.adapters.ReviewProjectRewardsAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.ExpandableHeightListView;
import com.jaribha.models.AddFAQ;
import com.jaribha.models.Community;
import com.jaribha.models.GetProjects;
import com.jaribha.models.ProjectStory;
import com.jaribha.models.Rewards;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreateProjectDetailsInfoFragment extends BaseFragment implements View.OnClickListener {

    Button btn_review_contact_me, btn_submit_project;

    TextView tv_review_community_more_info, tv_desc_more_infoOverView;

    ImageView projectImage, iv_review_project_instagram, iv_review_project_twitter, iv_review_project_facebook, iv_review_project_web, iv_review_project_phone, iv_review_project_email;

    private TextView tv_proj_title, tv_by_user, tv_category_name, tv_project_review_location, tv_supported_amount, tv_review_days_left, tv_review_goal, tv_overview;

    private TextView tv_review_user_name, tv_review_user_location, tv_review_user_join_date, tv_review_user_bio, tv_project_desc, tv_review_risk_challenges;

    ProgressBar progress;

    private TextView tv_review_community_detail;

    Button btn_become_sponsor;

    private BezelImageView iv_review_user_image, iv_community_user;

    private ImageView iv_review_community_email, iv_review_community_web;

    private ExpandableHeightListView list_review_faq, list_review_rewards;

    private ArrayList<Rewards> rewardsList = new ArrayList<>();

    private ProjectStory projectStory;

    private ProgressDialog progressDialog;

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_project_info_details_fragment, null);
        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        reviewProject();

        if (isTabletDevice()) {
            list_review_faq = (ExpandableHeightListView) view.findViewById(R.id.list_review_faq);
            tv_review_user_bio = (TextView) view.findViewById(R.id.tv_review_user_bio);
            list_review_rewards = (ExpandableHeightListView) view.findViewById(R.id.list_review_rewards);
            tv_review_risk_challenges = (TextView) view.findViewById(R.id.tv_review_risk_challenges);
        } else {
            tv_by_user = (TextView) view.findViewById(R.id.tv_by_user);
            tv_category_name = (TextView) view.findViewById(R.id.tv_category_name);
            tv_project_review_location = (TextView) view.findViewById(R.id.tv_project_review_location);
            tv_review_user_location = (TextView) view.findViewById(R.id.tv_review_user_location);
            tv_review_user_join_date = (TextView) view.findViewById(R.id.tv_review_user_join_date);
            iv_review_project_instagram = (ImageView) view.findViewById(R.id.iv_review_project_instagram);
            iv_review_project_twitter = (ImageView) view.findViewById(R.id.iv_review_project_twitter);
            iv_review_project_facebook = (ImageView) view.findViewById(R.id.iv_review_project_facebook);
            iv_review_project_web = (ImageView) view.findViewById(R.id.iv_review_project_web);
            iv_review_project_phone = (ImageView) view.findViewById(R.id.iv_review_project_phone);
            iv_review_project_email = (ImageView) view.findViewById(R.id.iv_review_project_email);
            iv_review_community_email = (ImageView) view.findViewById(R.id.iv_review_community_email);
            iv_review_community_web = (ImageView) view.findViewById(R.id.iv_review_community_web);
            tv_desc_more_infoOverView = (TextView) view.findViewById(R.id.tv_review_desc_more_infoOverView);
            tv_desc_more_infoOverView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(activity, MoreInfoActivity.class).putExtra("more_info", projectStory));
                }
            });

            //Email icon visibility
            if (!TextUtils.isNullOrEmpty(getUser().email)) {
                iv_review_project_email.setVisibility(View.VISIBLE);
            } else {
                iv_review_project_email.setVisibility(View.GONE);
            }

            if (!TextUtils.isNullOrEmpty(getUser().phone)) {
                iv_review_project_phone.setVisibility(View.VISIBLE);
            } else {
                iv_review_project_phone.setVisibility(View.GONE);
            }

            if (!TextUtils.isNullOrEmpty(getUser().website)) {
                iv_review_project_web.setVisibility(View.VISIBLE);
            } else {
                iv_review_project_web.setVisibility(View.GONE);
            }

            if (!TextUtils.isNullOrEmpty(getUser().facebook)) {
                iv_review_project_facebook.setVisibility(View.VISIBLE);
            } else {
                iv_review_project_facebook.setVisibility(View.GONE);
            }

            if (!TextUtils.isNullOrEmpty(getUser().twitter)) {
                iv_review_project_twitter.setVisibility(View.VISIBLE);
            } else {
                iv_review_project_twitter.setVisibility(View.GONE);
            }

            if (!TextUtils.isNullOrEmpty(getUser().instagram)) {
                iv_review_project_instagram.setVisibility(View.VISIBLE);
            } else {
                iv_review_project_instagram.setVisibility(View.GONE);
            }
        }

        btn_become_sponsor = (Button) view.findViewById(R.id.btn_review_become_sponsor);

        progress = (ProgressBar) view.findViewById(R.id.project_reviwe_progress);

        projectImage = (ImageView) view.findViewById(R.id.iv_project_review);

        tv_proj_title = (TextView) view.findViewById(R.id.tv_proj_title);

        tv_supported_amount = (TextView) view.findViewById(R.id.tv_supported_amount);

        tv_review_days_left = (TextView) view.findViewById(R.id.tv_review_days_left);

        tv_review_goal = (TextView) view.findViewById(R.id.tv_review_goal);

        tv_overview = (TextView) view.findViewById(R.id.tv_overview);

        tv_review_community_detail = (TextView) view.findViewById(R.id.tv_review_community_detail);

        iv_review_user_image = (BezelImageView) view.findViewById(R.id.iv_review_user_image);

        iv_community_user = (BezelImageView) view.findViewById(R.id.iv_community_user);

        tv_review_user_name = (TextView) view.findViewById(R.id.tv_review_user_name);

        tv_project_desc = (TextView) view.findViewById(R.id.tv_project_desc);

        tv_review_community_more_info = (TextView) view.findViewById(R.id.tv_review_community_more_info);
        tv_review_community_more_info.setOnClickListener(this);

        btn_review_contact_me = (Button) view.findViewById(R.id.btn_review_contact_me);
        btn_review_contact_me.setOnClickListener(this);

        btn_submit_project = (Button) view.findViewById(R.id.btn_submit_project);
        btn_submit_project.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.tv_review_community_more_info:
                //startActivity(new Intent(activity, MoreInfoActivity.class));
                break;

            case R.id.btn_review_contact_me:
                //startActivity(new Intent(activity, AskQuestionActivity.class));
                break;

            case R.id.btn_submit_project:
                if (isInternetConnected()) {
                    submitProject();
                } else {
                    showNetworkDialog();
                }
                break;

            default:
                break;
        }
    }

    public void dialogWithSingleButton(int resourceID, String title, String subTitle, String btnText, int btnResourceID) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_single_button);
        dialog.setCanceledOnTouchOutside(true);

        Button btn_dialog;
        TextView tv_dialog_title, tv_dialog_subtitle;
        ImageView img_dialog;

        img_dialog = (ImageView) dialog.findViewById(R.id.img_dialog);
        tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        tv_dialog_subtitle = (TextView) dialog.findViewById(R.id.tv_dialog_subtitle);
        btn_dialog = (Button) dialog.findViewById(R.id.btn_dialog);

        String dialogBtnText;
        dialogBtnText = btnText;

        btn_dialog.setBackgroundResource(btnResourceID);
        btn_dialog.setText(dialogBtnText);

        tv_dialog_subtitle.setText(subTitle);

        tv_dialog_title.setText(title);

        img_dialog.setImageResource(resourceID);

        LinearLayout dialog_main_container = (LinearLayout) dialog.findViewById(R.id.dialog_main_container);
        if (!isTabletDevice())
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(activity) * 80) / 100;
        else {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(activity) * 40) / 100;
        }

        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, HomeScreenActivity.class));
                activity.finish();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void reviewProject() {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //showLoading();
                if (progressDialog != null && !progressDialog.isShowing())
                    progressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", Urls.API_KEY);
                    jsonObject.put("user_id", getUser().id);
                    jsonObject.put("user_token", getUser().user_token);
                    jsonObject.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.REVIEW_PROJECT, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject response) {
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("msg");
                        if (status) {
                            JSONObject dataJsonObject = jsonObject.getJSONObject("data");
                            JSONObject projectJsonObject = dataJsonObject.getJSONObject("Project");
                            JSONObject userJsonObject = dataJsonObject.getJSONObject("User");
                            JSONObject storyJsonObject = dataJsonObject.getJSONObject("Story");

                            projectStory = new Gson().fromJson(storyJsonObject.toString(), ProjectStory.class);

                            ArrayList<AddFAQ> faqList = new Gson().fromJson(projectStory.question_answers,
                                    new TypeToken<List<AddFAQ>>() {
                                    }.getType());

                            JSONArray rewardsJsonArray = dataJsonObject.getJSONArray("Reward");
                            for (int i = 0; i < rewardsJsonArray.length(); i++) {
                                Rewards rewards = new Gson().fromJson(rewardsJsonArray.getJSONObject(i).getJSONObject("Reward").toString(), Rewards.class);
                                rewardsList.add(rewards);
                            }

                            Community community = new Gson().fromJson(dataJsonObject.getJSONObject("Community").toString(), Community.class);

                            GetProjects projects = new Gson().fromJson(projectJsonObject.toString(), GetProjects.class);

                            if (!TextUtils.isNullOrEmpty(projects.image_url)) {
                                displayImage(activity, projectImage, projects.image_url,
                                        ContextCompat.getDrawable(activity, R.drawable.server_error_placeholder));
                            }

                            tv_proj_title.setText(projects.title);

                            tv_project_desc.setText(projects.description);

                            tv_supported_amount.setText(String.format("%s%%", projects.total_support_amount));

                            tv_review_goal.setText("$" + projects.goal);

                            tv_review_days_left.setText(projects.period);

                            tv_overview.setText(Html.fromHtml(projectStory.prjdesc));

                            displayImage(activity, iv_review_user_image, userJsonObject.getString("pictureurl"),
                                    ContextCompat.getDrawable(activity, R.drawable.user_pic));

                            displayImage(activity, iv_community_user, community.image_url,
                                    ContextCompat.getDrawable(activity, R.drawable.no_image_icon));

                            tv_review_user_name.setText(userJsonObject.getString("name"));

                            tv_review_community_detail.setText(community.description);

                            if (isTabletDevice()) {
                                tv_review_risk_challenges.setText(projectStory.riskchallenges);

                                list_review_faq.setExpanded(true);
                                list_review_faq.setAdapter(new QueAnsAdapter(activity, faqList));

                                tv_review_user_bio.setText(getUser().bio);

                                list_review_rewards.setExpanded(true);
                                list_review_rewards.setAdapter(new ReviewProjectRewardsAdapter(activity, rewardsList, projects));

                            } else {
                                if (isAdded())
                                    tv_by_user.setText(getString(R.string.by) + " " + userJsonObject.getString("name"));
                                tv_category_name.setText(projects.category_name);
                                tv_project_review_location.setText(String.format("%s,%s", projects.city_name, projects.country_name));
                                tv_review_user_location.setText(String.format("%s,%s", projects.city_name, projects.country_name));
                                if (isAdded())
                                    tv_review_user_join_date.setText(getString(R.string.joined) + Utils.parseDateToMMMyyyy(getUser().created));

                                if (TextUtils.isNullOrEmpty(community.email)) {
                                    iv_review_community_email.setVisibility(View.GONE);
                                } else {
                                    iv_review_community_email.setVisibility(View.VISIBLE);
                                }

                                if (TextUtils.isNullOrEmpty(community.websiteURL)) {
                                    iv_review_community_web.setVisibility(View.GONE);
                                } else {
                                    iv_review_community_web.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            if (message.equalsIgnoreCase("User Not Found")) {
                                showSessionDialog();
                            } else if (message.equalsIgnoreCase("Data Not Found")) {
                                showDataNotFoundDialog();
                            } else {
                                showServerErrorDialog();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showServerErrorDialog();
                    }
                } else {
                    showServerErrorDialog();
                }
            }

            @Override
            protected void onCancelled() {
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }.execute();
    }

    public void submitProject() {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //showLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", Urls.API_KEY);
                    jsonObject.put("user_id", getUser().id);
                    jsonObject.put("user_token", getUser().user_token);
                    jsonObject.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.SUBMIT_PROJECT, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject response) {
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("msg");
                        if (status) {
                            if (isAdded())
                                dialogWithSingleButton(R.drawable.right_icon, getString(R.string.success), getString(R.string.your_project_has_been_successfully), getString(R.string.home), R.drawable.btn_bg_green);
                        } else {
                            if (message.equalsIgnoreCase("User Not Found")) {
                                showSessionDialog();
                            } else if (message.equalsIgnoreCase("Data Not Found")) { //Please complete all steps before submitting project
                                showDataNotFoundDialog();
                            } else if (message.equalsIgnoreCase("Please complete all steps before submitting project")) { //Please complete all steps before submitting project
                                if (isAdded())
                                    showCustomeDialog(R.drawable.icon_error, getString(R.string.fail), message, getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                            } else {
                                showServerErrorDialog();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showServerErrorDialog();
                    }
                } else {
                    showServerErrorDialog();
                }
            }

            @Override
            protected void onCancelled() {
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }.execute();
    }
}
