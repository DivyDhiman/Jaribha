package com.jaribha.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
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
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class NewsLetterFragment extends BaseFragment {

    private EditText edt_reg_email;

    Button sendBtn;

    private String email = "";

    private ProgressDialog progressDialog;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.subscribe_news_letter, null);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        edt_reg_email = (EditText) view.findViewById(R.id.edt_reg_email);
        edt_reg_email.setTextColor(Color.WHITE);

        sendBtn = (Button) view.findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = edt_reg_email.getText().toString();
                if (email.isEmpty()) {
                    showToast(getString(R.string.email_required));
                } else if (!Utils.isValidEmailAddress(email)) {
                    showToast(getString(R.string.please_enter_valid_email));
                } else {
                    if (isInternetConnected()) {
                        getNewsLetterData();
                    } else {
                        showNetworkDialog();
                    }
                }
            }
        });

        return view;
    }

    private void getNewsLetterData() {
        //showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("useremail", email);

            String uid = getUser() == null ? "" : getUser().id;

            jsonObject.put("user_id", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isInternetConnected()) {
            new NewsLetterTask(jsonObject).execute();
        } else {
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            showNetworkDialog();
        }
    }

    public class NewsLetterTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject jsonObject;

        NewsLetterTask(JSONObject params) {
            this.jsonObject = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.NEWS_LETTER, jsonObject);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            //mAuthTask = null;
            //hideLoading();//
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        edt_reg_email.setText("");
                        if (msg.equalsIgnoreCase("Newsletter Subscribed Suceesfully"))
                            showCustomeDialog(R.drawable.right_icon, getString(R.string.success), msg, getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                        else
                            showCustomeDialog(R.drawable.icon_error, getString(R.string.error), msg, getString(R.string.dgts__okay), R.drawable.red_strip);
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
            //mAuthTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }
}
