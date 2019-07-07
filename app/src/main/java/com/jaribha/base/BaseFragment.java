package com.jaribha.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.jaribha.R;
import com.jaribha.models.UserData;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Utils;

import java.text.DecimalFormat;

public class BaseFragment extends Fragment {

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.changeLangauge(JaribhaPrefrence.getPref(getActivity(), Constants.PROJECT_LANGUAGE, "en"), getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        Utils.changeLangauge(JaribhaPrefrence.getPref(getActivity(), Constants.PROJECT_LANGUAGE, "en"), getActivity());
    }

    public void showLoading() {
        ((BaseAppCompatActivity) getActivity()).showLoading();
    }

    public boolean isArabic() {
        return ((BaseAppCompatActivity) getActivity()).isArabic();
    }

    public void showToast(String message) {
        ((BaseAppCompatActivity) getActivity()).showToast(message);
    }

    public void hideLoading() {
        ((BaseAppCompatActivity) getActivity()).hideLoading();
    }

    public static void changeLocale(Context ctx, String lng) {
        Utils.changeLangauge(lng, ctx);
    }

    public void hideKeyBoard(View v) {
        ((BaseAppCompatActivity) getActivity()).hideKeyBoard(v);
    }

    public Boolean isInternetConnected() {
        return ((BaseAppCompatActivity) getActivity()).isInternetConnected();
    }

    public Boolean isTabletDevice() {
//        return ((BaseAppCompatActivity) getActivity()).isTablet();
        return getResources().getBoolean(R.bool.isTablet);
    }

    public void showAlertDialog(String message) {
        showAlertDialog(null, message);
    }

    public void showAlertDialog(String title, String message) {
        ((BaseAppCompatActivity) getActivity()).showAlertDialog(title, message);
    }

    public void videoPlay(String url) {
        Utils.playVideo(url, getActivity());
    }

    public LatLng getMyLocation() {
        //return ((BaseAppCompatActivity) getActivity()).getMyLocation();
        return ((BaseAppCompatActivity) mActivity).getMyLocation();
    }

    public void displayImage(Context context, ImageView imageView, String url, Drawable drawable) {
        //((BaseAppCompatActivity) getActivity()).displayImage(context, imageView, url, drawable);
        ((BaseAppCompatActivity) mActivity).displayImage(context, imageView, url, drawable);
    }

    public UserData getUser() {
        //return ((BaseAppCompatActivity) getActivity()).getUser();
        return ((BaseAppCompatActivity) mActivity).getUser();
    }

    public void showCustomeDialog(int resourceID, String title, String subTitle, String btnText, int btnResourceID) {
        ((BaseAppCompatActivity) getActivity()).showCustomeDialog(resourceID, title, subTitle, btnText, btnResourceID);
    }

    public void showSessionDialog() {
        //((BaseAppCompatActivity) getActivity()).showSessionDialog();
        ((BaseAppCompatActivity) mActivity).showSessionDialog();
    }

    public void showServerErrorDialog() {
        //((BaseAppCompatActivity) getActivity()).showServerDialogDialog();
        ((BaseAppCompatActivity) mActivity).showServerDialogDialog();
    }

    public void showNetworkDialog() {
        //((BaseAppCompatActivity) getActivity()).showNetworkDialog();
        ((BaseAppCompatActivity) getActivity()).showNetworkDialog();
    }

    public void showDataNotFoundDialog() {
        ((BaseAppCompatActivity) getActivity()).showDataNotFoundDialog();
    }

    public String getSupportPercentage(double amount, double total) {
        double supportAmount = (amount / total) * 100;
        return new DecimalFormat("##.##").format(supportAmount);
    }

    public int getProgressPercent(double amount, double total) {
        double supportAmount = (amount / total) * 100;
        return ((int) supportAmount);
    }
}
