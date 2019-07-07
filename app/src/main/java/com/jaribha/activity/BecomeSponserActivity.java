package com.jaribha.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class BecomeSponserActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView iv_close;

    TextView tv_title;

    BezelImageView img_user_image;

    private EditText edt_full_name, edt_company_name, edt_email, edt_phone;

    Button btn_send;

    private String full_name = "", company_name = "", email = "", phone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_sponser);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        btn_send = (Button) findViewById(R.id.btn_send);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.become_a_sponser));

        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.close_icon);
        iv_close.setOnClickListener(this);

        img_user_image.setVisibility(View.GONE);

        edt_full_name = (EditText) findViewById(R.id.edt_full_name);
        edt_full_name.setHint(getActivity().getResources().getString(R.string.full_name));

        edt_company_name = (EditText) findViewById(R.id.edt_company_name);
        edt_company_name.setHint(getActivity().getResources().getString(R.string.company_name));

        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_email.setHint(getActivity().getResources().getString(R.string.email));

        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_phone.setHint(getActivity().getResources().getString(R.string.phone));

        btn_send.setOnClickListener(this);
        btn_send.setText(getString(R.string.submit));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_close:
                finish();
                break;

            case R.id.btn_send:
                edt_full_name.setError(null);
                edt_email.setError(null);
                edt_company_name.setError(null);
                edt_phone.setError(null);

                boolean cancel = false;
                View focusView = null;

                full_name = edt_full_name.getText().toString().trim();
                company_name = edt_company_name.getText().toString().trim();
                email = edt_email.getText().toString().trim();
                phone = edt_phone.getText().toString().trim();

                if (TextUtils.isNullOrEmpty(edt_email.getText().toString())) {
                    edt_email.setError(getString(R.string.error_field_required));
                    focusView = edt_email;
                    cancel = true;
                } else if (!TextUtils.isValidEmail(edt_email.getText().toString())) {
                    edt_email.setError(getString(R.string.incorrect_email));
                    focusView = edt_email;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_full_name.getText().toString())) {
                    edt_full_name.setError(getString(R.string.error_field_required));
                    focusView = edt_full_name;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_phone.getText().toString())) {
                    edt_phone.setError(getString(R.string.error_field_required));
                    focusView = edt_phone;
                    cancel = true;
                } else if (!TextUtils.isValidMobile(edt_phone.getText().toString())) {
                    edt_phone.setError(getString(R.string.incorrect_mobile));
                    focusView = edt_phone;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_company_name.getText().toString())) {
                    edt_company_name.setError(getString(R.string.error_field_required));
                    focusView = edt_company_name;
                    cancel = true;
                }

               /* if (TextUtils.isNullOrEmpty(full_name)) {
                    showToast("Full name required");
                } else if (TextUtils.isNullOrEmpty(company_name)) {
                    showToast("Company name required");
                }else if (TextUtils.isNullOrEmpty(email)) {
                    showToast("Email id required");
                } else if (!Utils.isValidEmailAddress(email)) {
                    showToast("Please enter valid email id");
                } else if (TextUtils.isNullOrEmpty(phone)) {
                    showToast("Mobile number required");
                } else if (isInternetConnected()) {
                    submitSponsor();
                } else {
                    hideLoading();
                    showNetworkDialog();
                }*/
                if (cancel) {
                    focusView.requestFocus();
                } else {
                    if (isInternetConnected()) {
                        submitSponsor();
                    } else {
                        showNetworkDialog();
                    }
                }
                break;

            default:
                break;

        }
    }

    private void submitSponsor() {
        showLoading();
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", getUser().id);
            jsonObject.put("user_token", getUser().user_token);
            jsonObject.put("fullname", full_name);
            jsonObject.put("companyname", company_name);
            jsonObject.put("email", email);
            jsonObject.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isInternetConnected()) {
            new BecomeSponsorTask(jsonObject).execute();
        } else {
            hideLoading();
            showNetworkDialog();
        }

    }

    public class BecomeSponsorTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject jsonObject;

        BecomeSponsorTask(JSONObject params) {
            this.jsonObject = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.BECOME_SPONSOR, jsonObject);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            hideLoading();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        edt_full_name.setText("");
                        edt_email.setText("");
                        edt_phone.setText("");
                        edt_company_name.setText("");
                        showCustomeDialog(R.drawable.message_icon, getString(R.string.success1), getString(R.string.sponsor_submitted_successfully), getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                    } else {
                        if (jsonObject.getString("msg").equals("Data Not Found")) {
                            showDataNotFoundDialog();
                        } else if (jsonObject.getString("msg").equals("User Not Found")) {
                            showSessionDialog();
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
