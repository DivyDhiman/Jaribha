package com.jaribha.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.CreateProjectActivity;
import com.jaribha.adapters.AddRewardsAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.ExpandableHeightListView;
import com.jaribha.models.Rewards;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CreateProjectRewardsFragment extends BaseFragment implements View.OnClickListener {

    Button btn_rewards_next, btn_save_add_new;

    RadioGroup rg_rewards_limit;

    private TextView tv_point1, tv_point1_desc;

    EditText edt_supporter_amount, edt_rewards_desc, edt_year, edt_month, edt_shipping_details, edt_limit;

    ExpandableHeightListView list_rewards;

    private ListPopupWindow lpwYear, lpwMonth, lpwShipping;

    String[] month;

    List<String> stringsMonth;

    String[] stringsShipping;

    ArrayList<String> yearList = new ArrayList<>();

    String limitType = "limit";

    int indexMonth;

    AddRewardsAdapter rewardsAdapter;

    private ProgressDialog progressDialog;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @SuppressLint("SimpleDateFormat")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project_reward, null);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        rewardsAdapter = new AddRewardsAdapter(activity, rewardsList);

        list_rewards = (ExpandableHeightListView) view.findViewById(R.id.list_rewards);
        list_rewards.setExpanded(true);
        list_rewards.setAdapter(rewardsAdapter);

        month = getResources().getStringArray(R.array.months);

        stringsMonth = new ArrayList<>(Arrays.asList(month));

        stringsShipping = getResources().getStringArray(R.array.shipping);

        btn_rewards_next = (Button) view.findViewById(R.id.btn_rewards_next);
        btn_rewards_next.setOnClickListener(this);

        if (isTabletDevice()) {
            tv_point1 = (TextView) view.findViewById(R.id.tv_point1);
            tv_point1_desc = (TextView) view.findViewById(R.id.tv_point1_desc);
            tv_point1.setOnClickListener(this);
        }

        btn_save_add_new = (Button) view.findViewById(R.id.btn_save_add_new);
        btn_save_add_new.setOnClickListener(this);

        edt_supporter_amount = (EditText) view.findViewById(R.id.edt_supporter_amount);
        edt_supporter_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable mEdit) {
                if (mEdit.toString().length() > 0 && mEdit.toString().charAt(0) == '0') {
                    showToast(getString(R.string.first_letter_not_zero));
                    edt_supporter_amount.setText("");
                }
            }
        });

        edt_rewards_desc = (EditText) view.findViewById(R.id.edt_rewards_desc);

        edt_year = (EditText) view.findViewById(R.id.edt_year);
        edt_year.setOnClickListener(this);

        edt_month = (EditText) view.findViewById(R.id.edt_month);
        edt_month.setOnClickListener(this);

        edt_shipping_details = (EditText) view.findViewById(R.id.edt_shipping_details);
        edt_shipping_details.setOnClickListener(this);

        edt_limit = (EditText) view.findViewById(R.id.edt_limit);
        edt_limit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable mEdit) {
                if (mEdit.toString().length() > 0 && mEdit.toString().charAt(0) == '0') {
                    showToast(getString(R.string.first_letter_not_zero));
                    edt_limit.setText("");
                }
            }
        });

        rg_rewards_limit = (RadioGroup) view.findViewById(R.id.rg_rewards_limit);
        rg_rewards_limit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_limit) {
                    edt_limit.setVisibility(View.VISIBLE);
                    limitType = "limit";
                } else {
                    edt_limit.setVisibility(View.GONE);
                    limitType = "unlimited";
                }
            }
        });

        for (int i = 0; i < 11; i++) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, i);
            Date daysBeforeDate = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            String date = sdf.format(daysBeforeDate);
            Log.d(getClass().getName(), date);
            yearList.add(date);
        }

        lpwMonth = new ListPopupWindow(activity);
        lpwMonth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = stringsMonth.get(position);
                edt_month.setText(item);
                indexMonth = stringsMonth.indexOf(item);
                lpwMonth.dismiss();
            }
        });

        lpwYear = new ListPopupWindow(activity);
        lpwYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = yearList.get(position);
                edt_year.setText(item);
                lpwYear.dismiss();
            }
        });

        lpwShipping = new ListPopupWindow(activity);
        lpwShipping.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = stringsShipping[position];
                edt_shipping_details.setText(item);
                lpwShipping.dismiss();
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.edt_month:
                lpwMonth.setAdapter(new ArrayAdapter<>(activity,
                        android.R.layout.simple_list_item_1, stringsMonth));
                lpwMonth.setAnchorView(edt_month);
                lpwMonth.setModal(true);
                lpwMonth.show();
                break;

            case R.id.edt_shipping_details:
                lpwShipping.setAdapter(new ArrayAdapter<>(activity,
                        android.R.layout.simple_list_item_1, stringsShipping));
                lpwShipping.setAnchorView(edt_shipping_details);
                lpwShipping.setModal(true);
                lpwShipping.show();
                break;

            case R.id.edt_year:
                lpwYear.setAdapter(new ArrayAdapter<>(activity,
                        android.R.layout.simple_list_item_1, yearList));
                lpwYear.setAnchorView(edt_year);
                lpwYear.setModal(true);
                lpwYear.show();
                break;

            case R.id.btn_rewards_next:
                nextClick();
                break;

            case R.id.tv_point1:
                if (tv_point1_desc.getVisibility() == View.VISIBLE) {
                    tv_point1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);
                    tv_point1_desc.setVisibility(View.GONE);
                } else {
                    tv_point1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_remove_circle, 0, 0, 0);
                    tv_point1_desc.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.btn_save_add_new:
                if (isInternetConnected()) {
                    attemptToAddRewards();
                } else {
                    showNetworkDialog();
                }
                break;

            default:
                break;
        }
    }

    private void nextClick() {
        edt_supporter_amount.setError(null);
        edt_rewards_desc.setError(null);
        edt_year.setError(null);
        edt_month.setError(null);
        edt_shipping_details.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isNullOrEmpty(rewardsList)) {
            if (TextUtils.isNullOrEmpty(edt_supporter_amount.getText().toString())) {
                edt_supporter_amount.setError(getString(R.string.error_field_required));
                focusView = edt_supporter_amount;
                cancel = true;
            } else if (TextUtils.isNullOrEmpty(edt_rewards_desc.getText().toString())) {
                edt_rewards_desc.setError(getString(R.string.error_field_required));
                focusView = edt_rewards_desc;
                cancel = true;
            } else if (TextUtils.isNullOrEmpty(edt_year.getText().toString())) {
                edt_year.setError(getString(R.string.error_field_required));
                focusView = edt_year;
                cancel = true;
            } else if (TextUtils.isNullOrEmpty(edt_month.getText().toString())) {
                edt_month.setError(getString(R.string.error_field_required));
                focusView = edt_month;
                cancel = true;
            } else if (TextUtils.isNullOrEmpty(edt_shipping_details.getText().toString())) {
                edt_shipping_details.setError(getString(R.string.error_field_required));
                focusView = edt_shipping_details;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            if (isInternetConnected()) {
                if (!isTabletDevice())
                    ((CreateProjectActivity) activity).displayTab(6);
                else {
                    ((CreateProjectActivity) activity).displayFragment(6);
                }
            } else {
                showNetworkDialog();
            }
        }
    }

    private void attemptToAddRewards() {
        edt_supporter_amount.setError(null);
        edt_rewards_desc.setError(null);
        edt_year.setError(null);
        edt_month.setError(null);
        edt_shipping_details.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isNullOrEmpty(edt_supporter_amount.getText().toString().trim())) {
            edt_supporter_amount.setError(getString(R.string.error_field_required));
            focusView = edt_supporter_amount;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_rewards_desc.getText().toString().trim())) {
            edt_rewards_desc.setError(getString(R.string.error_field_required));
            focusView = edt_rewards_desc;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_year.getText().toString().trim())) {
            edt_year.setError(getString(R.string.error_field_required));
            focusView = edt_year;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_month.getText().toString().trim())) {
            edt_month.setError(getString(R.string.error_field_required));
            focusView = edt_month;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_shipping_details.getText().toString().trim())) {
            edt_shipping_details.setError(getString(R.string.error_field_required));
            focusView = edt_shipping_details;
            cancel = true;
        }

