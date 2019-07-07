package com.jaribha.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.adapters.ProjectsAdapter;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.ExpandableHeightListView;
import com.jaribha.customviews.HorizontalListView;
import com.jaribha.models.GetCommunities;
import com.jaribha.models.GetProjects;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunityDetailActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView iv_close, iv_email, iv_fb, iv_twitter, iv_insta, iv_phone, iv_web;

    TextView tv_title, tv_desc, tv_proj_title, tv_comm_name;

    BezelImageView img_user_image;

    private HorizontalListView projectListView;

    private ExpandableHeightListView listView;

    private String communityId;

    private GetCommunities getCommunities;

    private ProjectsAdapter adapter;

    private ArrayList<GetProjects> projectList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_details);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.community_details));

        tv_desc = (TextView) findViewById(R.id.commDetailDesc);

        tv_proj_title = (TextView) findViewById(R.id.commProjectTitle);

        tv_comm_name = (TextView) findViewById(R.id.commDetailTitle);

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(this);

        iv_email = (ImageView) findViewById(R.id.commDetailEmail);
        iv_email.setVisibility(View.GONE);
        iv_email.setOnClickListener(this);

        iv_phone = (ImageView) findViewById(R.id.commDetailPhone);
        iv_phone.setVisibility(View.GONE);
        iv_phone.setOnClickListener(this);

        iv_web = (ImageView) findViewById(R.id.commDetailWeb);
        iv_web.setVisibility(View.GONE);
        iv_web.setOnClickListener(this);

        iv_twitter = (ImageView) findViewById(R.id.commDetailTwit);
        iv_twitter.setVisibility(View.GONE);
        iv_twitter.setOnClickListener(this);

        iv_insta = (ImageView) findViewById(R.id.commDetailInsta);
        iv_insta.setVisibility(View.GONE);
        iv_insta.setOnClickListener(this);

        iv_fb = (ImageView) findViewById(R.id.commDetailFb);
        iv_fb.setVisibility(View.GONE);
        iv_fb.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.VISIBLE);
        img_user_image.setOnClickListener(this);
        displayUserImage(img_user_image);

        adapter = new ProjectsAdapter(getActivity(), projectList);

        if (!isTablet()) {
            listView = (ExpandableHeightListView) findViewById(R.id.commMoreProjList);
            listView.setAdapter(adapter);
            listView.setExpanded(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(CommunityDetailActivity.this, ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", projectList.get(position));
                    JaribhaPrefrence.setPref(CommunityDetailActivity.this, Constants.CREATOR_ID, projectList.get(position).user_id);
                    JaribhaPrefrence.setPref(CommunityDetailActivity.this, Constants.PROJECT_TITLE, projectList.get(position).title);
                    JaribhaPrefrence.setPref(CommunityDetailActivity.this, Constants.PROJECT_ID, projectList.get(position).id);
                    JaribhaPrefrence.setPref(getActivity(), Constants.END_DATE, projectList.get(position).enddate);
                    startActivity(intent);
                }
            });
        } else {
            projectListView = (HorizontalListView) findViewById(R.id.commMoreProjList);
            projectListView.setAdapter(adapter);

            projectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(CommunityDetailActivity.this, ProjectDetailsTabs.class);
                    intent.putExtra("detail_object", projectList.get(position));
                    JaribhaPrefrence.setPref(CommunityDetailActivity.this, Constants.CREATOR_ID, projectList.get(position).user_id);
                    JaribhaPrefrence.setPref(CommunityDetailActivity.this, Constants.PROJECT_TITLE, projectList.get(position).title);
                    JaribhaPrefrence.setPref(CommunityDetailActivity.this, Constants.PROJECT_ID, projectList.get(position).id);
                    JaribhaPrefrence.setPref(getActivity(), Constants.END_DATE, projectList.get(position).enddate);
                    startActivity(intent);
                }
            });
        }

        if (getIntent().getExtras() != null) {
            getCommunities = (GetCommunities) getIntent().getSerializableExtra("community_object");

            communityId = getCommunities.id;
            tv_desc.setText(getCommunities.description);
            if (isArabic())
                tv_comm_name.setText(getCommunities.name_ara);
            else
                tv_comm_name.setText(getCommunities.name_eng);

            tv_proj_title.setText(getString(R.string.projects_under) + getCommunities.name_eng);

            if (!TextUtils.isNullOrEmpty(getCommunities.email)) {
                iv_email.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isNullOrEmpty(getCommunities.facebookURL)) {
                iv_fb.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isNullOrEmpty(getCommunities.websiteURL)) {
                iv_web.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isNullOrEmpty(getCommunities.twitterURL)) {
                iv_twitter.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isNullOrEmpty(getCommunities.instagramURL)) {
                iv_insta.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isNullOrEmpty(getCommunities.mobile)) {
                iv_phone.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isInternetConnected())
            loadProjects();
        else {
            showNetworkDialog();
        }
    }

    public void loadProjects() {
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
            projectJson.put("community_id", communityId);

            GetProjectsAPI mAuthTask = new GetProjectsAPI(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {

            case R.id.iv_close:
                finish();
                break;
            case R.id.img_user_image:
                if (getUser() != null)
                    startActivity(new Intent(getActivity(), MyPortfolioActivity.class));
                else
                    startActivity(new Intent(getActivity(), LoginScreenActivity.class));
                break;
            case R.id.commDetailEmail:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getCommunities.email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send via"));
                break;
            case R.id.commDetailFb:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("web_url", getCommunities.facebookURL);
                intent.putExtra("title", "Facebook");
                startActivity(intent);
                break;
            case R.id.commDetailPhone:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    phoneCall();
                } else {
                    requestPhoneCall();
                }
                break;
            case R.id.commDetailWeb:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("web_url", getCommunities.websiteURL);
                intent.putExtra("title", "Website");
                startActivity(intent);
                break;
            case R.id.commDetailInsta:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("web_url", getCommunities.instagramURL);
                intent.putExtra("title", "Instagram");
                startActivity(intent);
                break;
            case R.id.commDetailTwit:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("web_url", getCommunities.twitterURL);
                intent.putExtra("title", "Twitter");
                startActivity(intent);
                break;

            default:
                break;

        }
    }

    public class GetProjectsAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetProjectsAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
            projectList.clear();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECTS_BY_COMMUNITY, nameValuePairs);
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
                        JSONArray jsonArray = jsonObject.optJSONArray("data");
                        if (jsonArray != null && jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                GetProjects data = new Gson().fromJson(jsonArray.getJSONObject(i).optJSONObject("Project").toString(), GetProjects.class);
                                data.status = Utils.setProjectStatus(data);
                                data.days_left = Utils.getDaysLeft(data);
                                projectList.add(data);
                            }
                            if (!isTablet())
                                listView.setVisibility(View.VISIBLE);
                            else
                                projectListView.setVisibility(View.VISIBLE);

                            adapter.notifyDataSetChanged();

                        } else {
                            tv_proj_title.setText(getString(R.string.no_projects_available_under) + getCommunities.name_eng);
                            if (!isTablet())
                                listView.setVisibility(View.VISIBLE);
                            else
                                projectListView.setVisibility(View.VISIBLE);

                        }

                    } else {
                        tv_proj_title.setText(getString(R.string.no_projects_available_under) + getCommunities.name_eng);
                        if (!isTablet())
                            listView.setVisibility(View.VISIBLE);
                        else
                            projectListView.setVisibility(View.VISIBLE);

                        if (msg.equals("User Not Found")) {
                            showSessionDialog();
                        } /*else {
                            showServerDialogDialog();
                        }*/
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
        callIntent.setData(Uri.parse("tel:" + getCommunities.mobile));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), okListener)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
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
