package com.jaribha.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class RegisterScreenActivity extends BaseAppCompatActivity implements View.OnClickListener, TextWatcher {

    ImageView iv_close;

    BezelImageView img_user_image;

    TextView tv_title;

    Button registerBtn;

    private EditText edt_full_name, edt_reg_email, edt_password, edt_confirm_password, edt_reg_confirm_email;

    CheckBox newsLetterCheckBox, termsCheckBox;

    Intent intent;

    TextView terms, privacy;

    //private CheckBox newsLetterCheckBox, termsCheckBox;
    /**
     * Keep track of the Registration task to ensure we can cancel it if requested.
     */
    private UserRegistrationTask mAuthTask = null;

    public boolean isCheckedTerms = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.GONE);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.sign_up));

        registerBtn = (Button) findViewById(R.id.registerationBtn);
        registerBtn.setOnClickListener(this);

        terms = (TextView) findViewById(R.id.terms);
        //terms.setTextColor(Color.WHITE);
        terms.setPaintFlags(terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        terms.setOnClickListener(this);

        privacy = (TextView) findViewById(R.id.privacy);
        privacy.setPaintFlags(privacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //privacy.setTextColor(Color.WHITE);
        privacy.setOnClickListener(this);

        terms.setVisibility(View.GONE);
        privacy.setVisibility(View.GONE);

        edt_reg_email = (EditText) findViewById(R.id.edt_reg_email);
        edt_reg_email.setTextColor(Color.WHITE);
        edt_reg_email.addTextChangedListener(this);

        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_password.setTextColor(Color.WHITE);
        edt_password.addTextChangedListener(this);

        edt_confirm_password = (EditText) findViewById(R.id.edt_confirm_password);
        edt_confirm_password.setTextColor(Color.WHITE);
        edt_confirm_password.addTextChangedListener(this);

        edt_full_name = (EditText) findViewById(R.id.edt_full_name);
        edt_full_name.setTextColor(Color.WHITE);
        edt_full_name.addTextChangedListener(this);

        edt_reg_confirm_email = (EditText) findViewById(R.id.edt_reg_confirm_email);
        edt_reg_confirm_email.setTextColor(Color.WHITE);
        edt_reg_confirm_email.addTextChangedListener(this);

        newsLetterCheckBox = (CheckBox) findViewById(R.id.newsLetterCheckBox);
        newsLetterCheckBox.setText(getString(R.string.newsletter));
        termsCheckBox = (CheckBox) findViewById(R.id.termsCheckBox);

        //String styledText = "I agree to Jaribha&apos;s <font color='#ffffff'><ul>Terms and conditions</ul></font> and <font color='#ffffff'><ul>Privacy Policy.</ul></font>";
        String styledText = getString(R.string.i_agree_to_Jaribha) + "<font color='#000000'><ul>" + getString(R.string.terms_and_conditions) + "</ul></font> " + getString(R.string.and) + " <font color='#000000'><ul>" + getString(R.string.privacy_policy) + "</ul></font>";

        termsCheckBox.setText(Html.fromHtml(styledText));

        Pattern pattern1 = Pattern.compile(getString(R.string.terms_and_conditions));
        Linkify.addLinks(termsCheckBox, pattern1, "terms-activity://");
        termsCheckBox.setLinkTextColor(Color.BLACK);

        Pattern pattern2 = Pattern.compile(getString(R.string.privacy_policy));
        Linkify.addLinks(termsCheckBox, pattern2, "privacy-activity://");

        termsCheckBox.setLinkTextColor(Color.BLACK);

        termsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheckedTerms = isChecked;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.registerationBtn:
                if (isInternetConnected()) {
                    attemptRegistration();
                } else {
                    showNetworkDialog();
                }
                break;
            case R.id.iv_close:
                //intent = new Intent(RegisterScreenActivity.this, LoginScreenActivity.class);
                //startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                finish();
                break;
            case R.id.terms:
                intent = new Intent(RegisterScreenActivity.this, TermsActivity.class);
                startActivity(intent);
                break;
            case R.id.privacy:
                intent = new Intent(RegisterScreenActivity.this, PrivacyActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void attemptRegistration() {
        if (mAuthTask != null) {
            return;
        }

        edt_full_name.setError(null);
        edt_reg_email.setError(null);
        edt_password.setError(null);
        edt_confirm_password.setError(null);

        String fullName = edt_full_name.getText().toString().trim();
        String email = edt_reg_email.getText().toString().trim();
        String confirm_email = edt_reg_confirm_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();
        String confirm_pass = edt_confirm_password.getText().toString().trim();

        String newsLetter = newsLetterCheckBox.isChecked() ? "1" : "0";

        // Check for full name, if the user entered one.
        if (TextUtils.isNullOrEmpty(fullName)) {
            edt_full_name.setError(getString(R.string.error_field_required));
            edt_full_name.requestFocus(fullName.length());
        } else if (TextUtils.isNullOrEmpty(email)) {
            edt_reg_email.setError(getString(R.string.error_field_required));
            edt_reg_email.requestFocus(email.length());
        } else if (!Utils.isValidEmailAddress(email)) {
            edt_reg_email.setError(getString(R.string.error_invalid_email));
            edt_reg_email.requestFocus(email.length());
        } else if (TextUtils.isNullOrEmpty(confirm_email)) {
            edt_reg_confirm_email.setError(getString(R.string.error_field_required));
            edt_reg_confirm_email.requestFocus(confirm_email.length());
        } else if (!email.equals(confirm_email)) {
            Utils.showDataToast(getString(R.string.email_does_not_match), RegisterScreenActivity.this);
        } else if (TextUtils.isNullOrEmpty(password)) {
            edt_password.setError(getString(R.string.error_field_required));
            edt_password.requestFocus(password.length());
        } else if (!Utils.isValidPassword(password)) {
            edt_password.setError(getString(R.string.error_invalid_password));
            edt_password.requestFocus(password.length());
        } else if (TextUtils.isNullOrEmpty(confirm_pass)) {
            edt_confirm_password.setError(getString(R.string.error_field_required));
            edt_confirm_password.requestFocus(confirm_pass.length());
        } else if (!password.equals(confirm_pass)) {
            Utils.showDataToast(getString(R.string.password_does_not_match), RegisterScreenActivity.this);
        } else if (!isCheckedTerms) {
            Utils.showDataToast(getString(R.string.please_accept_terms_and_condition_privacy_policy), this);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showLoading();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("apikey", Urls.API_KEY);
                jsonObject.put("email", email);
                jsonObject.put("password", password);
                jsonObject.put("name", fullName);
                jsonObject.put("device_id", JaribhaPrefrence.getPref(RegisterScreenActivity.this, Constants.DEVICE_ID, ""));
                jsonObject.put("device_type", Constants.DEVICE_TYPE);
                jsonObject.put("signup", "app");
                jsonObject.put("social_id", "");
                jsonObject.put("signup_data", "{}");
                jsonObject.put("language", "eng");
                jsonObject.put("newsletter", newsLetter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mAuthTask = new UserRegistrationTask(jsonObject);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegistrationTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        UserRegistrationTask(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.SIGN_UP, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mAuthTask = null;
            hideLoading();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.getString("msg");
                    if (status) {
                        edt_full_name.setText("");
                        edt_confirm_password.setText("");
                        edt_password.setText("");
                        edt_reg_confirm_email.setText("");
                        edt_reg_email.setText("");
                        // Utils.showDataToast(getString(R.string.register_success),RegisterScreenActivity.this);
                        //UserData user = new Gson().fromJson(jsonObject.getJSONObject("data").getJSONObject("User").toString(), UserData.class);
                        //SessionManager.getInstance(getActivity()).saveUser(user);
                        //Log.d("User Name : ", getUser().name);
                        //Intent intent = new Intent(RegisterScreenActivity.this, LoginScreenActivity.class);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        //startActivity(intent);
                        //overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                        //finish();
                        showCustomeDialog(R.drawable.right_icon, getString(R.string.success), getString(R.string.register_success), getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                    } else {
                        showCustomeDialog(R.drawable.icon_error, getString(R.string.error), msg, getString(R.string.try_again), R.drawable.btn_bg_green);

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
            hideLoading();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (edt_full_name.getText().length() > 0) {
            edt_full_name.setError(null);
        }
        if (edt_reg_email.getText().length() > 0) {
            edt_reg_email.setError(null);
        }
        if (edt_password.getText().length() > 0) {
            edt_password.setError(null);
        }
        if (edt_confirm_password.getText().length() > 0) {
            edt_confirm_password.setError(null);
        }
        if (edt_reg_confirm_email.getText().length() > 0) {
            edt_reg_confirm_email.setError(null);
        }
    }
}
