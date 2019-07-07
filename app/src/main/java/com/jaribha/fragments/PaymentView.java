package com.jaribha.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.activity.PaymentActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.fonts.FontButton;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentView extends BaseFragment {

    private WebView webView;

    private FontButton doneBtn;

    private Dialog dialog;

    private ProgressDialog progressDialog;

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_webview, container, false);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter(Constants.Action_PAYMENT));

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        webView = (WebView) view.findViewById(R.id.payView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.setWebViewClient(webViewClient);

        doneBtn = (FontButton) view.findViewById(R.id.donePayment);
        if (isAdded())
            doneBtn.setText(activity.getResources().getString(R.string.dgts__done));

        doneBtn.setEnabled(false);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDialog();
            }
        });
        doneBtn.setVisibility(View.GONE);

        String data = JaribhaPrefrence.getPref(activity, Constants.PAYMENT_JSON, "");
        if (!TextUtils.isNullOrEmpty(data)) {
            try {
                JSONObject object = new JSONObject(data);
                webView.loadUrl(object.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(getClass().getName(), url);
            // KNET---  http://jaribha.inventmedia.info/jaribha/jpayment/knet_payment_error?PaymentID=5118270161561260
            // Card---   http://jaribha.inventmedia.info/jaribha/jpayment/creditcard_payment_reponse.json?Title=Some+Title&vpc_AVSResultCode=Unsupported&vpc_AcqAVSRespCode=Unsupported&vpc_AcqCSCRespCode=Unsupported&vpc_AcqResponseCode=10&vpc_Amount=310&vpc_BatchNo=20160505&vpc_CSCResultCode=Unsupported&vpc_Card=MC&vpc_Command=pay&vpc_Locale=en&vpc_MerchTxnRef=20160505121941&vpc_Merchant=TESTGBK_KWD&vpc_Message=Unspecified+Failure&vpc_OrderInfo=VPC+Example&vpc_ReceiptNo=612622391332&vpc_RiskOverallResult=ACC&vpc_SecureHash=08F7BE2A0725DE6CF3A10BCF338EBAB1&vpc_TransactionNo=2000005050&vpc_TxnResponseCode=1&vpc_Version=1
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //showLoading();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            showToast("Oh no! " + description);
        }


    };

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.getAction().equals(Constants.Action_PAYMENT)) {
                String message = intent.getStringExtra(Constants.DATA);
                if (message.equalsIgnoreCase("payment success")) {
                    successDialog();
                } else {
                    doneBtn.setEnabled(false);
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void successDialog() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.setCancelable(false);

        Button btn_dialog = (Button) dialog.findViewById(R.id.btn_dialog);
        btn_dialog.setBackgroundColor(Color.parseColor("#EC6D5E"));
        if (isAdded())
            btn_dialog.setText(getString(R.string.thanks));

        TextView tv_dialog_subtitle = (TextView) dialog.findViewById(R.id.tv_dialog_subtitle);
        if (isAdded())
            tv_dialog_subtitle.setText(getString(R.string.your_payment_has_been_successfully_performed));

        TextView tv_dialog_title = (TextView) dialog.findViewById(R.id.tv_dialog_title);
        if (isAdded())
            tv_dialog_title.setText(getString(R.string.payment_successfully));

        ImageView img_dialog = (ImageView) dialog.findViewById(R.id.img_dialog);
        img_dialog.setImageResource(R.drawable.right_icon);

        LinearLayout dialog_main_container = (LinearLayout) dialog.findViewById(R.id.dialog_main_container);

        if (isTabletDevice()) {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(activity) * 40) / 100;
        }/* else {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(activity) * 80) / 100;
        }*/

        btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard(v);
                dialog.dismiss();
                ((PaymentActivity) activity).displayFragment(12, true);
            }
        });

        dialog.show();
    }
}
