package com.jaribha.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaribha.R;
import com.jaribha.activity.AskQuestionActivity;
import com.jaribha.activity.BecomeSponserActivity;
import com.jaribha.activity.CommunityDetailActivity;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.activity.MoreInfoActivity;
import com.jaribha.activity.ProjectByActivity;
import com.jaribha.activity.ProjectDetailsTabs;
import com.jaribha.activity.WebViewActivity;
import com.jaribha.adapters.QueAnsAdapter;
import com.jaribha.adapters.ReviewProjectRewardsAdapter;
import com.jaribha.adapters.SponsorListAdapter;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.ExpandableHeightListView;
import com.jaribha.interfaces.AddFavourite;
import com.jaribha.interfaces.MyTabInterface;
import com.jaribha.interfaces.SponsersInterface;
import com.jaribha.models.AddFAQ;
import com.jaribha.models.GetCommunities;
import com.jaribha.models.GetProjects;
import com.jaribha.models.ProjectStory;
import com.jaribha.models.Rewards;
import com.jaribha.models.Sponsors;
import com.jaribha.models.UserData;
import com.jaribha.server_communication.AddFavouriteTask;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectDetailInfoFragmentNew extends BaseFragment implements View.OnClickListener {

    private ImageView projectByEmail, projectByPhone, projectByInsta, projectByFb, projectByTwit, projectByWeb, community_email, community_Web, projectImage;

    private BezelImageView communityImage, projectByImage;

    Button btn_become_sponsor, contactMe, supportBtn, sponserBtn;

    TextView tv_desc_more_info, tv_review_risk_challenges, tv_community_more_info, tv_desc_more_infoOverView, tv_project_type;

    HtmlTextView projectOverView;

    private TextView tv_status, tv_proj_title, tv_fund_date, tv_supp_amt, tv_goal_amt, tv_days_left,
            tv_proj_desc, tv_communityName, tv_communityDesc, tv_project_by_name, tv_project_by_loc, tv_project_by_date, noSponsorTv, tv_count_supp;

    private GetProjects projectData;

    ExpandableHeightListView sponsorList, rewardListView, list_review_faq;

    private ProgressBar progressBar;

    private SponsorListAdapter sponsorListAdapter;

    private ImageView favImage;

    private int supportCount;

    private int sponCount;

    private ReviewProjectRewardsAdapter rewardsListAdapter;

    private ArrayList<Rewards> rewardsList = new ArrayList<>();

    private UserData userInfo;

    private GetCommunities communityInfo;

    private ProjectStory projectStory;

    private ProgressDialog progressDialog;

    private Activity activity;

    boolean isTablet;

    SponsersInterface sponsersInterface;

    String sponserid="";

    ArrayList<String> sponserIdarray=new ArrayList<>();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;

        if(activity instanceof SponsersInterface)
        {
            sponsersInterface=(SponsersInterface)activity;
        }
        else
        {
            throw new ClassCastException();
        }
    }

   /* public static ProjectDetailInfoFragmentNew newInstance(GetProjects project) {
        Bundle args = new Bundle();
        args.putSerializable("project_info", project);
        ProjectDetailInfoFragmentNew fragment = new ProjectDetailInfoFragmentNew();
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (Utils.isInternetConnected(activity)) {
                loadUpdates();
                loadSponsors();
            } else {
                showNetworkDialog();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //projectData = (GetProjects) getArguments().getSerializable("project_info");
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_detail_info, null);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        isTablet = getResources().getBoolean(R.bool.isTablet);

        sponsorList = (ExpandableHeightListView) view.findViewById(R.id.sponsorList);

        btn_become_sponsor = (Button) view.findViewById(R.id.btn_become_sponsor);
        btn_become_sponsor.setOnClickListener(this);

        supportBtn = (Button) view.findViewById(R.id.supportBtn);
        supportBtn.setOnClickListener(this);

        if (isAdded()) {
            supportBtn.setText(activity.getResources().getString(R.string.support_project));
        }


        noSponsorTv = (TextView) view.findViewById(R.id.no_sponsorText);
        noSponsorTv.setVisibility(View.GONE);

        projectImage = (ImageView) view.findViewById(R.id.projectImage);
        projectImage.setOnClickListener(this);

        projectByImage = (BezelImageView) view.findViewById(R.id.profileImage);
        projectByImage.setOnClickListener(this);

        if (!isTablet) {

            sponserBtn = (Button) view.findViewById(R.id.sponserBtn);
            sponserBtn.setOnClickListener(this);

            if (isAdded())
                sponserBtn.setText(activity.getResources().getString(R.string.become_a_sponsor));

            favImage = (ImageView) view.findViewById(R.id.favDetailIv);
            favImage.setOnClickListener(this);
            // overViewHeading = (TextView) view.findViewById(R.id.projOverviewHeading);
            //  projectOverView = (TextView) view.findViewById(R.id.projectDetailOverView);
            projectByEmail = (ImageView) view.findViewById(R.id.projectByEmail);
            projectByEmail.setOnClickListener(this);

            projectByFb = (ImageView) view.findViewById(R.id.projectByFb);
            projectByFb.setOnClickListener(this);

            projectByTwit = (ImageView) view.findViewById(R.id.projectByTwit);
            projectByTwit.setOnClickListener(this);

            projectByPhone = (ImageView) view.findViewById(R.id.projectByPhone);
            projectByPhone.setOnClickListener(this);

            projectByInsta = (ImageView) view.findViewById(R.id.projectByInsta);
            projectByInsta.setOnClickListener(this);

            projectByWeb = (ImageView) view.findViewById(R.id.projectByWeb);
            projectByWeb.setOnClickListener(this);

            community_email = (ImageView) view.findViewById(R.id.communityEmail);
            community_email.setOnClickListener(this);

            community_Web = (ImageView) view.findViewById(R.id.communityWeb);
            community_Web.setOnClickListener(this);

            tv_fund_date = (TextView) view.findViewById(R.id.fundDate);
            tv_status = (TextView) view.findViewById(R.id.projDetailStatus);

            tv_desc_more_infoOverView = (TextView) view.findViewById(R.id.tv_desc_more_infoOverView);
            tv_desc_more_infoOverView.setVisibility(View.GONE);
            tv_desc_more_infoOverView.setOnClickListener(this);

            tv_desc_more_info = (TextView) view.findViewById(R.id.tv_desc_more_info);
            tv_desc_more_info.setOnClickListener(this);

        } else {
            list_review_faq = (ExpandableHeightListView) view.findViewById(R.id.list_review_faq);
            tv_review_risk_challenges = (TextView) view.findViewById(R.id.tv_review_risk_challenges);

            rewardListView = (ExpandableHeightListView) view.findViewById(R.id.rewardTabsList);
            rewardListView.setExpanded(true);

        }


        projectOverView = (HtmlTextView) view.findViewById(R.id.tv_overview);

        progressBar = (ProgressBar) view.findViewById(R.id.projectProgress);

        communityImage = (BezelImageView) view.findViewById(R.id.communityPic);
        communityImage.setOnClickListener(this);

        sponsorList.setExpanded(true);
        sponsorListAdapter = new SponsorListAdapter(activity, sponsorsArrayList);
        sponsorList.setAdapter(sponsorListAdapter);

        tv_proj_title = (TextView) view.findViewById(R.id.projDetailTitle);

        tv_days_left = (TextView) view.findViewById(R.id.projDetailDays);

        tv_project_type = (TextView) view.findViewById(R.id.projDetailType);

        tv_supp_amt = (TextView) view.findViewById(R.id.projDetailSuppAmt);

        tv_goal_amt = (TextView) view.findViewById(R.id.proDetailGoalAmt);

        tv_count_supp = (TextView) view.findViewById(R.id.suppCount);

        tv_proj_desc = (TextView) view.findViewById(R.id.projDesc);

        tv_project_by_name = (TextView) view.findViewById(R.id.projectName);

        tv_project_by_loc = (TextView) view.findViewById(R.id.projectLocation);

        tv_project_by_date = (TextView) view.findViewById(R.id.projectDate);

        tv_communityName = (TextView) view.findViewById(R.id.communityTitle);

        tv_communityDesc = (TextView) view.findViewById(R.id.communityDesc);

        tv_community_more_info = (TextView) view.findViewById(R.id.tv_community_more_info);
        tv_community_more_info.setOnClickListener(this);

        contactMe = (Button) view.findViewById(R.id.contactMe);
        contactMe.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        MyTabInterface callBack = (MyTabInterface) activity;
        hideKeyBoard(v);
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_become_sponsor:
                if (getUser() != null) {
                    if (projectData != null)
                        if (projectData.status.equals("complete")) {
                            if (isAdded())
                                showCustomeDialog(R.drawable.icon_error, getString(R.string.campaign_ended), getString(R.string.you_cant_sponsor), getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                        } else if (projectData.status.equals("supported") && Utils.isProjectExpired(projectData.enddate)) {
                            if (isAdded())
                                showCustomeDialog(R.drawable.icon_error, getString(R.string.campaign_ended), getString(R.string.you_cant_sponsor), getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                        } else {
                            intent = new Intent(activity, BecomeSponserActivity.class);
                            startActivity(intent);
                        }
                } else {
                    Intent loginIntent = new Intent(activity, LoginScreenActivity.class);
                    //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(loginIntent);
                    //activity.finish();
                }
                break;

            case R.id.communityPic:
                if (communityInfo != null) {
                    Intent communityDetails = new Intent(activity, CommunityDetailActivity.class);
                    communityDetails.putExtra("community_object", communityInfo);
                    startActivity(communityDetails);
                }
                break;

            case R.id.favDetailIv:
                if (getUser() != null) {
                    if (Utils.isInternetConnected(activity)) {
                        if (projectData != null)
                            manageFavourite(projectData);
                    } else
                        showNetworkDialog();
                } else {
                    Intent loginIntent = new Intent(activity, LoginScreenActivity.class);
                    activity.startActivity(loginIntent);
                    //activity.overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                }

                break;
            case R.id.projectImage:
                if (projectStory != null)
                    if (!TextUtils.isNullOrEmpty(projectStory.video)) {

                        if (projectStory.video.contains("youtu") || projectStory.video.contains("vimeo")) {
                            if (projectStory.video.contains("youtu")) {
                                try {

                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(projectStory.video)));
//                                    Utils.watchYoutubeVideo(activity, extractYoutubeId(projectStory.video));

                                } catch (Exception e) {
                                    Crashlytics.logException(e);
                                    e.printStackTrace();
                                    if (isAdded())
                                        showToast(getString(R.string.invalid_video_url));
                                }
                            } else {
                                Utils.watchVimeoVideo(activity, projectStory.video.substring(projectStory.video.lastIndexOf("/") + 1));
                            }
                        } else {
                            if (isAdded())
                                showToast(getString(R.string.invalid_video_url));
                        }
                    }

                break;

            case R.id.projectByEmail:
                if (userInfo != null) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", userInfo.email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send via"));
            }
                break;
            case R.id.projectByFb:
                if (userInfo != null) {
                    intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra("web_url", userInfo.facebook);
                    intent.putExtra("title", "Facebook");
                    startActivity(intent);
                }
                break;
            case R.id.projectByInsta:
                if (userInfo != null)
                break;{
                intent = new Intent(activity, WebViewActivity.class);
                intent.putExtra("web_url", userInfo.instagram);
                intent.putExtra("title", "Instagram");
                startActivity(intent);
            }
            case R.id.projectByPhone:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    phoneCall();
                } else {
                    requestPhoneCall();
                }
                break;
            case R.id.projectByTwit:
                if (userInfo != null) {
                    intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra("web_url", userInfo.twitter);
                    intent.putExtra("title", "Twitter");
                    startActivity(intent);
                }
                break;

            case R.id.communityEmail:
                if (communityInfo != null) {
                    Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", communityInfo.email, null));
                    email.putExtra(Intent.EXTRA_SUBJECT, "");
                    email.putExtra(Intent.EXTRA_TEXT, "");
                    startActivity(Intent.createChooser(email, "Send via"));
                }
                break;
            case R.id.communityWeb:
                if (communityInfo != null) {
                    intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra("web_url", communityInfo.websiteURL);
                    intent.putExtra("title", "Website");
                    startActivity(intent);
                }
                break;
            case R.id.projectByWeb:
                if (userInfo != null) {
                    intent = new Intent(activity, WebViewActivity.class);
                    intent.putExtra("web_url", userInfo.website);
                    intent.putExtra("title", "Website");
                    startActivity(intent);
                }
                break;
            case R.id.sponserBtn:
                try {
                    if (getUser() != null) {
                        if (projectData != null)
                            if (projectData.status.equals("complete")) {
                                if (isAdded())
                                    showCustomeDialog(R.drawable.icon_error, getString(R.string.campaign_ended), getString(R.string.you_cant_sponsor), getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                            } else if (projectData.status.equals("supported") && Utils.isProjectExpired(projectData.enddate)) {
                                if (isAdded())
                                    showCustomeDialog(R.drawable.icon_error, getString(R.string.become_a_sponser), getString(R.string.you_cant_sponsor), getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                            } else {
                                Intent intent1 = new Intent(activity, BecomeSponserActivity.class);
                                startActivity(intent1);
                            }
                    } else {
                        Intent loginIntent = new Intent(activity, LoginScreenActivity.class);
                        //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(loginIntent);
                        //activity.finish();
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
                break;

            case R.id.supportBtn:
                try {
                    if (getUser() != null) {
                        if (projectData != null)
                            if (projectData.status.equals("complete")) {
                                if (isAdded())
                                    showCustomeDialog(R.drawable.icon_error,
                                            getString(R.string.campaign_ended),
                                            getString(R.string.you_cant_support),
                                            getString(R.string.dgts__okay),
                                            R.drawable.btn_bg_green);
                            } else if (projectData.status.equals("supported") && Utils.isProjectExpired(projectData.enddate)) {
                                if (isAdded())
                                    showCustomeDialog(R.drawable.icon_error,
                                            getString(R.string.support_project),
                                            getString(R.string.you_cant_support),
                                            getString(R.string.dgts__okay),
                                            R.drawable.btn_bg_green);
                            } else {
                                Constants.supportSelected = true;
                                if (!isTablet)
                                    callBack.setMyTab(1);
                            }
                    } else {
                        ((ProjectDetailsTabs) getActivity()).guest_check_save();
                        Intent loginIntent = new Intent(activity, LoginScreenActivity.class);
                        //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(loginIntent);
                        //activity.finish();
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
                break;

            case R.id.tv_desc_more_infoOverView:
                if (projectStory != null) {
                    Intent moreinfoIntent = new Intent(activity, MoreInfoActivity.class);
                    moreinfoIntent.putExtra("more_info", projectStory);
                    startActivity(moreinfoIntent);
                }
                break;
            case R.id.tv_community_more_info:
                if (communityInfo != null) {
                    Intent communityDetail = new Intent(activity, CommunityDetailActivity.class);
                    communityDetail.putExtra("community_object", communityInfo);
                    startActivity(communityDetail);
                }
                break;

            case R.id.contactMe:
                try {
                    if (getUser() != null) {
                        if (!getUser().id.equals(userInfo.id)) {
                            Intent askIntent = new Intent(activity, AskQuestionActivity.class);
                            askIntent.putExtra("receiver_id", userInfo.id);
                            askIntent.putExtra("receiver_name", userInfo.name);
                            askIntent.putExtra("project_name", projectData.title);
                            startActivity(askIntent);
                        } else {
                            if (isAdded())
                                showCustomeDialog(R.drawable.icon_error, getString(R.string.error), getString(R.string.you_cant_ask), getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                        }
                    } else {
                        Intent loginIntent = new Intent(activity, LoginScreenActivity.class);
                        //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(loginIntent);
                        //activity.finish();
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
                break;

            case R.id.profileImage:
                if (userInfo != null) {
                    Intent proIntent = new Intent(activity, ProjectByActivity.class);
                    proIntent.putExtra("user_info", userInfo);
                    startActivity(proIntent);
                }
                break;

            default:
                break;
        }
    }

    public void loadUpdates() {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            String uid = getUser() == null ? "" : getUser().id;
            String utoken = getUser() == null ? "" : getUser().user_token;

            projectJson.put("user_id", uid);
            projectJson.put("user_token", utoken);
            projectJson.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));

            GetProjectInfo mAuthTask = new GetProjectInfo(projectJson);
            mAuthTask.execute();
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    public class GetProjectInfo extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetProjectInfo(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showLoading();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECT_DETAIL, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            //  hideLoading();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {

                        projectData = new Gson().fromJson(jsonObject.optJSONObject("data").optJSONObject("Project").toString(), GetProjects.class);
                        projectData.status = Utils.setProjectStatus(projectData);
                        projectData.days_left = Utils.getDaysLeft(projectData);

                        userInfo = new Gson().fromJson(jsonObject.optJSONObject("data").optJSONObject("User").toString(), UserData.class);

                        if (!jsonObject.optJSONObject("data").optJSONObject("Community").has("data")) {
                            communityInfo = new Gson().fromJson(jsonObject.optJSONObject("data").optJSONObject("Community").toString(), GetCommunities.class);
                        }

                        projectStory = new Gson().fromJson(jsonObject.optJSONObject("data").optJSONObject("Story").toString(), ProjectStory.class);
                        if (projectStory != null && !isTablet)
                            tv_desc_more_infoOverView.setVisibility(View.VISIBLE);

                        try {
                            if (!TextUtils.isNullOrEmpty(userInfo.pictureurl))
                                displayImage(activity, projectByImage, userInfo.pictureurl, ContextCompat.getDrawable(activity, R.drawable.user_pic));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (jsonObject.optJSONObject("data").optJSONArray("Supporters") != null) {
                            supportCount = jsonObject.optJSONObject("data").optJSONArray("Supporters").length();
                        }

                        ArrayList<AddFAQ> faqList = new ArrayList<>();

                        try {
                            if (projectStory.question_answers.startsWith("[")) {
                                ArrayList<AddFAQ> list = new Gson().fromJson(projectStory.question_answers,
                                        new TypeToken<List<AddFAQ>>() {
                                        }.getType());
                                faqList.addAll(list);
                            }
                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                        }

                        if (communityInfo != null) {
                            if (!TextUtils.isNullOrEmpty(communityInfo.image_url))
                                displayImage(activity, communityImage, communityInfo.image_url, ContextCompat.getDrawable(activity, R.drawable.comunity_pic_demo));

                            tv_communityName.setText(communityInfo.name_eng);
                            tv_communityDesc.setText(communityInfo.description);

                        } else {

                            if (!isTablet) {
                                community_Web.setVisibility(View.GONE);
                                community_email.setVisibility(View.GONE);
                            }
                            tv_community_more_info.setVisibility(View.GONE);

                            tv_communityName.setVisibility(View.GONE);
                            communityImage.setVisibility(View.GONE);
                            if (isAdded())
                                tv_communityDesc.setText(getString(R.string.no_community));
                        }

                        JaribhaPrefrence.setPref(activity, Constants.PROJECT_STATUS, projectData.status);

                        //if (!TextUtils.isNullOrEmpty(userInfo.name))
                        tv_project_by_name.setText(userInfo.name);

//                        double supportAmt = Double.parseDouble(projectData.total_support_amount);
//                        double goalAmt = Double.parseDouble(projectData.goal);
//
//                        if (!projectData.status.equalsIgnoreCase("publish")) {
//                            if ((supportAmt < goalAmt)) {
//                                projectData.status = "complete";
//                            } else if (supportAmt >= goalAmt) {
//                                projectData.status = "supported";
//                            }
//                        }

                        if (!isTablet) {

                            String time = Utils.formateDateFromstring(activity, "yyyy-MM-dd", "dd-MMM-yyyy", projectData.enddate);
                            if (isAdded())
                                tv_fund_date.setText(String.format("%s %s", activity.getResources().getString(R.string.project_to_be_funded_by), time));

                            switch (projectData.status) {
                                case "supported":
                                    if (isAdded())
                                        tv_status.setText(activity.getResources().getString(R.string.successfully_supported));
                                    tv_status.setBackgroundResource(R.drawable.green_strip_transparent);
                                    break;
                                case "complete":
                                    tv_status.setBackgroundResource(R.drawable.red_strip);
                                    if (isAdded())
                                        tv_status.setText(activity.getResources().getString(R.string.campaign_ended));
                                    break;
                                default:
                                    tv_status.setVisibility(View.INVISIBLE);
                                    break;
                            }

                            if (projectData.is_favourite) {
                                favImage.setImageResource(R.drawable.ic_fav_selected);
                            } else {
                                favImage.setImageResource(R.drawable.fav_button);
                            }

                            if (isAdded())
                                tv_project_by_date.setText(String.format("%s%s%s%s", getString(R.string.joined), Utils.parseDateToMMMyyyy(userInfo.created).split(" ")[0], getString(R.string.at), Utils.parseDateToMMMyyyy(userInfo.created).split(" ")[1]));

                            tv_project_by_loc.setText(userInfo.address);

                            if (TextUtils.isNullOrEmpty(userInfo.email)) {
                                projectByEmail.setVisibility(View.GONE);
                            }
                            if (TextUtils.isNullOrEmpty(userInfo.facebook)) {
                                projectByFb.setVisibility(View.GONE);
                            }
                            if (TextUtils.isNullOrEmpty(userInfo.website)) {
                                projectByWeb.setVisibility(View.GONE);
                            }
                            if (TextUtils.isNullOrEmpty(userInfo.twitter)) {
                                projectByTwit.setVisibility(View.GONE);
                            }
                            if (TextUtils.isNullOrEmpty(userInfo.phone)) {
                                projectByPhone.setVisibility(View.GONE);
                            }
                            if (TextUtils.isNullOrEmpty(userInfo.instagram)) {
                                projectByInsta.setVisibility(View.GONE);
                            }
                        } else {
                            switch (projectData.status) {
                                case "complete":
                                    if (isAdded())
                                        supportBtn.setText(activity.getResources().getString(R.string.campaign_ended));
                                    break;
                                case "supported":
                                    if (isAdded())
                                        supportBtn.setText(activity.getResources().getString(R.string.successfully_supported));
                                    supportBtn.setBackgroundResource(R.drawable.btn_bg_green);
//                                supportBtn.setEnabled(false);
                                    break;
                                default:
                                    if (isAdded())
                                        supportBtn.setText(activity.getResources().getString(R.string.support_project));
                                    break;
                            }

                            list_review_faq.setExpanded(true);
                            if (TextUtils.isNullOrEmpty(faqList))
                                list_review_faq.setAdapter(new QueAnsAdapter(activity, faqList));
                            else {
                                list_review_faq.setAdapter(null);
                            }

                            if (!TextUtils.isNullOrEmpty(projectStory.riskchallenges))
                                tv_review_risk_challenges.setText(Html.fromHtml(projectStory.riskchallenges));
                            else {
                                if (isAdded())
                                    tv_review_risk_challenges.setText(getString(R.string.not_found));
                            }
                        }

                        tv_proj_title.setText(projectData.title);
                        tv_days_left.setText(projectData.days_left);
//                        try {
//                            int dayLeft = Utils.getDateDifference(projectData.enddate);
//                            Log.e("day left", dayLeft + ">>>");
//                            if (dayLeft > 0)
//                                tv_days_left.setText(String.valueOf(dayLeft));
//
//                        } catch (ParseException e) {
//                            Crashlytics.logException(e);
//                            e.printStackTrace();
//                        }

                        //tv_days_left.setText(projectData.period);
                        //userInfo.name
                        String by = projectData.project_by == null ? userInfo.name : projectData.project_by;
                        if (isAdded())
                            tv_project_type.setText(String.format("%s%s%s %s%s %s, %s", activity.getResources().getString(R.string.by), by, activity.getResources().getString(R.string.in), projectData.category_name, activity.getResources().getString(R.string.at), projectData.city_name, projectData.country_name));

                        double amount = 0;

                        if (!TextUtils.isNullOrEmpty(projectData.total_support_amount)) {
                            amount = Double.parseDouble(projectData.total_support_amount);
                        }

                        tv_supp_amt.setText(String.format("$%s", new DecimalFormat("##.##").format(amount)));
                        tv_goal_amt.setText(String.format("$%s", projectData.goal));
                        tv_proj_desc.setText(projectData.description);

                        try {
                            if (!TextUtils.isNullOrEmpty(projectData.image_url))
                                displayImage(activity, projectImage, projectData.image_url, ContextCompat.getDrawable(activity, R.drawable.server_error_placeholder));
                        } catch (Exception e) {
                            e.printStackTrace();
                            Crashlytics.logException(e);
                        }

                        if (!TextUtils.isNullOrEmpty(projectData.total_support_amount) && !TextUtils.isNullOrEmpty(projectData.goal))
                            progressBar.setProgress(getProgressPercent(Double.parseDouble(projectData.total_support_amount), Double.parseDouble(projectData.goal)));

                        if (!TextUtils.isNullOrEmpty(projectStory.prjdesc)) {
                            projectOverView.setHtmlFromString(projectStory.prjdesc, new HtmlTextView.RemoteImageGetter());
//                            projectOverView.setText(spannedValue);
                        } else {
                            if (isAdded())
                                projectOverView.setText(getString(R.string.overview_not_found));
                        }

//                        if (Utils.isInternetConnected(activity))
//                            loadSponsors();
//                        else {
//                            //hideLoading();
//                            if (progressDialog != null && progressDialog.isShowing())
//                                progressDialog.dismiss();
//                            showNetworkDialog();
//                        }

                    } else {
                        //hideLoading();
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        switch (msg) {
                            case "Data Not Found":
                                showDataNotFoundDialog();
                                break;
                            case "User Not Found":
                                showSessionDialog();
                                break;
                            case "Project not Found":
                                dialogWithTwoButton();
                                break;
                        }
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    // showServerErrorDialog();
                }
            } else {
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                showServerErrorDialog();
            }
        }

//        @Override
//        protected void onCancelled() {
//            //hideLoading();
//            if (progressDialog != null && progressDialog.isShowing())
//                progressDialog.dismiss();
//        }
    }

    public void dialogWithTwoButton() {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_single_button);
        dialog.setCanceledOnTouchOutside(true);

        TextView title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        TextView subTitle = (TextView) dialog.findViewById(R.id.tv_dialog_subtitle);

        ImageView img = (ImageView) dialog.findViewById(R.id.img_dialog);
        img.setImageResource(R.drawable.icon_error);
        if (isAdded()) {
            title.setText(getResources().getString(R.string.error));
            subTitle.setText(getResources().getString(R.string.proj_not_found));
        }
        Button btnYes = (Button) dialog.findViewById(R.id.btn_dialog);
        if (isAdded())
            btnYes.setText(getResources().getString(R.string.dgts__okay));
        //Button btnNO = (Button) dialog.findViewById(R.id.btn_no);
        LinearLayout dialog_main_container = (LinearLayout) dialog.findViewById(R.id.dialog_main_container);

        if (isTablet) {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(activity) * 40) / 100;
        } else {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(activity) * 80) / 100;
        }

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });

        dialog.show();
    }

    public void loadSponsors() {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);
            projectJson.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));

            SponsorAPI mAuthTask = new SponsorAPI(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<Sponsors> sponsorsArrayList = new ArrayList<>();

    public class SponsorAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        SponsorAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showLoading();
//            if (progressDialog != null && !progressDialog.isShowing())
//                progressDialog.show();

            sponsorsArrayList.clear();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_SPONSORS_BY_PROJECT, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            tv_count_supp.setText(String.valueOf(supportCount + sponCount));
            // hideLoading();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    //  String msg = jsonObject.optString("Sponsors Data");
                    if (status) {
                        JSONArray jsonArray = jsonObject.optJSONObject("data").optJSONArray("Sponsor");
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Sponsors data = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), Sponsors.class);
                                sponsorsArrayList.add(data);

                                sponserid=jsonArray.getJSONObject(i).getString("sponsor_id");
                                sponserIdarray.add(sponserid);
                            }
                            sponsersInterface.getSponsers(sponserIdarray);
                            sponCount = sponsorsArrayList.size();

                            tv_count_supp.setText(String.valueOf(supportCount + sponCount));
                            sponsorListAdapter.notifyDataSetChanged();

                            if (sponsorsArrayList.size() == 0) {
                                noSponsorTv.setVisibility(View.VISIBLE);
//                                if (isAdded())
//                                    noSponsorTv.setText(getString(R.string.no_sponsors_found));
                            }

                        } else {
                            noSponsorTv.setVisibility(View.VISIBLE);
//                            if (isAdded())
//                                noSponsorTv.setText(getString(R.string.no_sponsors_found));
                        }


                    } else {
                        noSponsorTv.setVisibility(View.VISIBLE);
//                        if (isAdded())
//                            noSponsorTv.setText(getString(R.string.no_sponsors_found));
                    }

                    if (isTablet) {
                        if (Utils.isInternetConnected(activity)) {
                            loadRewards();
                        } else {
                            //hideLoading();
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();
                            showNetworkDialog();
                        }
                    } else {
                        //hideLoading();
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    showServerErrorDialog();
                }
            } else {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                showServerErrorDialog();
            }
        }

//        @Override
//        protected void onCancelled() {
//            //hideLoading();
//            if (progressDialog != null && progressDialog.isShowing())
//                progressDialog.dismiss();
//        }
    }

    public void loadRewards() {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            String uid = getUser() == null ? "" : getUser().id;
            String utoken = getUser() == null ? "" : getUser().user_token;

            projectJson.put("user_id", uid);
            projectJson.put("user_token", utoken);
            projectJson.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));

            GetRewardsAPI mAuthTask = new GetRewardsAPI(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetRewardsAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetRewardsAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showLoading();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECT_REWARDS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            //hideLoading();
            try {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            } catch (IllegalArgumentException e){
                //Crashlytics.logException(e);
                e.printStackTrace();
            }
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Rewards data = new Gson().fromJson(jsonArray.getJSONObject(i).optJSONObject("Reward").toString(), Rewards.class);
                            rewardsList.add(data);
                        }
                        rewardsListAdapter = new ReviewProjectRewardsAdapter(activity, rewardsList,projectData);
                        rewardListView.setAdapter(rewardsListAdapter);
                        rewardsListAdapter.notifyDataSetChanged();

                    } else {
                        switch (msg) {
                            case "Data Not Found":
                                showDataNotFoundDialog();
                                break;
                            case "User Not Found":
                                showSessionDialog();
                                break;
                            default:
                                showServerErrorDialog();
                                break;
                        }
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    showServerErrorDialog();
                }
            } else {
                showServerErrorDialog();
            }
        }

//        @Override
//        protected void onCancelled() {
//            //hideLoading();
//            if (progressDialog != null && progressDialog.isShowing())
//                progressDialog.dismiss();
//        }
    }

    private void manageFavourite(final GetProjects object) {
        //showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", ((BaseAppCompatActivity) getContext()).getUser().id);
            jsonObject.put("user_token", ((BaseAppCompatActivity) getContext()).getUser().user_token);
            jsonObject.put("project_id", object.id);
            AddFavouriteTask addFavouriteTask = new AddFavouriteTask(jsonObject, new AddFavourite() {
                @Override
                public void OnSuccess(JSONObject response) {
                    //hideLoading();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (response != null) {
                        try {
                            JSONObject jsonObject = response.getJSONObject("result");
                            Boolean status = jsonObject.getBoolean("status");
                            String msg = jsonObject.optString("msg");
                            if (status) {

                                if (msg.equalsIgnoreCase("Favourite Added")) {
                                    Utils.updateFavoriteCount(activity, 1);
                                    favImage.setImageResource(R.drawable.ic_fav_selected);
                                } else {
                                    Utils.updateFavoriteCount(activity, -1);
                                    favImage.setImageResource(R.drawable.fav_button);
                                }
                            } else {
                                switch (msg) {
                                    case "Data Not Found":
                                        ((BaseAppCompatActivity) getContext()).showDataNotFoundDialog();
                                        break;
                                    case "User Not Found":
                                        ((BaseAppCompatActivity) getContext()).showSessionDialog();
                                        break;
                                    default:
                                        //((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                                        break;
                                }
                            }
                        } catch (JSONException e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                            //hideLoading();
                            if (progressDialog != null && progressDialog.isShowing())
                                progressDialog.dismiss();
                            //((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                        }
                    } else {
                        //hideLoading();
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        //((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                    }
                }

                @Override
                public void OnFail() {
                    //hideLoading();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                }
            });

            addFavouriteTask.execute();
        } catch (JSONException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    protected static String extractYoutubeId(String url) throws MalformedURLException {
        String id = null;
        try {
            String query = new URL(url).getQuery();

            if (query != null) {
                String[] param = query.split("&");

                for (String row : param) {
                    String[] param1 = row.split("=");
                    if (param1[0].equals("v")) {
                        id = param1[1];
                    }
                }
            } else {
                if (url.contains("embed")) {
                    id = url.substring(url.lastIndexOf("/") + 1);
                }
            }
        } catch (Exception ex) {
            Log.e("Exception", ex.toString());
        }
        return id;
    }

    private static final int PHONE_CALL_REQUEST = 18;

    @TargetApi(Build.VERSION_CODES.M)
    private void requestPhoneCall() {

        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("Phone Call");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        PHONE_CALL_REQUEST);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    PHONE_CALL_REQUEST);
            return;
        }

        phoneCall();
    }

    private void phoneCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + userInfo.phone));
        startActivity(callIntent);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), okListener)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PHONE_CALL_REQUEST: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);

                // Check for CALL_PHONE
                if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    phoneCall();
                } else {
                    // Permission Denied
                    showToast("Some Permission is Denied");
                }
            }
            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}