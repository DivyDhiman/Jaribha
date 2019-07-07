package com.jaribha.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.models.ActivityData;
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

public class MyPortfolioActivity extends BaseAppCompatActivity implements View.OnClickListener {

    BezelImageView img_user_image;

    TextView tv_title, tv_favourite, tv_sponsored, tv_supported, tv_created, tv_activity;

    TextView tv_user_name, tv_user_address, tv_join_date, tv_user_bio;

    TextView created_count, supportedCount, sponsorsCount, favouritesCount;

    ImageView edit, iv_close, email_icon, phoneCall_icon, webAddress_icon, fb_icon, twitter_icon, instagram_icon;

    BezelImageView profileImage;

    RelativeLayout activity_Relay, created_Relay, supported_Relay, sponsered_Relay, favourite_Relay;

    Intent intent;

    int resultCode = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_portfolio);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        iv_close = (ImageView) findViewById(R.id.iv_close);
        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        edit = (ImageView) findViewById(R.id.iv_edit);
        tv_title = (TextView) findViewById(R.id.tv_title);

        iv_close.setOnClickListener(this);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setVisibility(View.VISIBLE);

        img_user_image.setVisibility(View.GONE);

        edit.setVisibility(View.VISIBLE);
        edit.setOnClickListener(this);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.my_portfolio));

        activity_Relay = (RelativeLayout) findViewById(R.id.activity_Relay);
        created_Relay = (RelativeLayout) findViewById(R.id.created_Relay);
        supported_Relay = (RelativeLayout) findViewById(R.id.supported_Relay);
        sponsered_Relay = (RelativeLayout) findViewById(R.id.sponsered_Relay);
        favourite_Relay = (RelativeLayout) findViewById(R.id.favourite_Relay);

        tv_favourite = (TextView) findViewById(R.id.tv_favourite);
        tv_sponsored = (TextView) findViewById(R.id.tv_sponsored);
        tv_supported = (TextView) findViewById(R.id.tv_supported);
        tv_created = (TextView) findViewById(R.id.tv_created);
        tv_activity = (TextView) findViewById(R.id.tv_activity);

        created_count = (TextView) findViewById(R.id.count_1);
        supportedCount = (TextView) findViewById(R.id.count_2);
        sponsorsCount = (TextView) findViewById(R.id.count_3);
        favouritesCount = (TextView) findViewById(R.id.count_4);

        tv_favourite.setOnClickListener(this);
        tv_sponsored.setOnClickListener(this);
        tv_supported.setOnClickListener(this);
        tv_created.setOnClickListener(this);
        tv_activity.setOnClickListener(this);

        activity_Relay.setOnClickListener(this);
        created_Relay.setOnClickListener(this);
        supported_Relay.setOnClickListener(this);
        sponsered_Relay.setOnClickListener(this);
        favourite_Relay.setOnClickListener(this);


        if (getUser() != null) {
            profileImage = (BezelImageView) findViewById(R.id.profileImage);
            tv_user_name = (TextView) findViewById(R.id.tv_user_name);
            tv_user_address = (TextView) findViewById(R.id.tv_user_address);
            tv_join_date = (TextView) findViewById(R.id.tv_join_date);
            email_icon = (ImageView) findViewById(R.id.email_icon);

            displayUserImage(profileImage);

            if (!TextUtils.isNullOrEmpty(getUser().name)) {
                tv_user_name.setText(getUser().name);
            }

            if (!TextUtils.isNullOrEmpty(getUser().cityname) && !TextUtils.isNullOrEmpty(getUser().countryname)) {
                tv_user_address.setText(String.format("%s,%s", getUser().cityname, getUser().countryname));
            } else {
                tv_user_address.setVisibility(View.GONE);
            }

            if (!TextUtils.isNullOrEmpty(getUser().created)) {
                tv_join_date.setText(String.format("%s%s%s%s", getString(R.string.joined), Utils.parseDateToMMMyyyy(getUser().created).split(" ")[0], getString(R.string.at), Utils.parseDateToMMMyyyy(getUser().created).split(" ")[1]));
            }

            //Email icon visibility
            if (!TextUtils.isNullOrEmpty(getUser().email)) {
                email_icon.setVisibility(View.VISIBLE);
            } else {
                email_icon.setVisibility(View.GONE);
            }

            phoneCall_icon = (ImageView) findViewById(R.id.phoneCall_icon);
            if (!TextUtils.isNullOrEmpty(getUser().phone)) {
                phoneCall_icon.setVisibility(View.VISIBLE);
            } else {
                phoneCall_icon.setVisibility(View.GONE);
            }

            webAddress_icon = (ImageView) findViewById(R.id.webAddress_icon);
            if (!TextUtils.isNullOrEmpty(getUser().website)) {
                webAddress_icon.setVisibility(View.VISIBLE);
            } else {
                webAddress_icon.setVisibility(View.GONE);
            }

            fb_icon = (ImageView) findViewById(R.id.fb_icon);
            if (!TextUtils.isNullOrEmpty(getUser().facebook)) {
                fb_icon.setVisibility(View.VISIBLE);
            } else {
                fb_icon.setVisibility(View.GONE);
            }

            twitter_icon = (ImageView) findViewById(R.id.twitter_icon);
            if (!TextUtils.isNullOrEmpty(getUser().twitter)) {
                twitter_icon.setVisibility(View.VISIBLE);
            } else {
                twitter_icon.setVisibility(View.GONE);
            }

            instagram_icon = (ImageView) findViewById(R.id.instagram_icon);
            if (!TextUtils.isNullOrEmpty(getUser().instagram)) {
                instagram_icon.setVisibility(View.VISIBLE);
            } else {
                instagram_icon.setVisibility(View.GONE);
            }

            tv_user_bio = (TextView) findViewById(R.id.tv_user_bio);
            if (!TextUtils.isNullOrEmpty(getUser().bio)) {
                tv_user_bio.setText(getUser().bio);
            } else {
                tv_user_bio.setText(" ");
            }
        }

        email_icon.setOnClickListener(this);
        phoneCall_icon.setOnClickListener(this);
        fb_icon.setOnClickListener(this);
        instagram_icon.setOnClickListener(this);
        webAddress_icon.setOnClickListener(this);
        twitter_icon.setOnClickListener(this);
        //Call api MyPortfolio for all section.
        attemptCreated();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;
            case R.id.activity_Relay:
                if (TextUtils.isNullOrEmpty(activityDataArrayList)) {
                    showToast(getString(R.string.no_user_activity));
                } else {
                    intent = new Intent(MyPortfolioActivity.this, PortfolioProjectbyActivityActivity.class);
                    intent.putExtra(Constants.DATA, activityDataArrayList);
                    startActivity(intent);
                }
                break;
            case R.id.iv_edit:
                JaribhaPrefrence.setPref(MyPortfolioActivity.this, Constants.PORT_EDIT, true);
                intent = new Intent(MyPortfolioActivity.this, HomeScreenActivity.class);
                intent.putExtra("pos", 555);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.created_Relay:
                if (TextUtils.isNullOrEmpty(creatorsDataArrayList)) {
                    showToast(getString(R.string.no_project_created));
                } else {
                    intent = new Intent(MyPortfolioActivity.this, MyPortfolioCreatedProjectActivity.class);
                    intent.putExtra(Constants.DATA, creatorsDataArrayList);
                    startActivityForResult(intent, resultCode);
                }
                break;
            case R.id.supported_Relay:
                if (TextUtils.isNullOrEmpty(supportersDataArrayList)) {
                    showToast(getString(R.string.no_projects_supported));
                } else {
                    intent = new Intent(MyPortfolioActivity.this, PortfolioProjectbySupportedActivity.class);
                    intent.putExtra(Constants.DATA, supportersDataArrayList);
                    startActivity(intent);
                }
                break;
            case R.id.sponsered_Relay:
                if (TextUtils.isNullOrEmpty(sponsorsDataArrayList)) {
                    showToast(getString(R.string.no_project_sponsored));
                } else {
                    intent = new Intent(MyPortfolioActivity.this, PortfolioProjectbySponseredActivity.class);
                    intent.putExtra(Constants.DATA, sponsorsDataArrayList);
                    startActivity(intent);
                }
                break;
            case R.id.favourite_Relay:
                //((HomeScreenActivity)getActivity()).displayView(new MyFavoritesFragment(), "Favorites");
                if (TextUtils.isNullOrEmpty(favouritesDataArrayList)) {
                    showToast(getString(R.string.no_favorite_projects));
                } else {
                    intent = new Intent(MyPortfolioActivity.this, HomeScreenActivity.class);
                    intent.putExtra("pos", 9);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    //finish();
                }
                break;
            case R.id.email_icon:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", getUser().email, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send via"));
                break;
            case R.id.fb_icon:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("web_url", getUser().facebook);
                intent.putExtra("title", "Facebook");
                startActivity(intent);
                break;
            case R.id.phoneCall_icon:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    phoneCall();
                } else {
                    requestPhoneCall();
                }
                break;
            case R.id.webAddress_icon:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("web_url", getUser().website);
                intent.putExtra("title", "Website");
                startActivity(intent);
                break;
            case R.id.instagram_icon:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("web_url", getUser().instagram);
                intent.putExtra("title", "Instagram");
                startActivity(intent);
                break;
            case R.id.twitter_icon:
                intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("web_url", getUser().twitter);
                intent.putExtra("title", "Twitter");
                startActivity(intent);
                break;
            default:
                break;

        }
    }

    private MyPortfolioTask mMyPortfolioTask = null;

    private ArrayList<ActivityData> activityDataArrayList = new ArrayList<>();
    private ArrayList<GetProjects> creatorsDataArrayList = new ArrayList<>();
    private ArrayList<GetProjects> supportersDataArrayList = new ArrayList<>();
    private ArrayList<GetProjects> sponsorsDataArrayList = new ArrayList<>();
    private ArrayList<GetProjects> favouritesDataArrayList = new ArrayList<>();

    public class MyPortfolioTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        MyPortfolioTask(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.MY_PORTFOLIO, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mMyPortfolioTask = null;
            hideLoading();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.optJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        JSONArray activityjsonArray = jsonObject.getJSONObject("data").getJSONArray("activity");
                        JSONArray creatorsjsonArray = jsonObject.getJSONObject("data").getJSONArray("creators");
                        JSONArray supportersjsonArray = jsonObject.getJSONObject("data").getJSONArray("supporters");
                        JSONArray sponsorsjsonArray = jsonObject.getJSONObject("data").getJSONArray("sponsors");
                        JSONArray favouritesjsonArray = jsonObject.getJSONObject("data").getJSONArray("favourites");

                        if (activityjsonArray != null && activityjsonArray.length() != 0) {
                            for (int i = 0; i < activityjsonArray.length(); i++) {
                                ActivityData activityData = new Gson().fromJson(activityjsonArray.getJSONObject(i).getJSONObject("Project").toString(), ActivityData.class);
                                activityDataArrayList.add(activityData);
                            }
                        }
                        if (creatorsjsonArray != null && creatorsjsonArray.length() != 0) {
                            for (int i = 0; i < creatorsjsonArray.length(); i++) {
                                GetProjects creatorsData = new Gson().fromJson(creatorsjsonArray.getJSONObject(i).getJSONObject("Project").toString(), GetProjects.class);
                                if (!creatorsData.status.equals("incomplete"))
                                    creatorsData.status = Utils.setProjectStatus(creatorsData);
                                creatorsData.days_left = Utils.getDaysLeft(creatorsData);
                                creatorsDataArrayList.add(creatorsData);
                            }
                        }
                        if (supportersjsonArray != null && supportersjsonArray.length() != 0) {
                            for (int i = 0; i < supportersjsonArray.length(); i++) {
                                GetProjects supportersData = new Gson().fromJson(supportersjsonArray.getJSONObject(i).getJSONObject("Project").toString(), GetProjects.class);
                                supportersData.status = Utils.setProjectStatus(supportersData);
                                supportersData.days_left = Utils.getDaysLeft(supportersData);
                                supportersDataArrayList.add(supportersData);
                            }
                        }
                        if (sponsorsjsonArray != null && sponsorsjsonArray.length() != 0) {
                            for (int i = 0; i < sponsorsjsonArray.length(); i++) {
                                GetProjects sponsorsData = new Gson().fromJson(sponsorsjsonArray.getJSONObject(i).getJSONObject("Project").toString(), GetProjects.class);
                                sponsorsData.status = Utils.setProjectStatus(sponsorsData);
                                sponsorsData.days_left = Utils.getDaysLeft(sponsorsData);
                                sponsorsDataArrayList.add(sponsorsData);
                            }
                        }
                        if (favouritesjsonArray != null && favouritesjsonArray.length() != 0) {
                            for (int i = 0; i < favouritesjsonArray.length(); i++) {
                                GetProjects favouritesData = new Gson().fromJson(favouritesjsonArray.getJSONObject(i).getJSONObject("Project").toString(), GetProjects.class);
                                favouritesData.status = Utils.setProjectStatus(favouritesData);
                                favouritesData.days_left = Utils.getDaysLeft(favouritesData);
                                favouritesDataArrayList.add(favouritesData);
                            }
                        }

                        created_count.setText(String.valueOf(creatorsDataArrayList.size()));
                        supportedCount.setText(String.valueOf(supportersDataArrayList.size()));
                        sponsorsCount.setText(String.valueOf(sponsorsDataArrayList.size()));
                        favouritesCount.setText(String.valueOf(favouritesDataArrayList.size()));

                    } else {
                        /*if (msg.equals("Data Not Found")) {
                            showDataNotFoundDialog();
                        } else */
                        if (msg.equals("User Not Found")) {
                            showSessionDialog();
                        } /*else {
                            showServerDialogDialog();
                        }*/
                    }
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                    showServerDialogDialog();
                }
            } else {
                showServerDialogDialog();
            }
        }

        @Override
        protected void onCancelled() {
            mMyPortfolioTask = null;
            hideLoading();
        }
    }


    private void attemptCreated() {
        if (isInternetConnected()) {
            try {
                JSONObject PortfolioJsonObject = new JSONObject();
                PortfolioJsonObject.put("apikey", Urls.API_KEY);
                PortfolioJsonObject.put("user_id", getUser().id);
                PortfolioJsonObject.put("user_token", getUser().user_token);

                mMyPortfolioTask = new MyPortfolioTask(PortfolioJsonObject);
                mMyPortfolioTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        } else {
            showNetworkDialog();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                //String result = data.getStringExtra("result");
                ArrayList<Integer> result = data.getIntegerArrayListExtra("result");

                for (int i = 0; i < result.size(); i++) {
                    try {
                        //int listPosition = Integer.parseInt(result);
                        int value = result.get(i);
                        creatorsDataArrayList.remove(value);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                created_count.setText(String.valueOf(creatorsDataArrayList.size()));
            }
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
        callIntent.setData(Uri.parse("tel:" + getUser().phone));
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
