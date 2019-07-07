package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.models.UserData;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;

import org.json.JSONException;
import org.json.JSONObject;


public class NotificationEmailFragment extends BaseFragment implements View.OnClickListener {

    TextView sameAsPush;

    Button btn_user_update;

    private CheckBox emailRadio_btn1;

    private CheckBox emailRadio_btn2;

    private CheckBox emailRadio_btn3;

    private CheckBox supporterRadio_btn1;

    private CheckBox supporterRadio_btn2;

    private CheckBox sponserRadio_btn1;

    private CheckBox GeneralRadio_btn1;

    private CheckBox GeneralRadio_btn2;

    private String creator_new_supporter = "0";

    private String creator_new_sponser = "0";

    private String creator_new_comments = "0";

    private String supporter_project_deadline = "0";

    private String supporter_project_updated = "0";

    private String sponsor_platinum_space = "0";

    private String newsletter_proeject_sucessfull = "0";

    private String newsletter_message_chat = "0";

    private ProgressDialog progressDialog;

    private Activity activity;

    UserData userData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public static NotificationEmailFragment newInstance(UserData userData) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.DATA, userData);
        NotificationEmailFragment fragment = new NotificationEmailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userData = (UserData) getArguments().getSerializable(Constants.DATA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nitification_email, container, false);

//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(Constants.Action_Settings));

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        sameAsPush = (TextView) view.findViewById(R.id.sameAsPush);
        sameAsPush.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        sameAsPush.setOnClickListener(this);

        sameAsPush.setText(activity.getResources().getString(R.string.same_as_push));

        emailRadio_btn1 = (CheckBox) view.findViewById(R.id.emailRadio_btn1);

        emailRadio_btn2 = (CheckBox) view.findViewById(R.id.emailRadio_btn2);

        emailRadio_btn3 = (CheckBox) view.findViewById(R.id.emailRadio_btn3);

        supporterRadio_btn1 = (CheckBox) view.findViewById(R.id.supporterRadio_btn1);
        supporterRadio_btn1.setEnabled(false);

        supporterRadio_btn2 = (CheckBox) view.findViewById(R.id.supporterRadio_btn2);
        supporterRadio_btn2.setEnabled(false);

        sponserRadio_btn1 = (CheckBox) view.findViewById(R.id.sponserRadio_btn1);
        sponserRadio_btn1.setEnabled(false);

        GeneralRadio_btn1 = (CheckBox) view.findViewById(R.id.GeneralRadio_btn1);
        GeneralRadio_btn2 = (CheckBox) view.findViewById(R.id.GeneralRadio_btn2);

        if (isAdded()) {
            emailRadio_btn1.setText(getString(R.string.when_i_get_a_new_supporter_every_time));
            emailRadio_btn2.setText(getString(R.string.when_i_get_a_new_sponser_every_time));
            emailRadio_btn3.setText(getString(R.string.when_my_project_receives_a_comment));

            supporterRadio_btn1.setText(getString(R.string.when_one_of_the_projects_i_supported_reaches));
            supporterRadio_btn2.setText(getString(R.string.when_one_of_the_projects_i_supported));

            sponserRadio_btn1.setText(getString(R.string.projects_with_open_platinum_sponser_spaces));

            GeneralRadio_btn1.setText(getString(R.string.newletter_when_i_received_a_private_message));
            GeneralRadio_btn2.setText(getString(R.string.update_on_new_projects_and_successful_projects));
        }



        emailRadio_btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    creator_new_supporter = "1";
                else
                    creator_new_supporter = "0";
            }
        });

        emailRadio_btn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    creator_new_sponser = "1";
                else
                    creator_new_sponser = "0";
            }
        });

        emailRadio_btn3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    creator_new_comments = "1";
                else
                    creator_new_comments = "0";
            }
        });

        supporterRadio_btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    supporter_project_deadline = "1";
                else
                    supporter_project_deadline = "0";
            }
        });

        supporterRadio_btn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    supporter_project_updated = "1";
                else
                    supporter_project_updated = "0";
            }
        });

        sponserRadio_btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    sponsor_platinum_space = "1";
                else
                    sponsor_platinum_space = "0";
            }
        });

        GeneralRadio_btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    newsletter_proeject_sucessfull = "1";
                else
                    newsletter_proeject_sucessfull = "0";
            }
        });

        GeneralRadio_btn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    newsletter_message_chat = "1";
                else
                    newsletter_message_chat = "0";
            }
        });

        btn_user_update = (Button) view.findViewById(R.id.btn_user_update);
        btn_user_update.setOnClickListener(this);
        if (isAdded())
            btn_user_update.setText(activity.getResources().getString(R.string.update));


        displayUserSettings();

        if (!TextUtils.isNullOrEmpty(userData.sponsor_type)) {
            sponserRadio_btn1.setEnabled(true);
        }
        else {
            sponserRadio_btn1.setChecked(false);
            sponserRadio_btn1.setEnabled(false);
        }

        if (!TextUtils.isNullOrEmpty(userData.supporter_type)) {
            supporterRadio_btn1.setEnabled(true);
            supporterRadio_btn2.setEnabled(true);
        }
        else {
            supporterRadio_btn1.setChecked(false);
            supporterRadio_btn1.setEnabled(false);

            supporterRadio_btn2.setChecked(false);
            supporterRadio_btn2.setEnabled(false);
        }

