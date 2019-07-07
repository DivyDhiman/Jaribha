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


public class NotificationPushFragment extends BaseFragment implements View.OnClickListener {

    TextView sameAsPush;

    Button btn_user_update;

    private CheckBox email_push_Radio_btn1;

    private CheckBox email_push_Radio_btn2;

    private CheckBox email_push_Radio_btn3;

    private CheckBox supporter_push_Radio_btn1;

    private CheckBox supporter_push_Radio_btn2;

    private CheckBox sponser_push_Radio_btn1;

    private CheckBox General_push_Radio_btn1;

    private CheckBox General_push_Radio_btn2;

    private String creator_new_supporter = "0";

    private String creator_new_sponser = "0";

    private String creator_new_comments = "0";

    private String supporter_project_deadline = "0";

    private String supporter_project_updated = "0";

    private String sponsor_platinum_space = "0";

    private String newsletter_proeject_sucessfull = "0";

    private String newsletter_message_chat = "0";

    private ProgressDialog progressDialog;

    Activity activity;

    UserData userData;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public static NotificationPushFragment newInstance(UserData userData) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.DATA, userData);
        NotificationPushFragment fragment = new NotificationPushFragment();
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
        View view = inflater.inflate(R.layout.fragment_nitification_push, container, false);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        sameAsPush = (TextView) view.findViewById(R.id.sameAsPush);
        sameAsPush.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        sameAsPush.setOnClickListener(this);

        email_push_Radio_btn1 = (CheckBox) view.findViewById(R.id.email_push_Radio_btn1);

        email_push_Radio_btn2 = (CheckBox) view.findViewById(R.id.email_push_Radio_btn2);

        email_push_Radio_btn3 = (CheckBox) view.findViewById(R.id.email_push_Radio_btn3);

        supporter_push_Radio_btn1 = (CheckBox) view.findViewById(R.id.supporter_push_Radio_btn1);

        supporter_push_Radio_btn2 = (CheckBox) view.findViewById(R.id.supporter_push_Radio_btn2);

        sponser_push_Radio_btn1 = (CheckBox) view.findViewById(R.id.sponser_push_Radio_btn1);

        General_push_Radio_btn1 = (CheckBox) view.findViewById(R.id.General_push_Radio_btn1);

        General_push_Radio_btn2 = (CheckBox) view.findViewById(R.id.General_push_Radio_btn2);

        ///radioGroupEmail = (RadioGroup) view.findViewById(R.id.radioGroupEmail);

        if (isAdded()) {
            email_push_Radio_btn1.setText(getString(R.string.when_i_get_a_new_supporter_every_time));
            email_push_Radio_btn2.setText(getString(R.string.when_i_get_a_new_sponser_every_time));
            email_push_Radio_btn3.setText(getString(R.string.when_my_project_receives_a_comment));

            supporter_push_Radio_btn1.setText(getString(R.string.when_one_of_the_projects_i_supported_reaches));
            supporter_push_Radio_btn2.setText(getString(R.string.when_one_of_the_projects_i_supported));

            sponser_push_Radio_btn1.setText(getString(R.string.projects_with_open_platinum_sponser_spaces));

            General_push_Radio_btn1.setText(getString(R.string.newletter_when_i_received_a_private_message));
            General_push_Radio_btn2.setText(getString(R.string.update_on_new_projects_and_successful_projects));
        }

        sponser_push_Radio_btn1.setEnabled(false);
        supporter_push_Radio_btn1.setEnabled(false);
        supporter_push_Radio_btn2.setEnabled(false);

        email_push_Radio_btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userData.creator_new_supporter_push = "1";
                    creator_new_supporter = "1";
                } else {
                    userData.creator_new_supporter_push = "0";
                    creator_new_supporter = "0";
                }
            }
        });

        email_push_Radio_btn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userData.creator_new_sponser_push = "1";
                    creator_new_sponser = "1";
                } else {
                    userData.creator_new_sponser_push = "0";
                    creator_new_sponser = "0";
                }
            }
        });

        email_push_Radio_btn3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userData.creator_new_comments_push = "1";
                    creator_new_comments = "1";
                } else {
                    userData.creator_new_comments_push = "0";
                    creator_new_comments = "0";
                }
            }
        });

        supporter_push_Radio_btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userData.supporter_project_deadline_push = "1";
                    supporter_project_deadline = "1";
                } else {
                    userData.supporter_project_deadline_push = "0";
                    supporter_project_deadline = "0";
                }
            }
        });

        supporter_push_Radio_btn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userData.supporter_project_updated_push = "1";
                    supporter_project_updated = "1";
                } else {
                    userData.supporter_project_updated_push = "0";
                    supporter_project_updated = "0";
                }
            }
        });

        sponser_push_Radio_btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userData.sponsor_platinum_space_push = "1";
                    sponsor_platinum_space = "1";
                } else {
                    userData.sponsor_platinum_space_push = "0";
                    sponsor_platinum_space = "0";
                }
            }
        });

        General_push_Radio_btn1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userData.newsletter_message_chat_push = "1";
                    newsletter_proeject_sucessfull = "1";
                } else {
                    userData.newsletter_message_chat_push = "0";
                    newsletter_proeject_sucessfull = "0";
                }
            }
        });

        General_push_Radio_btn2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userData.newsletter_proeject_sucessfull_push = "1";
                    newsletter_message_chat = "1";
                } else {
                    userData.newsletter_proeject_sucessfull_push = "0";
                    newsletter_message_chat = "0";
                }
            }
        });

        btn_user_update = (Button) view.findViewById(R.id.btn_push_update);
        btn_user_update.setOnClickListener(this);

        if (isAdded())
            btn_user_update.setText(activity.getResources().getString(R.string.update));

        displayUserSettings();

        //        if (!TextUtils.isNullOrEmpty(getUser().sponsor_type)) {
