package com.jaribha.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.jaribha.R;
import com.jaribha.activity.EditProjectActivity;
import com.jaribha.base.BaseFragment;

import java.util.regex.Pattern;

public class EditProjectGuideLine extends BaseFragment {

    Button btn_start_new_project;

    private Activity activity;

    WebView webView;

    private ProgressDialog progressDialog;

    CheckBox termsCheckBox;

    private boolean checkboxChecked = false;

    private String currentUrl;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint({"InflateParams", "SetJavaScriptEnabled"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_proj_guidelines_webview, null);
        btn_start_new_project = (Button) view.findViewById(R.id.btn_start_new_project);

        btn_start_new_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkboxChecked) {
                    ((EditProjectActivity) activity).firstLoad = false;
                    if (!isTabletDevice())
                        ((EditProjectActivity) activity).displayTab(1, false);
                    else {
                        ((EditProjectActivity) activity).displayFragment(1, false);
                    }
                } else {
                    showToast(getString(R.string.please_accept_terms_and_condition_privacy_policy));
                }
            }
        });

        btn_start_new_project.setText(getString(R.string.edit_project));

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        //showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();

        termsCheckBox = (CheckBox) view.findViewById(R.id.termsCheckBox);
        String styledText = getString(R.string.i_understand_that_in_order_to_launch) + " <font color='red'>" + getString(R.string.terms) + "</font> & <font color='red'>" + getString(R.string.privacy) + ".</font>";
        termsCheckBox.setText(Html.fromHtml(styledText));
        termsCheckBox.setLinkTextColor(Color.RED);

        Pattern pattern1 = Pattern.compile(getString(R.string.terms_and_conditions));
        Linkify.addLinks(termsCheckBox, pattern1, "terms-activity://");

        Pattern pattern3 = Pattern.compile(getString(R.string.privacy_policy));
        Linkify.addLinks(termsCheckBox, pattern3, "privacy-activity://");

        termsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkboxChecked = isChecked;
            }
        });

        webView = (WebView) view.findViewById(R.id.webView);
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(true);
        webSetting.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.clearHistory();
        webView.clearFormData();
        webView.clearCache(true);

        if (isArabic())
            currentUrl = "http://jaribha.inventmedia.info/jaribha/jpages/htmlpage?language=arab&pagename=showmore";
        else
            currentUrl = "http://jaribha.inventmedia.info/jaribha/jpages/htmlpage?language=eng&pagename=showmore";

        webView.loadUrl(currentUrl);

        return view;
    }

    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.equals(currentUrl)) {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
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
}