//        if (!TextUtils.isNullOrEmpty(getUser().sponsor_type) && !TextUtils.isNullOrEmpty(getUser().supporter_type)) {
//            sponserRadio_btn1.setEnabled(true);
//            supporterRadio_btn1.setEnabled(true);
//            supporterRadio_btn2.setEnabled(true);
//        }
//        else {
//            sponserRadio_btn1.setChecked(false);
//            sponserRadio_btn1.setEnabled(false);
//
//            supporterRadio_btn1.setChecked(false);
//            supporterRadio_btn1.setEnabled(false);
//
//            supporterRadio_btn2.setChecked(false);
//            supporterRadio_btn2.setEnabled(false);
//        }

        return view;
    }

    private void displayUserSettings() {
        if (!TextUtils.isNullOrEmpty(userData.creator_new_sporter)) {
            emailRadio_btn1.setChecked(true);
        }
        if (!TextUtils.isNullOrEmpty(userData.creator_new_sponser)) {
            emailRadio_btn2.setChecked(true);
        }
        if (!TextUtils.isNullOrEmpty(userData.creator_new_comments)) {
            emailRadio_btn3.setChecked(true);
        }
        if (!TextUtils.isNullOrEmpty(userData.supporter_project_deadline)) {
            supporterRadio_btn1.setChecked(true);
        }
        if (!TextUtils.isNullOrEmpty(userData.supporter_project_updated)) {
            supporterRadio_btn2.setChecked(true);
        }
        if (!TextUtils.isNullOrEmpty(userData.sponsor_platinum_space)) {
            sponserRadio_btn1.setChecked(true);
        }
        if (!TextUtils.isNullOrEmpty(userData.newsletter_message_chat)) {
            GeneralRadio_btn1.setChecked(true);
        }
        if (!TextUtils.isNullOrEmpty(userData.newsletter_proeject_sucessfull)) {
            GeneralRadio_btn2.setChecked(true);
        }
    }

    private void onSameAsPushClick() {
        if (!TextUtils.isNullOrEmpty(userData.creator_new_supporter_push) && userData.creator_new_supporter_push.equalsIgnoreCase("1")) {
            emailRadio_btn1.setChecked(true);
        } else {
            emailRadio_btn1.setChecked(false);
        }

        if (!TextUtils.isNullOrEmpty(userData.creator_new_sponser_push) && userData.creator_new_sponser_push.equalsIgnoreCase("1")) {
            emailRadio_btn2.setChecked(true);
        } else {
            emailRadio_btn2.setChecked(false);
        }

        if (!TextUtils.isNullOrEmpty(userData.creator_new_comments_push) && userData.creator_new_comments_push.equalsIgnoreCase("1")) {
            emailRadio_btn3.setChecked(true);
        } else {
            emailRadio_btn3.setChecked(false);
        }

        if (!TextUtils.isNullOrEmpty(userData.supporter_project_deadline_push) && userData.supporter_project_deadline_push.equalsIgnoreCase("1")) {
            supporterRadio_btn1.setChecked(true);
        } else {
            supporterRadio_btn1.setChecked(false);
        }

        if (!TextUtils.isNullOrEmpty(userData.supporter_project_updated_push) && userData.supporter_project_updated_push.equalsIgnoreCase("1")) {
            supporterRadio_btn2.setChecked(true);
        } else {
            supporterRadio_btn2.setChecked(false);
        }

        if (!TextUtils.isNullOrEmpty(userData.sponsor_platinum_space_push) && userData.sponsor_platinum_space_push.equalsIgnoreCase("1")) {
            sponserRadio_btn1.setChecked(true);
        } else {
            sponserRadio_btn1.setChecked(false);
        }

        if (!TextUtils.isNullOrEmpty(userData.newsletter_message_chat_push) && userData.newsletter_message_chat_push.equalsIgnoreCase("1")) {
            GeneralRadio_btn1.setChecked(true);
        } else {
            GeneralRadio_btn1.setChecked(false);
        }

        if (!TextUtils.isNullOrEmpty(userData.newsletter_proeject_sucessfull_push) && userData.newsletter_proeject_sucessfull_push.equalsIgnoreCase("1")) {
            GeneralRadio_btn2.setChecked(true);
        } else {
            GeneralRadio_btn2.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sameAsPush:
                onSameAsPushClick();
                break;

            case R.id.btn_user_update:
                if (isInternetConnected()) {
                    updateNotifications();
                } else {
                    showNetworkDialog();
                }

                break;

            default:
                break;
        }
    }

