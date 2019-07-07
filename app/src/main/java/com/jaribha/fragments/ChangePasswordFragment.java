package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ChangePasswordFragment extends BaseFragment implements View.OnClickListener {

    private EditText oldPassword, newPassword, confirmNewPassword;

    private Button btn_update;

    private ChangePasswordTask mChangePasswordTask = null;

    private Activity mActivity;

    private ProgressDialog progressDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password, container, false);
        progressDialog = new ProgressDialog(mActivity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        oldPassword = (EditText) view.findViewById(R.id.oldPassword);
        oldPassword.setHint(getString(R.string.old_password));

        newPassword = (EditText) view.findViewById(R.id.newPassword);
        newPassword.setHint(getString(R.string.new_password));

        confirmNewPassword = (EditText) view.findViewById(R.id.confirmNewPassword);
        confirmNewPassword.setHint(getString(R.string.confirm_new_password));

        btn_update = (Button) view.findViewById(R.id.btn_user_update);
        btn_update.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

    public class ChangePasswordTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        ChangePasswordTask(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.CHANGE_PASSWORD, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mChangePasswordTask = null;
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
        } else if (!TextUtils.isNullOrEmpty(newPass) && !Utils.isValidPassword(newPass)) { // Check for a valid password, if the user entered one.
            newPassword.setError(getString(R.string.error_invalid_password));
            focusView = newPassword;
            cancel = true;
        } else if (!TextUtils.isNullOrEmpty(confirmPass) && !Utils.isValidPassword(confirmPass)) {  // Check for a Confirm password.
            confirmNewPassword.setError(getString(R.string.error_invalid_password));
            focusView = confirmNewPassword;
            cancel = true;
        } else if (!newPass.equals(confirmPass) || !confirmPass.equals(newPass)) {
            newPassword.setError(getString(R.string.error_pass_match));
            confirmNewPassword.setError(getString(R.string.error_pass_match));
            focusView = confirmNewPassword;
            cancel = true;
        }else if (!oldPass.equals(JaribhaPrefrence.getPref(getActivity(), Constants.PASSWORD_CHECK,""))) {
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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("apikey", Urls.API_KEY);
                jsonObject.put("user_id", getUser().id);
                jsonObject.put("user_token", getUser().user_token);
                jsonObject.put("email", getUser().email);
                jsonObject.put("new_password", confirmPass);
                JaribhaPrefrence.setPref(getActivity(), Constants.PASSWORD_CHECK, confirmPass);
                mChangePasswordTask = new ChangePasswordTask(jsonObject);
                mChangePasswordTask.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
