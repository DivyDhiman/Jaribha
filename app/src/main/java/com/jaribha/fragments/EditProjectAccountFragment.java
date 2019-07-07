package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.EditProjectActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.models.Account;
import com.jaribha.models.GetProjects;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProjectAccountFragment extends BaseFragment implements View.OnClickListener {

    Button btn_save_next_account;

    EditText edt_account_number, edt_swift_number, edt_name_of_bank, edt_routing_number, edt_other_bank;

    JSONObject mObject;

    private ProgressDialog progressDialog;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public static EditProjectAccountFragment newInstance(JSONObject object) {
        Bundle args = new Bundle();
        args.putString(Constants.DATA, object.toString());
        EditProjectAccountFragment fragment = new EditProjectAccountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String mString = getArguments().getString(Constants.DATA);
        try {
            mObject = new JSONObject(mString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project_account, null);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        btn_save_next_account = (Button) view.findViewById(R.id.btn_save_next_account);
        btn_save_next_account.setOnClickListener(this);

        edt_account_number = (EditText) view.findViewById(R.id.edt_account_number);
        edt_swift_number = (EditText) view.findViewById(R.id.edt_swift_number);
        edt_name_of_bank = (EditText) view.findViewById(R.id.edt_name_of_bank);
        edt_routing_number = (EditText) view.findViewById(R.id.edt_routing_number);
        edt_other_bank = (EditText) view.findViewById(R.id.edt_other_bank);

        SetData();

        return view;
    }

    GetProjects projects;

    Account account;

    private void SetData() {
        try {
            account = new Gson().fromJson(mObject.getJSONObject("Account").toString(), Account.class);

            projects = new Gson().fromJson(mObject.optJSONObject("Project").toString(), GetProjects.class);

            if (!TextUtils.isNullOrEmpty(account.account_number))
                edt_account_number.setText(account.account_number);

            if (!TextUtils.isNullOrEmpty(account.swift_number))
                edt_swift_number.setText(account.swift_number);

            if (!TextUtils.isNullOrEmpty(account.bankname))
                edt_name_of_bank.setText(account.bankname);

            if (!TextUtils.isNullOrEmpty(account.iban))
                edt_routing_number.setText(account.iban);

            if (!TextUtils.isNullOrEmpty(account.details))
                edt_other_bank.setText(account.details);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.btn_save_next_account:
                if (isInternetConnected()) {
                    attemptToAddAccount();
                } else {
                    showNetworkDialog();
                }
                break;

            default:
                break;
        }
    }

    private void attemptToAddAccount() {
        edt_account_number.setError(null);
        edt_swift_number.setError(null);
        edt_name_of_bank.setError(null);
        edt_routing_number.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isNullOrEmpty(edt_account_number.getText().toString().trim())) {
            edt_account_number.setError(getString(R.string.error_field_required));
            focusView = edt_account_number;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_swift_number.getText().toString().trim())) {
            edt_swift_number.setError(getString(R.string.error_field_required));
            focusView = edt_swift_number;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_name_of_bank.getText().toString().trim())) {
            edt_name_of_bank.setError(getString(R.string.error_field_required));
            focusView = edt_name_of_bank;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_routing_number.getText().toString().trim())) {
            edt_routing_number.setError(getString(R.string.error_field_required));
            focusView = edt_routing_number;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (isInternetConnected()) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("apikey", Urls.API_KEY);
                    jsonObject.put("user_id", getUser().id);
                    jsonObject.put("user_token", getUser().user_token);
                    jsonObject.put("project_id", projects.id);
                    jsonObject.put("account_number", edt_account_number.getText().toString());
                    jsonObject.put("swift_number", edt_swift_number.getText().toString()); // *
                    jsonObject.put("bankname", edt_name_of_bank.getText().toString()); // *
                    jsonObject.put("iban", edt_routing_number.getText().toString());
                    jsonObject.put("detail", edt_other_bank.getText().toString());
                    if (TextUtils.isNullOrEmpty(account.id)) {
                        jsonObject.put("accountID", "");
                    } else {
                        jsonObject.put("accountID", account.id);
                    }

                    addProjectAccount(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showNetworkDialog();
            }
        }
    }

    public void addProjectAccount(final JSONObject jsonObject) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //showLoading();
                if (progressDialog != null && !progressDialog.isShowing())
                    progressDialog.show();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                return new JSONParser().getJsonObjectFromUrl1(Urls.ADD_ACCOUNT, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject response) {
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("result");
                        boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("msg");
                        if (status) {
                            ((EditProjectActivity) activity).firstLoad = false;
                            if (!isTabletDevice())
                                ((EditProjectActivity) activity).displayTab(5, true);
                            else {
                                ((EditProjectActivity) activity).displayFragment(5, true);
                            }
                        } else {
                            if (message.equalsIgnoreCase("User Not Saved")) {
                                showSessionDialog();
                            } else if (message.equalsIgnoreCase("Data Not Found")) {
                                showDataNotFoundDialog();
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
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }.execute();
    }
}