//            sponser_push_Radio_btn1.setEnabled(true);
//        }

        if (!TextUtils.isNullOrEmpty(userData.sponsor_type)) {
            sponser_push_Radio_btn1.setEnabled(true);
        }
        else {
            sponser_push_Radio_btn1.setChecked(false);
            sponser_push_Radio_btn1.setEnabled(false);
        }

        if (!TextUtils.isNullOrEmpty(userData.supporter_type)) {
            supporter_push_Radio_btn1.setEnabled(true);
            supporter_push_Radio_btn2.setEnabled(true);
        }
        else {
            supporter_push_Radio_btn1.setChecked(false);
            supporter_push_Radio_btn1.setEnabled(false);

            supporter_push_Radio_btn2.setChecked(false);
            supporter_push_Radio_btn2.setEnabled(false);
        }


//        if (!TextUtils.isNullOrEmpty(getUser().sponsor_type) && !TextUtils.isNullOrEmpty(getUser().supporter_type)) {
//            sponser_push_Radio_btn1.setEnabled(true);
//            supporter_push_Radio_btn1.setEnabled(true);
//            supporter_push_Radio_btn2.setEnabled(true);
//        }
//        else {
//            sponser_push_Radio_btn1.setChecked(false);
//            sponser_push_Radio_btn1.setEnabled(false);
//
//            supporter_push_Radio_btn1.setChecked(false);
//            supporter_push_Radio_btn1.setEnabled(false);
//
//            supporter_push_Radio_btn2.setChecked(false);
//            supporter_push_Radio_btn2.setEnabled(false);
//        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_push_update:
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

    private void displayUserSettings() {
        if (!TextUtils.isNullOrEmpty(userData.creator_new_supporter_push)) {
            email_push_Radio_btn1.setChecked(true);
        }

        if (!TextUtils.isNullOrEmpty(userData.creator_new_sponser_push)) {
            email_push_Radio_btn2.setChecked(true);
        }

        if (!TextUtils.isNullOrEmpty(userData.creator_new_comments_push)) {
            email_push_Radio_btn3.setChecked(true);
        }

        if (!TextUtils.isNullOrEmpty(userData.supporter_project_deadline_push)) {
            supporter_push_Radio_btn1.setChecked(true);
        }

        if (!TextUtils.isNullOrEmpty(userData.supporter_project_updated_push)) {
            supporter_push_Radio_btn2.setChecked(true);
        }

        if (!TextUtils.isNullOrEmpty(userData.sponsor_platinum_space_push)) {
            sponser_push_Radio_btn1.setChecked(true);
        }

        if (!TextUtils.isNullOrEmpty(userData.newsletter_message_chat_push)) {
            General_push_Radio_btn1.setChecked(true);
        }

        if (!TextUtils.isNullOrEmpty(userData.newsletter_proeject_sucessfull_push)) {
            General_push_Radio_btn2.setChecked(true);
        }
    }

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
            return new JSONParser().getJsonObjectFromUrl1(Urls.UPDATE_PUSH_SETTINGS, nameValuePairs);
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

//                        Intent intent = new Intent(Constants.Action_Settings);
//                        intent.putExtra(Constants.DATA, userData);
//                        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);

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
            jsonObject.put("creator_new_supporter_push", creator_new_supporter);
            jsonObject.put("creator_new_sponser_push", creator_new_sponser);
            jsonObject.put("creator_new_comments_push", creator_new_comments);
            jsonObject.put("supporter_project_deadline_push", supporter_project_deadline);
            jsonObject.put("supporter_project_updated_push", supporter_project_updated);
            jsonObject.put("sponsor_platinum_space_push", sponsor_platinum_space);
            jsonObject.put("newsletter_proeject_sucessfull_push", newsletter_proeject_sucessfull);
            jsonObject.put("newsletter_message_chat_push", newsletter_message_chat);

            mUpdateNotificationEmailTask = new UpdateNotificationEmailTask(jsonObject);
            mUpdateNotificationEmailTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
