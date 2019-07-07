package com.jaribha.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.eyeem.recyclerviewtools.Log;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.utility.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class PrivacyActivity extends BaseAppCompatActivity implements View.OnClickListener {

    BezelImageView userPic;

    private ProgressDialog progressDialog;

    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        progressDialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.privacy_policy));

        userPic = (BezelImageView) findViewById(R.id.img_user_image);
        userPic.setOnClickListener(this);
        userPic.setVisibility(View.GONE);

        displayUserImage(userPic);

        ImageView iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        if (isInternetConnected())
            loadTermsData();
        else
            showNetworkDialog();
    }

    public void loadTermsData() {
        try {
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();

            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            GetTermsData mAuthTask = new GetTermsData(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetTermsData extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetTermsData(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_TERMS, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {

            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();

            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");

                    if (status && !msg.equalsIgnoreCase("Data Not Found")) {
                        String data;
                        JSONObject data_json = jsonObject.optJSONObject("data");

                        if (data_json != null && !data_json.equals("")) {

                            if (isArabic()) {
                                data = data_json.optJSONObject("Policy").optString("policy_eng");

                            } else {
                                data = data_json.optJSONObject("Policy").optString("policy_ara");

                            }

                            webView.loadData(data, "text/html", "UTF-8");
                        } else {
                            Log.d(getClass().getName(), "Data Not Found");
                        }
                    } else {
                        if (msg.equals("Data Not Found")) {
                            Log.d(getClass().getName(), "Data Not Found");
                        } else if (msg.equals("User Not Found")) {
                            showSessionDialog();
                        } else if (!msg.equals("Data Not Found")) {
                            showServerDialogDialog();
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

        }
    }

    //WebView client to load url
    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            hideLoading();
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            showLoading();
            super.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:
                finish();
                break;

            case R.id.img_user_image:
                startActivity(new Intent(getActivity(), MyPortfolioActivity.class));
                break;

            default:
                break;
        }
    }
}
