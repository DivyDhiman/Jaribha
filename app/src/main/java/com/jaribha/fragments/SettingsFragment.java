package com.jaribha.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.activity.ChangePasswordActivity;
import com.jaribha.activity.HomeScreenActivity;
import com.jaribha.activity.NotificationActivity;
import com.jaribha.activity.SelectLanguageActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Utils;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {

    TextView account, editProfile, notification, selectLanguage;

    Activity activity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        account = (TextView) view.findViewById(R.id.account);
        account.setText(activity.getResources().getString(R.string.account));
        account.setOnClickListener(this);

        editProfile = (TextView) view.findViewById(R.id.editProfile);
        editProfile.setText(activity.getResources().getString(R.string.edit_profile));
        editProfile.setOnClickListener(this);

        notification = (TextView) view.findViewById(R.id.notification);
        notification.setText(activity.getResources().getString(R.string.notification));
        notification.setOnClickListener(this);

        selectLanguage = (TextView) view.findViewById(R.id.selectLanguage);
        selectLanguage.setText(activity.getResources().getString(R.string.change_languages));
        selectLanguage.setOnClickListener(this);

        if (JaribhaPrefrence.getPref(activity, Constants.isSocial, false)) {
            account.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            account.setText(activity.getResources().getString(R.string.account));
            editProfile.setText(activity.getResources().getString(R.string.edit_profile));
            notification.setText(activity.getResources().getString(R.string.notifications));
            selectLanguage.setText(activity.getResources().getString(R.string.change_languages));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context ;
        Utils.changeLangauge(JaribhaPrefrence.getPref(activity, Constants.PROJECT_LANGUAGE, "en"), context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account:
                startActivity(new Intent(activity, ChangePasswordActivity.class));
                break;
            case R.id.editProfile:
                ((HomeScreenActivity) activity).DisplayVIEW(Constants.EDIT_PROFILE_FRAGMENT);
                break;
            case R.id.notification:
                startActivity(new Intent(activity, NotificationActivity.class));
                break;
            case R.id.selectLanguage:
                startActivity(new Intent(activity, SelectLanguageActivity.class));
                break;

            default:

        }
    }
}
