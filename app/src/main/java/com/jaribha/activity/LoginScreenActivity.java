package com.jaribha.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djhs16.net.JSONParser;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.GetProjects;
import com.jaribha.models.UserData;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Methods;
import com.jaribha.utility.SessionManager;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginScreenActivity extends BaseAppCompatActivity implements View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener {

    private String TAG = getClass().getSimpleName();

    private Intent intent;

    private Button loginBtn;

    private ImageView iv_facebook, iv_linked_in, iv_google, vImage, langIcon;

    private AutoCompleteTextView edt_email;

    private EditText edt_password;

    private TextView tv_dont_have_account, forgetPwd, tv_explore_as_guest, signWith, orText, signupBtn;

    private CallbackManager callbackManager;

    private TwitterAuthClient mTwitterAuthClient;

    private String social_id = "", social_type = "", social_email = "", social_user_name = "", social_profile_pic = "";

    private UserSocialRegistrationTask mSocialAuthTask = null;

    private static String SOCIAL_TYPE_FACEBOOK = "facebook";

    private static String SOCIAL_TYPE_TWITTER = "twitter";

    private static String SOCIAL_TYPE_LINKEDIN = "linkedin";

    private static String SOCIAL_TYPE_GOOGLEPLUS = "google";

    boolean isArabic = false, isRemember = false;

    private CheckBox rememberCheck;

    private SpannableString styledString;

    private int length;

    private ProgressDialog progressDialog;

    private GoogleApiClient mGoogleApiClient;

    private ProfileTracker mProfileTracker;
    private AccessTokenTracker mAccestokentracker;
    String Fbretid, FBretFNAME, FBretEmail, FBretLNAME, FBgender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        //For manage orientation in phone and tablet
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            Toast.makeText(this, "Large screen",Toast.LENGTH_LONG).show();

        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            Toast.makeText(this, "Normal sized screen" , Toast.LENGTH_LONG).show();

        }
        else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            Toast.makeText(this, "Small sized screen" , Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Screen size is neither large, normal or small" , Toast.LENGTH_LONG).show();
        }


        loadMainData();
    }

    /*@Override
    public void onConfigurationChanged(Configuration newConfig) {
        loadMainData();
        super.onConfigurationChanged(newConfig);
    }*/

    @Override
    public void onClick(View view) {
        hideKeyBoard(view);
        switch (view.getId()) {

            case R.id.tv_dont_have_account:
                Intent loginIntent = new Intent(LoginScreenActivity.this, RegisterScreenActivity.class);
                startActivity(loginIntent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;
            case R.id.signupBtn:
                Intent signup = new Intent(LoginScreenActivity.this, RegisterScreenActivity.class);
                startActivity(signup);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;
            case R.id.forgetPwd:
                Intent forgotIntent = new Intent(LoginScreenActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotIntent);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                break;

            case R.id.loginBtn:
                if (isInternetConnected())
                    attemptLogin();
                else
                    showNetworkDialog();
                break;

            case R.id.vImage:
                intent = new Intent(LoginScreenActivity.this, TutorialVideoActivity.class);
                intent.putExtra("video", true);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                break;

            case R.id.eImage:
                if (isArabic) {
                    Utils.changeLangauge("en", this);
                    JaribhaPrefrence.setPref(this, Constants.PROJECT_LANGUAGE, "en");
                    isArabic = false;
                    langIcon.setImageResource(R.drawable.icon_arabic);
                } else {
                    Utils.changeLangauge("ar", this);
                    JaribhaPrefrence.setPref(this, Constants.PROJECT_LANGUAGE, "ar");
                    isArabic = true;
                    langIcon.setImageResource(R.drawable.english);
                }
                edt_email.requestFocus();
                edt_email.setHint(getString(R.string.email));
                edt_password.setHint(getString(R.string.password));
                forgetPwd.setText(getString(R.string.forgot_password));
                tv_explore_as_guest.setText(getString(R.string.explore_as_guest));
                orText.setText(getString(R.string.or));
                signWith.setText(getString(R.string.sign_in_with));
                edt_password.setText(JaribhaPrefrence.getPref(this, Constants.PASSWORD, ""));
                edt_email.setText(JaribhaPrefrence.getPref(this, Constants.USER_NAME, ""));

                styledString = new SpannableString(getString(R.string.dont_have_account_new));

                //length = getString(R.string.dont_have_account).length();
                // change text color
                //styledString.setSpan(new ForegroundColorSpan(Color.parseColor("#E7877C")), length, length, 0);
                tv_dont_have_account.setText(styledString);
                signupBtn.setText(getString(R.string.sign_up_new));
                loginBtn.setText(getString(R.string.login_title));
                rememberCheck.setText(getString(R.string.remember_me));
                break;

            case R.id.iv_facebook:
                if (isInternetConnected()) {
                    social_id = "";
                    social_type = "";
                    social_email = "";
                    social_user_name = "";
                    social_profile_pic = "";
                    OnFacebookClick();
                } else {
                    showNetworkDialog();
                }
                break;

           /* case R.id.iv_twitter:
                if (isInternetConnected()) {
                    social_id = "";
                    social_type = "";
                    social_email = "";
                    social_user_name = "";
                    social_profile_pic = "";
                    onClickTwitter();
                } else {
                    showNetworkDialog();
                }

                break;*/

            case R.id.iv_linked_in:
                if (isInternetConnected()) {
                    social_id = "";
                    social_type = "";
                    social_email = "";
                    social_user_name = "";
                    social_profile_pic = "";
                    linkedInLogin();
                } else {
                    showNetworkDialog();
                }
                break;

            case R.id.iv_google:
                if (isInternetConnected()) {
                    social_id = "";
                    social_type = "";
                    social_email = "";
                    social_user_name = "";
                    social_profile_pic = "";
                    startActivityForResult(new Intent(LoginScreenActivity.this, NewGooglePlus.class), 2015);
                } else {
                    showNetworkDialog();
                }
                break;

            case R.id.tv_explore_as_guest:
//              Constants.LoginAsGuest = true;
                Intent intent2 = new Intent(LoginScreenActivity.this, HomeScreenActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                finish();
                break;

            default:
                break;

        }
    }

//    public void showFriendsInviteDialog() {
//
//        String appLinkUrl = "https://fb.me/1590785191202625";
//        String previewImageUrl = "https://www.mydomain.com/my_invite_image.jpg";
//
//        if (AppInviteDialog.canShow()) {
//            AppInviteContent content = new AppInviteContent.Builder()
//                    .setApplinkUrl(appLinkUrl)
//                    .setPreviewImageUrl(previewImageUrl)
//                    .build();
//            AppInviteDialog.show(LoginScreenActivity.this, content);
//        }
//    }

    //Linked in Login
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE, Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Constants.LoginAsGuest = false;
        hideLoading();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        loadMainData();

    }

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */

    public class UserLoginTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        UserLoginTask(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // showLoading();
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.LOGIN, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mAuthTask = null;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        Utils.setDefaultValues(LoginScreenActivity.this);
                        UserData user = new Gson().fromJson(jsonObject.getJSONObject("data").toString(), UserData.class);
                        SessionManager.getInstance(getActivity()).saveUser(user);
                        Log.d("User Name : ", getUser().name);
                        showToast(getString(R.string.successfully_login));
                        /*JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE, getString(R.string.recently_launched));
                        JaribhaPrefrence.setPref(getActivity(), Constants.CATEGORY_NAME, "all categories");
                        JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE_NAME, getString(R.string.all_features));
                        JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_NAME, "all countries");
                        JaribhaPrefrence.setPref(getActivity(), Constants.FEATURE_ID, "recentlylaunched");
*/
                        GetProjects getProjects = JaribhaPrefrence.get_utils(LoginScreenActivity.this);
                        boolean comment = JaribhaPrefrence.getPref(LoginScreenActivity.this,"comment_push",false);
                        boolean update_push = JaribhaPrefrence.getPref(LoginScreenActivity.this,"update_push",false);

                        if(getProjects != null){

                            JaribhaPrefrence.setPref(LoginScreenActivity.this,"back_press",true);

                            JaribhaPrefrence.deleteKey(LoginScreenActivity.this, "MyObject");
                            JaribhaPrefrence.deleteKey(LoginScreenActivity.this, "comment_push");
                            JaribhaPrefrence.deleteKey(LoginScreenActivity.this, "update_push");

                            Intent intent = new Intent(LoginScreenActivity.this, ProjectDetailsTabs.class);
                            intent.putExtra("detail_object",getProjects);
                            intent.putExtra("comment_push",comment);
                            intent.putExtra("update_push",update_push);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            finish();


                        }else {
                            Intent intent = new Intent(LoginScreenActivity.this, HomeScreenActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                            finish();
                        }
                    } else {
                        showCustomeDialog(R.drawable.icon_error, getString(R.string.error), getString(R.string.invalid_email_or_password), getString(R.string.try_again), R.drawable.btn_bg_green);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showServerDialogDialog();
                }
            } else {
                showServerDialogDialog();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        edt_email.setError(null);
        edt_password.setError(null);
        // Store values at the time of the login attempt.
        String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isNullOrEmpty(email)) {
            edt_email.setError(getString(R.string.field_required));
            focusView = edt_email;
            cancel = true;
        } else if (!Utils.isValidEmailAddress(email)) {
            edt_email.setError(getString(R.string.invalid_email));
            focusView = edt_email;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(password)) {
            edt_password.setError(getString(R.string.field_required));
            focusView = edt_password;
            cancel = true;
        } else if (!Utils.isValidPassword(password)) {
            edt_password.setError(getString(R.string.invalid_password));
            focusView = edt_password;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            try {
                JSONObject loginJsonObject = new JSONObject();
                loginJsonObject.put("apikey", Urls.API_KEY);
                loginJsonObject.put("email", email);
                loginJsonObject.put("password", password);
                loginJsonObject.put("device_type", Constants.DEVICE_TYPE);
                loginJsonObject.put("device_id", JaribhaPrefrence.getPref(LoginScreenActivity.this, Constants.DEVICE_ID, ""));

                mAuthTask = new UserLoginTask(loginJsonObject);
                mAuthTask.execute();
                JaribhaPrefrence.setPref(LoginScreenActivity.this, Constants.PASSWORD_CHECK, password);

                if (isRemember) {
                    JaribhaPrefrence.setPref(LoginScreenActivity.this, Constants.USER_NAME, email);
                    JaribhaPrefrence.setPref(LoginScreenActivity.this, Constants.PASSWORD, password);
                } else {
                    JaribhaPrefrence.deleteKey(this, Constants.USER_NAME);
                    JaribhaPrefrence.deleteKey(this, Constants.PASSWORD);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hideLoading();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (requestCode == 2015 && resultCode == RESULT_OK) { // for Twitter
            //getTwitterCallback(data);
            String google_data = data.getStringExtra("google_data");
            Log.d(TAG, google_data);
            System.out.println("-----google_data---" + google_data);
            if (google_data != null) {
                try {
                    JSONObject jsonObject = new JSONObject(google_data);
                    String google_id = jsonObject.optString("google_id");
                    String google_user_name = jsonObject.optString("google_user_name");
                    String google_profile_pic = jsonObject.optString("google_profile_pic");

                    System.out.println("-----google_id--" + google_id);

                    if (google_id != null) {
                        social_id = google_id;
                    } else {
                        social_id = "";
                    }
                    if (google_user_name != null) {
                        social_user_name = google_user_name;
                    } else {
                        social_user_name = "";
                    }
                    if (google_profile_pic != null) {
                        social_profile_pic = google_profile_pic;
                    } else {
                        social_profile_pic = "";
                    }

                    social_email = "";
                    social_type = SOCIAL_TYPE_GOOGLEPLUS;

                    if (isInternetConnected()) {
                        isSocialExistCall();
                    } else {
                        showNetworkDialog();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else {
            // for Facebook
            callbackManager.onActivityResult(requestCode, resultCode, data);
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
            }
            // for LinkedIn
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);

            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //////////////////////Facebook
    public void OnFacebookClick() {
        // showLoading();
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //hideLoading();
                Profile.fetchProfileForCurrentAccessToken();
                makeMeRequest();
            }

            @Override
            public void onCancel() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                showAlertDialog(getString(R.string.login_cancel));
            }

            @Override
            public void onError(FacebookException e) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                showAlertDialog(getString(R.string.login_error));
            }
        });
    }

    private void makeMeRequest() {

        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        //  hideLoading();
                        if (jsonObject != null) {
                            Log.d(TAG, "userProfile" + graphResponse.toString());
                            System.out.println("-----Fb DATA--" + jsonObject);

                            if(Profile.getCurrentProfile() == null) {
                                mProfileTracker = new ProfileTracker() {
                                    @Override
                                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                                        Log.v("facebook - profile", profile2.getFirstName());
                                        mProfileTracker.stopTracking();
                                    }
                                };
                                mProfileTracker.startTracking();
                                mAccestokentracker.startTracking();
                            } else {
                                Profile profile = Profile.getCurrentProfile();
//                                            FBusername = profile.getFirstName();
                                Fbretid = profile.getId();
                                FBretEmail = jsonObject.optString("email");
                                FBretFNAME = jsonObject.optString("first_name");
                                FBretLNAME = jsonObject.optString("last_name");
                                FBgender = jsonObject.optString("gender");
                                Log.v("facebook - profile", profile.getFirstName());
                                Log.v("facebook -  FBretEmail", FBretEmail);
                                Log.v("facebook - FBretFNAME", FBretFNAME);
                                Log.v("facebook - FBretLNAME", FBretLNAME);
                                Log.v("facebook - FBgender", FBgender);

//           Log.v("facebook -  email", email);
                                Log.v("facebook - id", Fbretid);
                            }

                            try {
                                String fb_id = jsonObject.optString("id");
                                String fb_name = jsonObject.optString("name");
                                String fb_email = jsonObject.optString("email");
                                Log.d("fb_email",fb_id+"--"+fb_name+"--"+fb_email);

                                JSONObject jObject = jsonObject.getJSONObject("picture");
                                JSONObject jObject2 = jObject.getJSONObject("data");

                                String fb_profilr_pic = jObject2.optString("url");

                                if (fb_id != null) {
                                    social_id = fb_id;
                                } else {
                                    social_id = "";
                                }
                                if (fb_email != null) {
                                    social_email = fb_email;
                                } else {
                                    social_email = "";
                                }
                                if (fb_name != null) {
                                    social_user_name = fb_name;
                                } else {
                                    social_user_name = "";
                                }
                                if (fb_profilr_pic != null) {
                                    social_profile_pic = fb_profilr_pic;
                                } else {
                                    social_profile_pic = "";
                                }
                                social_type = SOCIAL_TYPE_FACEBOOK;


                                if (isInternetConnected()) {
                                    isSocialExistCall();
                                } else {
                                    showNetworkDialog();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                showServerDialogDialog();
                            }

                        } else if (graphResponse.getError() != null) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            switch (graphResponse.getError().getCategory()) {
                                case LOGIN_RECOVERABLE:
                                    Log.d(TAG, "Authentication error: " + graphResponse.getError());
                                    showAlertDialog(graphResponse.getError().toString());
                                    break;

                                case TRANSIENT:
                                    Log.d(TAG, "Transient error. Try again. " + graphResponse.getError());
                                    showAlertDialog(graphResponse.getError().toString());
                                    break;

                                case OTHER:
                                    Log.d(TAG, "Some other error: " + graphResponse.getError());
                                    showAlertDialog(graphResponse.getError().toString());
                                    break;
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,gender,name,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    ///////////// Twitter
    public void onClickTwitter() {
        mTwitterAuthClient.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                hideLoading();
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                Twitter.getApiClient(session).getAccountService()
                        .verifyCredentials(true, false, new Callback<com.twitter.sdk.android.core.models.User>() {

                            @Override
                            public void failure(TwitterException e) {

                                hideLoading();
                            }

                            @Override
                            public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                                com.twitter.sdk.android.core.models.User user = userResult.data;
                                System.out.println("-----Twitter user DATA---" + user.id);
                                System.out.println("-----Twitter user DATA---" + user.name);
                                System.out.println("-----Twitter user DATA---" + user.email);
                                System.out.println("-----Twitter user DATA---" + user.profileImageUrl);

                                try {
                                    String tw_id = "" + user.id;
                                    String tw_name = user.name;
                                    String tw_email = user.email;
                                    String tw_pictureUrl = user.profileImageUrl;

                                    if (tw_id != null) {
                                        social_id = tw_id;
                                    } else {
                                        social_id = "";
                                    }
                                    if (tw_id != null) {
                                        social_user_name = tw_name;
                                    } else {
                                        social_user_name = "";
                                    }
                                    if (tw_id != null) {
                                        social_email = tw_email;
                                    } else {
                                        social_email = "";
                                    }
                                    if (tw_id != null) {
                                        social_profile_pic = tw_pictureUrl;
                                    } else {
                                        social_profile_pic = "";
                                    }

                                    social_type = SOCIAL_TYPE_TWITTER;

                                    if (isInternetConnected()) {
                                        isSocialExistCall();
                                    } else {
                                        showNetworkDialog();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    showServerDialogDialog();
                                }
                            }
                        });
            }

            @Override
            public void failure(TwitterException e) {
                hideLoading();
                showServerDialogDialog();
                e.printStackTrace();
            }
        });
        //   showLoading();
    }

    /////////// LinkedIn
    private void linkedInLogin() {

        //For LinkedIn login and get all information
        LISessionManager.getInstance(getApplicationContext()).init(LoginScreenActivity.this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,pictureUrl,email-address)";

                APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
                apiHelper.getRequest(LoginScreenActivity.this, url, new ApiListener() {
                    @Override
                    public void onApiSuccess(ApiResponse apiResponse) {
                        // Success!
                        try {
                            Log.d(TAG, apiResponse.getResponseDataAsString());
                            JSONObject jsonObject = apiResponse.getResponseDataAsJson();
                            System.out.println("-----LinkedIn DATA--" + jsonObject);

                            if (jsonObject != null) {
                                String ln_id = jsonObject.optString("id");
                                String ln_fName = jsonObject.optString("firstName");
                                String ln_lName = jsonObject.optString("lastName");
                                String ln_pictureUrl = jsonObject.optString("pictureUrl");
                                String emailId = jsonObject.optString("emailAddress");

                                if (ln_id != null) {
                                    social_id = ln_id;
                                } else {
                                    social_id = "";
                                }
                                if (social_profile_pic != null) {
                                    social_profile_pic = ln_pictureUrl;
                                } else {
                                    social_profile_pic = "";
                                }
                                social_type = SOCIAL_TYPE_LINKEDIN;

                                social_user_name = ln_fName + " " + ln_lName;

                                if (!TextUtils.isNullOrEmpty(emailId))
                                    social_email = emailId;
                                else {
                                    social_email = "";
                                }

                                if (isInternetConnected()) {
                                    isSocialExistCall();
                                } else {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    showNetworkDialog();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            showServerDialogDialog();
                        }
                    }

                    @Override
                    public void onApiError(LIApiError liApiError) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        showServerDialogDialog();
                    }
                });
            }

            @Override
            public void onAuthError(LIAuthError error) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                // Handle authentication errors
               // showAlertDialog("failed " + error.);
                Log.d(getClass().getSimpleName(), error.toString());
            }
        }, true);
    }

    ///////////COMMON Task for All Social
    private void isSocialExistCall() {
        // showLoading();
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("device_id", JaribhaPrefrence.getPref(LoginScreenActivity.this, Constants.DEVICE_ID, ""));
            jsonObject.put("device_type", Constants.DEVICE_TYPE);
            jsonObject.put("social_id", social_id);
            jsonObject.put("social_type", social_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new IsSocialExistTask(jsonObject).execute();
    }

    public class IsSocialExistTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject jsonObject;


        IsSocialExistTask(JSONObject params) {
            this.jsonObject = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.IS_SOCIAL_EXIST, jsonObject);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        JaribhaPrefrence.setPref(LoginScreenActivity.this, Constants.SOCIAL_TYPE, social_type);
                        UserData user = new Gson().fromJson(jsonObject.getJSONObject("data").toString(), UserData.class);
                        SessionManager.getInstance(getActivity()).saveUser(user);
                        Log.d("User Name : ", getUser().name);
                        JaribhaPrefrence.setPref(LoginScreenActivity.this, Constants.isSocial, true);
                        showToast(getString(R.string.successfully_login));
                        Utils.setDefaultValues(LoginScreenActivity.this);
                        Intent intent = new Intent(LoginScreenActivity.this, HomeScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        finish();
                    } else {

                        //showAlertDialog(jsonObject.getString("msg"));// user does not exist
                        if (social_email == null || social_email.equals("") || social_email.trim().length() == 0) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            openDialogFillRemainingEntriesSocial();
                        } else
                            attemptRegistration();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showServerDialogDialog();
                }
            } else {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                showServerDialogDialog();
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void attemptRegistration() {
        // showLoading();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("email", social_email);
            jsonObject.put("password", "");
            jsonObject.put("name", social_user_name);
            jsonObject.put("device_id", JaribhaPrefrence.getPref(LoginScreenActivity.this, Constants.DEVICE_ID, ""));
            jsonObject.put("device_type", Constants.DEVICE_TYPE);
            jsonObject.put("signup", social_type);
            jsonObject.put("social_id", social_id);
            jsonObject.put("signup_data", "{}");
            jsonObject.put("language", "eng");
            jsonObject.put("newsletter", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocialAuthTask = new UserSocialRegistrationTask(jsonObject);
        mSocialAuthTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserSocialRegistrationTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        UserSocialRegistrationTask(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.SIGN_UP, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mSocialAuthTask = null;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        JaribhaPrefrence.setPref(LoginScreenActivity.this, Constants.SOCIAL_TYPE, social_type);
                        UserData user = new Gson().fromJson(jsonObject.getJSONObject("data").getJSONObject("User").toString(), UserData.class);
                        SessionManager.getInstance(getActivity()).saveUser(user);
                        Log.d("User Name : ", getUser().name);
                        showToast(getString(R.string.successfully_login));
                        Utils.setDefaultValues(LoginScreenActivity.this);

                        Intent intent = new Intent(LoginScreenActivity.this, HomeScreenActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        finish();

                    } else {
                        openDialogFillRemainingEntriesSocial();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showServerDialogDialog();
                }
            } else {
                showServerDialogDialog();
            }
        }

        @Override
        protected void onCancelled() {
            mSocialAuthTask = null;
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }


    }

    public void openDialogFillRemainingEntriesSocial() {
        final Dialog dialog = new Dialog(LoginScreenActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_enter_required_info);
        dialog.setCancelable(false);

        TextView txt_msg = (TextView) dialog.findViewById(R.id.tv_dialog_subtitle);

        Button btn_continue = (Button) dialog.findViewById(R.id.btn_continue);

        LinearLayout dialog_main_container = (LinearLayout) dialog.findViewById(R.id.dialog_main_container);

        String message = getString(R.string.please_check_fb_settings);

        if (social_type.equals(SOCIAL_TYPE_FACEBOOK)) {
            message = getString(R.string.please_check_fb_settings);
        } else if (social_type.equals(SOCIAL_TYPE_GOOGLEPLUS)) {
            message = getString(R.string.please_check_google_settings);
        } else if (social_type.equals(SOCIAL_TYPE_LINKEDIN)) {
            message = getString(R.string.please_check_linkedin_settings);
        }

        txt_msg.setText(message);

        if (isTablet()) {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(this) * 40) / 100;
        }

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //hideLoading();
                if (social_type.equals(SOCIAL_TYPE_FACEBOOK)) {
                    // hideLoading();
                    LoginManager.getInstance().logOut();
                } else if (social_type.equals(SOCIAL_TYPE_GOOGLEPLUS)) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                            new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull com.google.android.gms.common.api.Status status) {
                                    // hideLoading();
                                    //clearData();
                                }
                            });
                } else if (social_type.equals(SOCIAL_TYPE_LINKEDIN)) {

                }

                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void loadMainData() {
        setContentView(R.layout.activity_login_screen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        progressDialog = new ProgressDialog(this, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        Methods.initThreadPolicy();
        // For FACEBOOK
        callbackManager = CallbackManager.Factory.create();

        mTwitterAuthClient = new TwitterAuthClient();

        vImage = (ImageView) findViewById(R.id.vImage);
        vImage.setOnClickListener(this);

        langIcon = (ImageView) findViewById(R.id.eImage);
        langIcon.setOnClickListener(this);

        iv_facebook = (ImageView) findViewById(R.id.iv_facebook);
        iv_facebook.setOnClickListener(this);

        iv_linked_in = (ImageView) findViewById(R.id.iv_linked_in);
        iv_linked_in.setOnClickListener(this);

        iv_google = (ImageView) findViewById(R.id.iv_google);
        iv_google.setOnClickListener(this);

        forgetPwd = (TextView) findViewById(R.id.forgetPwd);
        signupBtn = (TextView) findViewById(R.id.signupBtn);
        orText = (TextView) findViewById(R.id.orText);
        rememberCheck = (CheckBox) findViewById(R.id.rememberMe);
        signWith = (TextView) findViewById(R.id.signWith);

        forgetPwd.setOnClickListener(this);
        forgetPwd.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        tv_explore_as_guest = (TextView) findViewById(R.id.tv_explore_as_guest);
        tv_explore_as_guest.setOnClickListener(this);

        edt_email = (AutoCompleteTextView) findViewById(R.id.edt_email);
        signupBtn.setOnClickListener(this);
        //populateAutoComplete();

        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        rememberCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRemember = isChecked;
            }
        });


        if (!TextUtils.isNullOrEmpty(JaribhaPrefrence.getPref(this, Constants.USER_NAME, "")) || !TextUtils.isNullOrEmpty(JaribhaPrefrence.getPref(this, Constants.PASSWORD, ""))) {
            rememberCheck.setChecked(true);
        }

        edt_email.setTextColor(Color.WHITE);
        edt_password.setTextColor(Color.WHITE);
        //edt_email.setTe

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

        tv_dont_have_account = (TextView) findViewById(R.id.tv_dont_have_account);
        tv_dont_have_account.setOnClickListener(this);

        styledString = new SpannableString(getString(R.string.dont_have_account_new));

        //length = getString(R.string.dont_have_account).length();
        // change text color
        //styledString.setSpan(new ForegroundColorSpan(Color.parseColor("#E7877C")), length - 9, length, 0);

        tv_dont_have_account.setText(styledString);
        signupBtn.setText(getString(R.string.sign_up_new));

        edt_email.setHint(getString(R.string.email));
       // if (JaribhaPrefrence.getPref(this, Constants.isRemember, false)) {
        edt_password.setText(JaribhaPrefrence.getPref(this, Constants.PASSWORD, ""));
        edt_email.setText(JaribhaPrefrence.getPref(this, Constants.USER_NAME, ""));
        //}

        edt_password.setHint(getString(R.string.password));
        forgetPwd.setText(getString(R.string.forgot_password));
        tv_explore_as_guest.setText(getString(R.string.explore_as_guest));
        orText.setText(getString(R.string.or));
        signWith.setText(getString(R.string.sign_in_with));
        loginBtn.setText(getString(R.string.login_title));
        rememberCheck.setText(getString(R.string.remember_me));
        tv_explore_as_guest.setPaintFlags(tv_explore_as_guest.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        if (!isArabic()) {
            isArabic = false;
            langIcon.setImageResource(R.drawable.icon_arabic);
        } else {
            isArabic = true;
            langIcon.setImageResource(R.drawable.english);
        }
    }
}

