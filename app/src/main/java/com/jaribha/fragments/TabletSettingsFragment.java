package com.jaribha.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;


public class TabletSettingsFragment extends BaseFragment implements View.OnClickListener {

    TextView account, editProfile, notification, selectLanguage, titleScreen;

    private FragmentManager fragmentManager;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context ;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("com.settings"));

        account = (TextView) view.findViewById(R.id.account);
        editProfile = (TextView) view.findViewById(R.id.editProfile);
        titleScreen = (TextView) view.findViewById(R.id.setting_title);
        notification = (TextView) view.findViewById(R.id.notification);
        selectLanguage = (TextView) view.findViewById(R.id.selectLanguage);

        if(isAdded()) {
            account.setText(activity.getResources().getString(R.string.account));
            editProfile.setText(activity.getResources().getString(R.string.edit_profile));
            notification.setText(activity.getResources().getString(R.string.notifications));
            selectLanguage.setText(activity.getResources().getString(R.string.change_languages));
        }

        fragmentManager = getChildFragmentManager();

        account.setOnClickListener(this);
        editProfile.setOnClickListener(this);
        notification.setOnClickListener(this);
        selectLanguage.setOnClickListener(this);

        if (JaribhaPrefrence.getPref(activity, Constants.isSocial, false)) {
            account.setVisibility(View.GONE);
            displayView(new EditProfileFragment());
        }else{
            displayView(new ChangePasswordFragment());
        }

        if(isAdded())
        titleScreen.setText(activity.getResources().getString(R.string.edit_profile));

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account:
                displayView(new ChangePasswordFragment());
                if(isAdded())
                titleScreen.setText(activity.getResources().getString(R.string.account));
                break;
            case R.id.editProfile:
                displayView(new EditProfileFragment());
                if(isAdded())
                titleScreen.setText(activity.getResources().getString(R.string.edit_profile));
                break;
            case R.id.notification:
                displayView(new NotificationFragment());
                if(isAdded())
                titleScreen.setText(activity.getResources().getString(R.string.notification));
                break;
            case R.id.selectLanguage:
                displayView(new SelectLanguageFragment());
                if(isAdded())
                titleScreen.setText(activity.getResources().getString(R.string.select_language));
                break;

            default:

        }
    }
    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.getAction().equals("com.settings")) {

                account.setText(activity.getResources().getString(R.string.account));
                editProfile.setText(activity.getResources().getString(R.string.edit_profile));
                notification.setText(activity.getResources().getString(R.string.notifications));
                selectLanguage.setText(activity.getResources().getString(R.string.change_languages));

                Fragment fragment = fragmentManager.findFragmentById(R.id.settingsContainer);

                String fragClassName = fragment.getClass().getName();

                if (fragClassName.equals(ChangePasswordFragment.class.getName())) {
                    titleScreen.setText(activity.getResources().getString(R.string.account));
                }if (fragClassName.equals(EditProfileFragment.class.getName())) {
                    titleScreen.setText(activity.getResources().getString(R.string.edit_profile));
                }if (fragClassName.equals(NotificationFragment.class.getName())) {
                    titleScreen.setText(activity.getResources().getString(R.string.notification));
                }if (fragClassName.equals(SelectLanguageFragment.class.getName())) {
                    titleScreen.setText(activity.getResources().getString(R.string.select_language));
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


    private void displayView(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.settingsContainer, fragment);
        fragmentTransaction.commit();
    }
}