//        if (TextUtils.isNullOrEmpty(edt_limit.getText().toString())) {
//            edt_limit.setError(getString(R.string.error_field_required));
//            focusView = edt_limit;
//            cancel = true;
//        }

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
                    jsonObject.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));
                    jsonObject.put("amount", edt_supporter_amount.getText().toString());
                    jsonObject.put("description", edt_rewards_desc.getText().toString());
                    jsonObject.put("year", edt_year.getText().toString());
                    jsonObject.put("month", String.valueOf(indexMonth + 1));
                    jsonObject.put("shipping", edt_shipping_details.getText().toString());
                    jsonObject.put("limit_type", limitType);
                    jsonObject.put("limit_no", edt_limit.getText().toString());
                    jsonObject.put("is_store", "false");
                    addRewards(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showNetworkDialog();
            }
        }
    }

    ArrayList<Rewards> rewardsList = new ArrayList<>();

    public void addRewards(final JSONObject jsonObject) {
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
                return new JSONParser().getJsonObjectFromUrl1(Urls.ADD_REWARDS, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject response) {
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (response != null) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("msg");
                        if (status) {
                            resetView();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i).getJSONObject("Reward");
                                Rewards rewards = new Gson().fromJson(object.toString(), Rewards.class);
                                rewardsList.add(rewards);
                            }
                            rewardsAdapter.notifyDataSetChanged();
                        } else {
                            if (message.equalsIgnoreCase("User Not Found")) {
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

            }
        }.execute();
    }

    private void resetView() {
        edt_supporter_amount.setText("");
        edt_rewards_desc.setText("");
        edt_year.setText("");
        edt_month.setText("");
        edt_shipping_details.setText("");
        edt_shipping_details.setText("");
    }
}
