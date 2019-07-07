package com.jaribha.activity;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.utility.Methods;

import org.json.JSONObject;

public class GooglePlusLogin extends BaseAppCompatActivity implements
        ConnectionCallbacks, OnConnectionFailedListener,
        ResultCallback<LoadPeopleResult> {

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_plus_login);
        Methods.initThreadPolicy();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Plus.API,
                        Plus.PlusOptions.builder()
                                .addActivityTypes("http://schemas.google.com/AddActivity",
                                        "http://schemas.google.com/BuyActivity").build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        handler = new Handler();
        handler.postDelayed(signInCallBack, 2000);

        showLoading();

    }

    private Runnable signInCallBack = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            signInWithGplus();
        }
    };

    /**
     * Sign-in into google
     */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
                //toastCustom.show("Sign in with Google+ is cancelled");
                finish();
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }

        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
        //Toast.makeText(this, "Logged In", Toast.LENGTH_LONG).show();
        // Get user's information
        handler.removeCallbacks(signInCallBack);
        getProfileInformation();
    }

    /**
     * Fetching user's information name, email, profile pic
     */
    private void getProfileInformation() {
        hideLoading();
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person person = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);

                String google_id = person.getId();
                String google_user_name = person.getDisplayName();
                String google_profile_pic = person.getUrl();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("google_id", google_id);
                jsonObject.put("google_user_name", google_user_name);
                jsonObject.put("google_profile_pic", google_profile_pic);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("google_data", "" + jsonObject);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                //toastCustom.show("Person information is private");
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onResult(@NonNull LoadPeopleResult peopleData) {
        // TODO Auto-generated method stub

    }
}
