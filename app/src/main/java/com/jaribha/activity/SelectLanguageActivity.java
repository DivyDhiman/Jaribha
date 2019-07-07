package com.jaribha.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.models.UserData;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class SelectLanguageActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ImageView iv_close;

    private TextView tv_title;

    private Button btn_update;

    private RadioButton eng, arabic;

    private RadioGroup radioGroup;

    private String setLaguage = "eng";

    private BezelImageView img_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        img_user = (BezelImageView) findViewById(R.id.img_user_image);
        displayUserImage(img_user);
        img_user.setOnClickListener(this);
        eng = (RadioButton) findViewById(R.id.eng);
        eng.setChecked(false);

        arabic = (RadioButton) findViewById(R.id.arb);
        arabic.setChecked(false);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

//        if (!TextUtils.isNullOrEmpty(getUser().language)) {
//            if (getUser().language.equals("eng")) {
//                eng.setChecked(true);
//            }
//            if (getUser().language.equals("ara")) {
//                arabic.setChecked(true);
//            }
//        }

        if (isArabic()) {
            arabic.setChecked(true);
        } else {
            eng.setChecked(true);
        }

        setLaguage = JaribhaPrefrence.getPref(this,Constants.PROJECT_LANGUAGE,"en");

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


        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);

        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);

        btn_update = (Button) findViewById(R.id.btn_user_update);
        btn_update.setOnClickListener(this);

        updateLocal();
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
            case R.id.btn_user_update:
                JaribhaPrefrence.setPref(this, Constants.PROJECT_LANGUAGE, setLaguage);
                JaribhaPrefrence.setPref(this, Constants.SETTING_LANGUAGE, true);

                Utils.changeLangauge(JaribhaPrefrence.getPref(this,Constants.PROJECT_LANGUAGE,"en"), this);
                updateLocal();
                Utils.setDefaultValues(getActivity());
                break;

            default:
                break;

        }
    }

    private void updateLocal() {
        tv_title.setText(getString(R.string.select_language));
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
            showLoading();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.UPDATE_LANGUAGE, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mUpdateLanguageTask = null;
            hideLoading();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        if (setLaguage.equals("eng")) {
                            JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_LANGUAGE, "en");
                        } else if (setLaguage.equals("ara")) {
                            JaribhaPrefrence.setPref(getActivity(), Constants.PROJECT_LANGUAGE, "ar");
                        }

                        Utils.changeLangauge(JaribhaPrefrence.getPref(SelectLanguageActivity.this, Constants.PROJECT_LANGUAGE, "en"), SelectLanguageActivity.this);
                        btn_update.setText(getString(R.string.update));

                        String message = jsonObject.getString("msg");
                        showCustomeDialog(R.drawable.right_icon, "Success", message, getString(R.string.dgts__okay), R.drawable.btn_bg_green);
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
            showLoading();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_USER_INFO, nameValuePairs);
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
                        if (!TextUtils.isNullOrEmpty(userData.language)) {
                            if (userData.language.equals("eng")) {
                                eng.setChecked(true);
                                //JaribhaPrefrence.setPref(this, Constants.PROJECT_LANGUAGE,"ar");
                            }
                            if (userData.language.equals("ara")) {
                                arabic.setChecked(true);
                                //JaribhaPrefrence.setPref(this, Constants.PROJECT_LANGUAGE,"ar");
                            }
                        }
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
    }
}
