package com.jaribha.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.CustomDialogClass;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends BaseAppCompatActivity implements View.OnClickListener, TextWatcher {

    ImageView iv_close;

    BezelImageView img_user_image;

    TextView tv_title, goBack;

    EditText edt_reg_email;

    Button sendBtn;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.GONE);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(R.string.forgot_password);

        edt_reg_email = (EditText) findViewById(R.id.edt_reg_email);
        edt_reg_email.setTextColor(Color.WHITE);
        edt_reg_email.addTextChangedListener(this);

        sendBtn = (Button) findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);

        goBack = (TextView) findViewById(R.id.goBack);
        goBack.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        goBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                // intent = new Intent(ForgotPasswordActivity.this, LoginScreenActivity.class);
                //startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
                break;

            case R.id.sendBtn:
                if (isInternetConnected()) {
                    sendForgetPassword();
                } else {
                    showNetworkDialog();
                }
                break;

            case R.id.goBack:
                //intent = new Intent(ForgotPasswordActivity.this, LoginScreenActivity.class);
                //startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ForgotPasswordTask mForgotPassTask = null;

    /**
     * Represents an asynchronous Forgot password task used to remind password via sent mail on valid/Registered
     * E-mail id of user.
     */

    public class ForgotPasswordTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        ForgotPasswordTask(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // TODO: register the new account here.
            return new JSONParser().getJsonObjectFromUrl1(Urls.FORGET_PASSWORD, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mForgotPassTask = null;
            hideLoading();
            if (responce != null) {
                try {

                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String message = jsonObject.getString("msg");
                    if (status) {
                        showCustomeDialog(R.drawable.right_icon, getString(R.string.success), message, getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                    } else {
                        showCustomeDialog(R.drawable.icon_error, getString(R.string.error), message, getString(R.string.try_again), R.drawable.btn_bg_green);
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
            mForgotPassTask = null;
            hideLoading();
        }
    }

    public void sendForgetPassword() {
        edt_reg_email.setError(null);

        // Store values at the time of sent E-mail.
        String email = edt_reg_email.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isNullOrEmpty(email)) {
            //edt_reg_email.setError(getString(R.string.error_field_required));
            showCustomeDialog(R.drawable.icon_error, getString(R.string.error), getString(R.string.incorrect_email), getString(R.string.try_again), R.drawable.btn_bg_green);
            focusView = edt_reg_email;
            cancel = true;
        } else if (!Utils.isValidEmailAddress(email)) {
            //edt_reg_email.setError(getString(R.string.error_invalid_email));
            showCustomeDialog(R.drawable.icon_error, getString(R.string.error), getString(R.string.incorrect_email), getString(R.string.try_again), R.drawable.btn_bg_green);
            focusView = edt_reg_email;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            try {
                JSONObject ForgotPassJsObj = new JSONObject();
                ForgotPassJsObj.put("apikey", Urls.API_KEY);
                ForgotPassJsObj.put("email", email);

                mForgotPassTask = new ForgotPasswordTask(ForgotPassJsObj);
                mForgotPassTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (edt_reg_email.getText().length() > 0) {
            edt_reg_email.setError(null);
        }
    }

    public void showCustomeDialog(int resourceID, String title, String subTitle, String btnText, int btnResourceID) {
        CustomDialogClass cdd;
        if (isTablet()) {
            cdd = new CustomDialogClass(ForgotPasswordActivity.this, resourceID, title, subTitle, btnText, btnResourceID, true);
        } else {
            cdd = new CustomDialogClass(ForgotPasswordActivity.this, resourceID, title, subTitle, btnText, btnResourceID, false);
        }
        //CustomDialogClass cdd = new CustomDialogClass(ForgotPasswordActivity.this, resourceID, title, subTitle, btnText, btnResourceID);
        cdd.show();
    }
}
