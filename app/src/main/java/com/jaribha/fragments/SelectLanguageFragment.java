package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.models.UserData;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class SelectLanguageFragment extends BaseFragment implements View.OnClickListener {

    private Button btn_update;

    private RadioButton eng, arabic;

    private RadioGroup radioGroup;

    private String setLaguage = "eng";

    private ProgressDialog progressDialog;

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_select_language, container, false);
        eng = (RadioButton) view.findViewById(R.id.eng);
        eng.setChecked(false);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        arabic = (RadioButton) view.findViewById(R.id.arb);
        arabic.setChecked(false);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        if (isArabic()) {
            arabic.setChecked(true);
        } else {
            eng.setChecked(true);
        }
        setLaguage = JaribhaPrefrence.getPref(activity,Constants.PROJECT_LANGUAGE,"en");

        if(setLaguage.equals("en")){
            eng.setChecked(true);
        }else if(setLaguage.equals("ar")){
            arabic.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.eng) {
                    setLaguage = "en";
                } else if (checkedId == R.id.arb) {
                    setLaguage = "ar";
                }
            }
        });

        btn_update = (Button) view.findViewById(R.id.btn_user_update);
        btn_update.setOnClickListener(this);

        loadSettings();
        updateLocal();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_user_update:
                Intent intent = new Intent("com.settings");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                Intent intent1 = new Intent("com.menu");
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent1);
                JaribhaPrefrence.setPref(activity, Constants.PROJECT_LANGUAGE, setLaguage);
                JaribhaPrefrence.setPref(activity, Constants.SETTING_LANGUAGE, true);
                Utils.changeLangauge(JaribhaPrefrence.getPref(activity,Constants.PROJECT_LANGUAGE,"en"), activity);
                updateLocal();
                Utils.setDefaultValues(activity);
                break;

            default:
                break;

        }
    }

    private void updateLocal() {
        btn_update.setText(getString(R.string.update));
    }

    private UpdateLanguageTask mUpdateLanguageTask = null;

    /**
     * Represents an asynchronous UpdateLanguageTask used to update application laguage.
     */

    public class UpdateLanguageTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        UpdateLanguageTask(JSONObject params) {
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
            return new JSONParser().getJsonObjectFromUrl1(Urls.UPDATE_LANGUAGE, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mUpdateLanguageTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {

                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        if (setLaguage.equals("eng")) {
                            // eng.setChecked(true);
                            JaribhaPrefrence.setPref(activity, Constants.PROJECT_LANGUAGE, "en");
                        } else if (setLaguage.equals("ara")) {
                            //arabic.setChecked(true);
                            JaribhaPrefrence.setPref(activity, Constants.PROJECT_LANGUAGE, "ar");
                        }
                        Utils.changeLangauge(JaribhaPrefrence.getPref(activity, Constants.PROJECT_LANGUAGE, "en"), activity);
                        if (isAdded())
                            btn_update.setText(getString(R.string.update));
                        String message = jsonObject.getString("msg");
                        if (isAdded())
                            showCustomeDialog(R.drawable.right_icon, "Success", message, getString(R.string.dgts__okay), R.drawable.btn_bg_green);
//
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
            mUpdateLanguageTask = null;
            hideLoading();
        }
    }

    private void updateLanguage() {
        try {
            JSONObject UpdateLangJsonObject = new JSONObject();
            UpdateLangJsonObject.put("apikey", Urls.API_KEY);
            UpdateLangJsonObject.put("user_id", getUser().id);
            UpdateLangJsonObject.put("user_token", getUser().user_token);
            UpdateLangJsonObject.put("language", setLaguage);

            mUpdateLanguageTask = new UpdateLanguageTask(UpdateLangJsonObject);
            mUpdateLanguageTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSettings() {
        if (isInternetConnected()) {
            try {
                JSONObject PortfolioJsonObject = new JSONObject();
                PortfolioJsonObject.put("apikey", Urls.API_KEY);
                PortfolioJsonObject.put("user_id", getUser().id);
                PortfolioJsonObject.put("user_token", getUser().user_token);

                GetUser mMyPortfolioTask = new GetUser(PortfolioJsonObject);
                mMyPortfolioTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            showNetworkDialog();
        }
    }

    UserData userData;

    public class GetUser extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetUser(JSONObject params) {
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
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_USER_INFO, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {

            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {

                        userData = new Gson().fromJson(jsonObject.optJSONObject("data").toString(), UserData.class);
                       /* if (!TextUtils.isNullOrEmpty(userData.language)) {
                            if (userData.language.equals("eng")) {
                                eng.setChecked(true);
                                //JaribhaPrefrence.setPref(this, Constants.PROJECT_LANGUAGE,"ar");
                            }
                            if (userData.language.equals("ara")) {
                                arabic.setChecked(true);
                                //JaribhaPrefrence.setPref(this, Constants.PROJECT_LANGUAGE,"ar");
                            }
                        }*/

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
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }


}
