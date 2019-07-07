package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class ContactusFragment extends BaseFragment implements View.OnClickListener {

    private Button submit;

    private EditText edt_fax_number, edt_full_name, edt_email, edt_mobile, edt_message;

    private String fname = "", email = "", mobile = "", fax_number = "", message = "";

    private Activity activity;

    private ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactus_new, container, false);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        progressDialog = new ProgressDialog(activity, R.style.MyTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        submit = (Button) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);

        edt_full_name = (EditText) view.findViewById(R.id.edt_full_name);
        edt_email = (EditText) view.findViewById(R.id.edt_email);
        edt_mobile = (EditText) view.findViewById(R.id.edt_mobile);
        edt_message = (EditText) view.findViewById(R.id.edt_message);

        edt_fax_number = (EditText) view.findViewById(R.id.edt_fax_number);
        String text = activity.getResources().getString(R.string.fax_number);
        String next = "<font color='#C3C3C3'><small>(" + activity.getResources().getString(R.string.optional) + ")</small></font>";
        edt_fax_number.setHint(Html.fromHtml(text +" "+next));

        if (isAdded()) {
            submit.setText(getActivity().getResources().getString(R.string.submit));
            edt_full_name.setHint(getActivity().getResources().getString(R.string.full_name));
            edt_email.setHint(getActivity().getResources().getString(R.string.email_id));
            edt_mobile.setHint(getActivity().getResources().getString(R.string.telephone_mobile));
            edt_message.setHint(getActivity().getResources().getString(R.string.comments_message));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                edt_full_name.setError(null);
                edt_email.setError(null);
                edt_mobile.setError(null);
                edt_fax_number.setError(null);
                edt_message.setError(null);

                boolean cancel = false;
                View focusView = null;

                fname = edt_full_name.getText().toString().trim();
                email = edt_email.getText().toString().trim();
                mobile = edt_mobile.getText().toString().trim();
                fax_number = edt_fax_number.getText().toString().trim();
                message = edt_message.getText().toString().trim();

                if (TextUtils.isNullOrEmpty(edt_email.getText().toString())) {
                    edt_email.setError(getString(R.string.error_field_required));
                    focusView = edt_email;
                    cancel = true;
                } else if (!TextUtils.isValidEmail(edt_email.getText().toString())) {
                    edt_email.setError(getString(R.string.incorrect_email));
                    focusView = edt_email;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_full_name.getText().toString())) {
                    edt_full_name.setError(getString(R.string.error_field_required));
                    focusView = edt_full_name;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_mobile.getText().toString())) {
                    edt_mobile.setError(getString(R.string.error_field_required));
                    focusView = edt_mobile;
                    cancel = true;
                } else if (!TextUtils.isValidMobile(edt_mobile.getText().toString())) {
                    edt_mobile.setError(getString(R.string.incorrect_mobile));
                    focusView = edt_mobile;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_message.getText().toString())) {
                    edt_message.setError(getString(R.string.error_field_required));
                    focusView = edt_message;
                    cancel = true;
                }

                if (cancel) {
                    focusView.requestFocus();
                } else {
                    if (Utils.isInternetConnected(activity)) {
                        submitContactUs();
                    } else {
                        showNetworkDialog();
                    }
                }

                break;

            default:
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    private void submitContactUs() {
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("fullname", fname);
            jsonObject.put("useremail", email);
            jsonObject.put("phone", mobile);
            if (!TextUtils.isNullOrEmpty(fax_number))
                jsonObject.put("faxno", fax_number);
            else {
                jsonObject.put("faxno", "");
            }
            jsonObject.put("message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Utils.isInternetConnected(activity)) {
            new ContactUsTask(jsonObject).execute();
        } else {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            showNetworkDialog();
        }

    }

    public class ContactUsTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject jsonObject;


        ContactUsTask(JSONObject params) {
            this.jsonObject = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.CONTACT_US, jsonObject);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    if (status) {
                        edt_full_name.setText("");
                        edt_email.setText("");
                        edt_mobile.setText("");
                        edt_fax_number.setText("");
                        edt_message.setText("");
                        if (isAdded())
                            showCustomeDialog(R.drawable.message_icon, getString(R.string.thank_you), getString(R.string.for_your_contact_us), getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                    } else {
                        if (jsonObject.getString("msg").equals("Data Not Found")) {
                            showDataNotFoundDialog();
                        } else if (jsonObject.getString("msg").equals("User Not Found")) {
                            showSessionDialog();
                        } else {
                            showServerErrorDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showServerErrorDialog();
                }
            } else {
                showServerErrorDialog();
            }
        }

        @Override
        protected void onCancelled() {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            progressDialog = null;
        }
    }
}