//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            if (intent.getAction().equals(Constants.Action_Settings)) {
//                userData = (UserData) intent.getSerializableExtra(Constants.DATA);
//            }
//        }
//    };
//
//    @Override
//    public void onDestroy() {
//        // Unregister since the activity is about to be closed.
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
//        super.onDestroy();
//    }

    private UpdateNotificationEmailTask mUpdateNotificationEmailTask = null;

    /**
     * Represents an asynchronous UpdateNotificationEmailTask used to update notifications.
     */

    public class UpdateNotificationEmailTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        UpdateNotificationEmailTask(JSONObject params) {
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
            return new JSONParser().getJsonObjectFromUrl1(Urls.UPDATE_EMAIL_SETTINGS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mUpdateNotificationEmailTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        String message = jsonObject.getString("msg");
                        if (isAdded())
                            showCustomeDialog(R.drawable.right_icon, getString(R.string.success), message, getString(R.string.dgts__okay), R.drawable.btn_bg_green);
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
                    e.printStackTrace();
                    showServerErrorDialog();
                }
            } else {
                showServerErrorDialog();
            }
        }

        @Override
        protected void onCancelled() {
            mUpdateNotificationEmailTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    private void updateNotifications() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", getUser().id);
            jsonObject.put("user_token", getUser().user_token);
            jsonObject.put("creator_new_supporter", creator_new_supporter);
            jsonObject.put("creator_new_sponser", creator_new_sponser);
            jsonObject.put("creator_new_comments", creator_new_comments);
            jsonObject.put("supporter_project_deadline", supporter_project_deadline);
            jsonObject.put("supporter_project_updated", supporter_project_updated);
            jsonObject.put("sponsor_platinum_space", sponsor_platinum_space);
            jsonObject.put("newsletter_proeject_sucessfull", newsletter_proeject_sucessfull);
            jsonObject.put("newsletter_message_chat", newsletter_message_chat);

            mUpdateNotificationEmailTask = new UpdateNotificationEmailTask(jsonObject);
            mUpdateNotificationEmailTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void loadSettings() {
//        if (isInternetConnected()) {
//            try {
//                JSONObject PortfolioJsonObject = new JSONObject();
//                PortfolioJsonObject.put("apikey", Urls.API_KEY);
//                PortfolioJsonObject.put("user_id", getUser().id);
//                PortfolioJsonObject.put("user_token", getUser().user_token);
//
//                GetUser mMyPortfolioTask = new GetUser(PortfolioJsonObject);
//                mMyPortfolioTask.execute();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            showNetworkDialog();
//        }
//    }


//    public class GetUser extends AsyncTask<Void, Void, JSONObject> {
//
//        JSONObject nameValuePairs;
//
//        GetUser(JSONObject params) {
//            this.nameValuePairs = params;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            //showLoading();
//            if (progressDialog != null && !progressDialog.isShowing())
//                progressDialog.show();
//        }
//
//        @Override
//        protected JSONObject doInBackground(Void... params) {
//            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_USER_INFO, nameValuePairs);
//        }
//
//        @Override
//        protected void onPostExecute(final JSONObject responce) {
//
//            if (progressDialog != null && progressDialog.isShowing())
//                progressDialog.dismiss();
//            if (responce != null) {
//                try {
//                    JSONObject jsonObject = responce.getJSONObject("result");
//                    Boolean status = jsonObject.getBoolean("status");
//                    String msg = jsonObject.optString("msg");
//                    if (status) {
//
//                        userData = new Gson().fromJson(jsonObject.optJSONObject("data").toString(), UserData.class);
//                        if (!TextUtils.isNullOrEmpty(userData.creator_new_sporter)) {
//                            emailRadio_btn1.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.creator_new_sponser)) {
//                            emailRadio_btn2.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.creator_new_comments)) {
//                            emailRadio_btn3.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.supporter_project_deadline)) {
//                            supporterRadio_btn1.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.supporter_project_updated)) {
//                            supporterRadio_btn2.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.sponsor_platinum_space)) {
//                            sponserRadio_btn1.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.newsletter_message_chat)) {
//                            GeneralRadio_btn1.setChecked(true);
//                        }
//                        if (!TextUtils.isNullOrEmpty(userData.newsletter_proeject_sucessfull)) {
//                            GeneralRadio_btn2.setChecked(true);
//                        }
//                    } else {
//                        switch (msg) {
//                            case "Data Not Found":
//                                showDataNotFoundDialog();
//                                break;
//                            case "User Not Found":
//                                showSessionDialog();
//                                break;
//                            default:
//                                showServerErrorDialog();
//                                break;
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    showServerErrorDialog();
//                }
//            } else {
//                showServerErrorDialog();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            //hideLoading();
//            if (progressDialog != null && progressDialog.isShowing())
//                progressDialog.dismiss();
//        }
//    }
}
