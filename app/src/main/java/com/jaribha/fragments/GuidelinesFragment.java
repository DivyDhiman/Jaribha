package com.jaribha.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.djhs16.net.JSONParser;
import com.eyeem.recyclerviewtools.Log;
import com.jaribha.R;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.activity.MyPortfolioActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.utility.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class GuidelinesFragment extends BaseFragment implements View.OnClickListener {

    private ProgressDialog progressDialog;

    WebView webView;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.guideline_fragment, container, false);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        if (isInternetConnected())
            loadTermsData();
        else
            showNetworkDialog();
        return view;
    }

    public void loadTermsData() {
        try {
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();

            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            String uid = getUser() == null ? "" : getUser().id;
            String utoken = getUser() == null ? "" : getUser().user_token;

            projectJson.put("user_id", uid);
            projectJson.put("user_token", utoken);

            GetGuideLineData mAuthTask = new GetGuideLineData(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetGuideLineData extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetGuideLineData(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_GUIDELINES, nameValuePairs);
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

                    if (status) {
                        String data;
                        if (isArabic()) {
                            data = jsonObject.optString("data");
                            data = data.substring(data.lastIndexOf("=") + 1);
                            data = data.substring(data.lastIndexOf("=") + 1);
                            webView.loadUrl(Urls.BASE_URL_abarb+data);
                        } else {
                            data = jsonObject.optString("data");
                            data = data.substring(data.lastIndexOf("=") + 1);
                            data = data.substring(data.lastIndexOf("=") + 1);
                            webView.loadUrl(Urls.BASE_URL_eng+data);
                        }
                    } else {
                        if (msg.equalsIgnoreCase("Data Not Found")) {
                            Log.d(getClass().getName(), "Data Not Found");
                        } else if (msg.equalsIgnoreCase("User Not Found")) {
                            showSessionDialog();
                        } else if (!msg.equalsIgnoreCase("Data Not Found")) {
                            showServerErrorDialog();
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
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_close:

                break;
            case R.id.img_user_image:
                if (getUser() != null)
                    startActivity(new Intent(activity, MyPortfolioActivity.class));
                else {
                    startActivity(new Intent(activity, LoginScreenActivity.class));
                    //activity.finish();
                }
                break;

            default:
                break;
        }
    }
}
