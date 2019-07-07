package com.jaribha.fragments;

import android.annotation.SuppressLint;
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
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.eyeem.recyclerviewtools.Log;
import com.jaribha.R;
import com.jaribha.activity.HomeScreenActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.utility.Urls;

import org.json.JSONException;
import org.json.JSONObject;


public class HowWorksSponsersFragment extends BaseFragment {

    WebView webView;

    private ProgressDialog progressDialog;

    Activity activity;
    private LinearLayout learn_more;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        //showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();

        learn_more = (LinearLayout) view.findViewById(R.id.learn_more);
        learn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeScreenActivity) getActivity()).replaceFragment(new HelpFragment(), HelpFragment.class.getName(), true);
            }
        });

        learn_more.setVisibility(View.GONE);
        webView = (WebView) view.findViewById(R.id.rewards_webv);
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);

        if (isArabic())
            webView.loadUrl("http://jaribha.inventmedia.info/jaribha/jpages/htmlpage?language=arab&pagename=sponsor");
        else
            webView.loadUrl("http://jaribha.inventmedia.info/jaribha/jpages/htmlpage?language=eng&pagename=sponsor");

/*

        if (isInternetConnected())
            loadSponsorsData();
        else
            showNetworkDialog();
*/

/*

        if (isArabic())
            webView.loadUrl("file:///android_asset/sponsor_arab.html");
        else
            webView.loadUrl("file:///android_asset/sponsor.html");
*/

        return view;
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing()){
                progressDialog.dismiss();
                learn_more.setVisibility(View.VISIBLE);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            super.onReceivedError(view, request, error);
        }
    }
    public void loadSponsorsData() {
        try {
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();

            JSONObject projectJson = new JSONObject();
           // projectJson.put("apikey", Urls.API_KEY);

            if (isArabic())
                projectJson.put("language", "arab");
            else
                projectJson.put("language", "eng");

            projectJson.put("pagename", "sponsor");

            GetHelpData mAuthTask = new GetHelpData(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class GetHelpData extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetHelpData(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1("http://jaribha.inventmedia.info/jaribha/pages/.json", nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {

            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");

                    if (status) {
                        String data;
                        data = jsonObject.optString("data");
                        if (data != null)
                            webView.loadData(data, "text/html", "UTF-8");
                    } else {
                        if (msg.equals("Data Not Found")) {
                            Log.d(getClass().getName(), "Data Not Found");
                        } else if (msg.equals("User Not Found")) {
                            showSessionDialog();
                        } else if (!msg.equals("Data Not Found")) {
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
}
