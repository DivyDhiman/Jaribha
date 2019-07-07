package com.jaribha.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jaribha.base.BaseAppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class NewGooglePlus extends BaseAppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "SignInActivity";

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // showLoading();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

       // hideLoading();
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //hideLoading();
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        Intent resultIntent = new Intent();
      //  hideLoading();
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            String google_id = acct.getId();
            String google_user_name = acct.getDisplayName();
           // String google_profile_pic = acct.getPhotoUrl().toString();

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("google_id", google_id);
                jsonObject.put("google_user_name", google_user_name);
                jsonObject.put("google_profile_pic", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            resultIntent.putExtra("google_data", "" + jsonObject);
            setResult(RESULT_OK, resultIntent);
            finish();

        } else {
            setResult(RESULT_CANCELED, resultIntent);
        }
        finish();
    }

}