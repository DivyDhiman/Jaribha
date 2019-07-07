package com.jaribha.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
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

import com.jaribha.R;
import com.jaribha.base.BaseFragment;

public class HelpFragment extends BaseFragment {

    WebView webView;

    private Activity mActivity;

    private ProgressDialog progressDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rewards, container, false);
        progressDialog = new ProgressDialog(mActivity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(mActivity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();

        webView = (WebView) view.findViewById(R.id.rewards_webv);

        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);

       /* if (isInternetConnected())
            loadHelpData();
        else
            showNetworkDialog();*/

        if (isArabic())
            webView.loadUrl("http://jaribha.inventmedia.info/jaribha/jpages/htmlpage?language=arab&pagename=help");
        else
            webView.loadUrl("http://jaribha.inventmedia.info/jaribha/jpages/htmlpage?language=eng&pagename=help");

        return view;
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

//    public void loadHelpData() {
//        try {
//            if (progressDialog != null && !progressDialog.isShowing())
//                progressDialog.show();
//
//            JSONObject projectJson = new JSONObject();
//            //  projectJson.put("apikey", Urls.API_KEY);
//
//            if (isArabic())
//                projectJson.put("language", "arab");
//            else
//                projectJson.put("language", "eng");
//
//            projectJson.put("pagename", "help");
//
//            GetHelpData mAuthTask = new GetHelpData(projectJson);
//            mAuthTask.execute();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public class GetHelpData extends AsyncTask<Void, Void, JSONObject> {
//
//        JSONObject nameValuePairs;
//
//        GetHelpData(JSONObject params) {
//            this.nameValuePairs = params;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected JSONObject doInBackground(Void... params) {
//            return new JSONParser().getJsonObjectFromUrl1("http://jaribha.inventmedia.info/jaribha/pages/.json", nameValuePairs);
//        }
//
//        @Override
//        protected void onPostExecute(final JSONObject responce) {
//
//            if (responce != null) {
//                try {
//                    JSONObject jsonObject = responce.getJSONObject("result");
//                    Boolean status = jsonObject.getBoolean("status");
//                    String msg = jsonObject.optString("msg");
//
//                    if (status) {
//                        String data;
//                        data = jsonObject.optString("data");
//                        if (data != null)
//                            webView.loadData(data, "text/html", "UTF-8");
//                    } else {
//                        if (msg.equals("Data Not Found")) {
//                            Log.d(getClass().getName(), "Data Not Found");
//                        } else if (msg.equals("User Not Found")) {
//                            showSessionDialog();
//                        } else if (!msg.equals("Data Not Found")) {
//                            showServerErrorDialog();
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    showServerErrorDialog();
//                }
//            } else {
//                showServerErrorDialog();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//
//        }
//    }

}
