package com.jaribha.activity;

import android.content.Intent;
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
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ImageView iv_close;

    private TextView tv_title;

    private EditText oldPassword, newPassword, confirmNewPassword;

    private Button btn_update;

    BezelImageView img_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        img_user = (BezelImageView) findViewById(R.id.img_user_image);
        displayUserImage(img_user);
        img_user.setOnClickListener(this);

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.account));

        oldPassword = (EditText) findViewById(R.id.oldPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        confirmNewPassword = (EditText) findViewById(R.id.confirmNewPassword);

        btn_update = (Button) findViewById(R.id.btn_user_update);
        btn_update.setOnClickListener(this);
        btn_update.setText(getString(R.string.update));


        oldPassword.setHint(getString(R.string.old_password));
        newPassword.setHint(getString(R.string.new_password));
        confirmNewPassword.setHint(getString(R.string.confirm_new_password));
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
                if (isInternetConnected())
                    changePassword();
                else
                    showNetworkDialog();
                break;

            default:
                break;

        }
    }

    private ChangePasswordTask mChangePasswordTask = null;

    public class ChangePasswordTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        ChangePasswordTask(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.CHANGE_PASSWORD, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mChangePasswordTask = null;
            hideLoading();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        oldPassword.setText("");
                        newPassword.setText("");
                        confirmNewPassword.setText("");
                        String message = jsonObject.getString("msg");
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
            mChangePasswordTask = null;
            hideLoading();
        }
    }

    private void changePassword() {
        oldPassword.setError(null);
        newPassword.setError(null);
        confirmNewPassword.setError(null);
        String oldPass = oldPassword.getText().toString().trim();
        String newPass = newPassword.getText().toString().trim();
        String confirmPass = confirmNewPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isNullOrEmpty(oldPass)) {
            oldPassword.setError(getString(R.string.error_field_required));
            focusView = oldPassword;
            cancel = true;
        }else if (TextUtils.isNullOrEmpty(newPass)) {
            newPassword.setError(getString(R.string.error_field_required));
            focusView = newPassword;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(confirmPass)) {
            confirmNewPassword.setError(getString(R.string.error_field_required));
            focusView = confirmNewPassword;
            cancel = true;
        }  else if (!TextUtils.isNullOrEmpty(newPass) && !Utils.isValidPassword(newPass)) { // Check for a valid password, if the user entered one.
            newPassword.setError(getString(R.string.error_invalid_password));
            focusView = newPassword;
            cancel = true;
        } else if (!TextUtils.isNullOrEmpty(confirmPass) && !Utils.isValidPassword(confirmPass)) { // Check for a Confirm password.
            confirmNewPassword.setError(getString(R.string.error_invalid_password));
            focusView = confirmNewPassword;
            cancel = true;
        } else if (!newPass.equals(confirmPass) || !confirmPass.equals(newPass)) {
            newPassword.setError(getString(R.string.error_pass_match));
            confirmNewPassword.setError(getString(R.string.error_pass_match));
            focusView = confirmNewPassword;
            cancel = true;
        } else if (!oldPass.equals(JaribhaPrefrence.getPref(getActivity(), Constants.PASSWORD_CHECK, ""))) {
            oldPassword.setError(getString(R.string.invalid_old_password));
            focusView = oldPassword;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            try {
                JSONObject ChangePassJsonObject = new JSONObject();
                ChangePassJsonObject.put("apikey", Urls.API_KEY);
                ChangePassJsonObject.put("user_id", getUser().id);
                ChangePassJsonObject.put("user_token", getUser().user_token);
                ChangePassJsonObject.put("email", getUser().email);
                ChangePassJsonObject.put("new_password", confirmPass);
                JaribhaPrefrence.setPref(this, Constants.PASSWORD_CHECK, confirmPass);
                mChangePasswordTask = new ChangePasswordTask(ChangePassJsonObject);
                mChangePasswordTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
