package com.jaribha.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.jaribha.R;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.CustomDataNoFoundDialog;
import com.jaribha.customviews.CustomDialogClass;
import com.jaribha.customviews.CustomNetworkDialog;
import com.jaribha.customviews.CustomServerErrorDialog;
import com.jaribha.customviews.CustomSessionDialog;
import com.jaribha.customviews.TextDrawable;
import com.jaribha.models.UserData;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.GPSTracker;
import com.jaribha.utility.SessionManager;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Utils;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class BaseAppCompatActivity extends BaseActivity {

    private Toolbar toolbar;
    RelativeLayout mViewLoading;
    private ProgressDialog progressDialog;
    TextDrawable.IBuilder mDrawableBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.changeLangauge(JaribhaPrefrence.getPref(this, Constants.PROJECT_LANGUAGE, "en"), this);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Utils.changeLangauge(JaribhaPrefrence.getPref(this, Constants.PROJECT_LANGUAGE, "en"), this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.tool_bar_layout);
        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.containerLayout);
        viewGroup.addView(getLayoutInflater().inflate(layoutResID, null));
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        mViewLoading = (RelativeLayout) findViewById(R.id.loading_view);
        mViewLoading.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(BaseAppCompatActivity.this, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        mDrawableBuilder = TextDrawable.builder().beginConfig()
                .withBorder(4)
                .endConfig()
                .round();
    }

    public Toolbar getToolBar() {
        return toolbar;
    }

    public void showToast(String message) {
        if (!TextUtils.isNullOrEmpty(message))
            Utils.showDataToast(message, BaseAppCompatActivity.this);
    }

    public boolean isArabic() {
        return JaribhaPrefrence.getPref(BaseAppCompatActivity.this, Constants.PROJECT_LANGUAGE, "en").equalsIgnoreCase("ar");
    }

    public static void changeLocale(Context ctx, String lng) {
        Utils.changeLangauge(lng, ctx);
    }

    public boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    public int getDeviceWidth() {
        return Utils.getDeviceWidth(BaseAppCompatActivity.this);
    }

    public int getDeviceHeight() {
        return Utils.getDeviceHeight(BaseAppCompatActivity.this);
    }

    public void showLoading() {
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    public void videoPlay(String url) {
        Utils.playVideo(url, this);
    }

    public void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void hideKeyBoard(View v) {
        Utils.hideKeyBoard(BaseAppCompatActivity.this, v);
    }

    public Boolean isInternetConnected() {
        return Utils.isInternetConnected(BaseAppCompatActivity.this);
    }

    public String getSupportPercentage(double amount, double total) {
        double supportAmount = (amount / total) * 100;
        return new DecimalFormat("##.##").format(supportAmount);
    }

    public int getProgressPercent(double amount, double total) {
        double supportAmount = (amount / total) * 100;
        return ((int) supportAmount);
    }

    public void showAlertDialog(String message) {
        if (!TextUtils.isNullOrEmpty(message))
            showAlertDialog(null, message);
    }

    public void showAlertDialog(String title, String message) {
        if (!TextUtils.isNullOrEmpty(message))
            Utils.showAlertDialog(BaseAppCompatActivity.this, title, message);
    }

    public LatLng getMyLocation() {
        GPSTracker gpsTracker = new GPSTracker(BaseAppCompatActivity.this);
        if (gpsTracker.canGetLocation()) {
            return gpsTracker.getMyLocation();
        } else {
            return null;
        }
    }

    public void displayImage(Context context, ImageView imageView, String url, Drawable drawable) {
        Picasso.with(context).load(url).placeholder(drawable).error(drawable).fit().into(imageView);
    }

    public UserData getUser() {
        return SessionManager.getInstance(this).getUser();
    }

    public void showCustomeDialog(int resourceID, String title, String subTitle, String btnText, int btnResourceID) {
        CustomDialogClass cdd;
        if (isTablet()) {
            cdd = new CustomDialogClass(BaseAppCompatActivity.this, resourceID, title, subTitle, btnText, btnResourceID, true);
        } else {
            cdd = new CustomDialogClass(BaseAppCompatActivity.this, resourceID, title, subTitle, btnText, btnResourceID, false);
        }
        if (!cdd.isShowing())
            cdd.show();
    }

    public void showSessionDialog() {
        CustomSessionDialog cdd;
        if (isTablet()) {
            cdd = new CustomSessionDialog(BaseAppCompatActivity.this, true);
        } else {
            cdd = new CustomSessionDialog(BaseAppCompatActivity.this, false);
        }
        if (!cdd.isShowing())
            cdd.show();
    }

    public void showServerDialogDialog() {
        CustomServerErrorDialog cdd;
        if (isTablet()) {
            cdd = new CustomServerErrorDialog(BaseAppCompatActivity.this, true);
        } else {
            cdd = new CustomServerErrorDialog(BaseAppCompatActivity.this, false);
        }
        if (!cdd.isShowing())
            cdd.show();
    }

    public void showNetworkDialog() {
        CustomNetworkDialog cdd;
        if (isTablet()) {
            cdd = new CustomNetworkDialog(BaseAppCompatActivity.this, true);
        } else {
            cdd = new CustomNetworkDialog(BaseAppCompatActivity.this, false);
        }
        if (!cdd.isShowing())
            cdd.show();
    }

    public void showDataNotFoundDialog() {
        CustomDataNoFoundDialog cdd;
        if (isTablet()) {
            cdd = new CustomDataNoFoundDialog(BaseAppCompatActivity.this, true);
        } else {
            cdd = new CustomDataNoFoundDialog(BaseAppCompatActivity.this, false);
        }
        if (!cdd.isShowing())
            cdd.show();
    }

    public void displayUserImage(BezelImageView img_user_image) {
        if (getUser() != null) {
            String text = getUser().name.trim();
            text = String.valueOf(text.charAt(0));
            TextDrawable drawable = mDrawableBuilder.build(text, Color.parseColor("#EA5D4C"));

            if (TextUtils.isNullOrEmpty(getUser().pictureurl)) {
                img_user_image.setImageDrawable(drawable);
            } else {
                displayImage(this, img_user_image, getUser().pictureurl, drawable);
            }
        } else {
            img_user_image.setImageResource(R.drawable.user_pic);
        }
    }
}
