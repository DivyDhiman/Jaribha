package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.interfaces.AddFavourite;
import com.jaribha.models.GetProjects;
import com.jaribha.models.Pledge;
import com.jaribha.models.Rewards;
import com.jaribha.models.UserData;
import com.jaribha.server_communication.AddFavouriteTask;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentResponse extends BaseFragment implements View.OnClickListener {

    Rewards mRewards;

    TextView tv_payment_project_title, tv_payment_project_by, tv_payment_project_category, tv_payment_project_location;

    TextView tv_payment_project_supported, tv_payment_project_goal, tv_payment_project_days_left;

    ImageView iv_payment_project_image, iv_payment_project_favourite;

    ProgressBar payment_project_progress;

    GetProjects projects;

    TextView tv_rewards, tv_availability, tv_supported_amount, tv_currency_rate, tv_kd_amount, tv_payment_type, tv_payment_id, tv_tracking_id, tv_date_time;

    private ProgressDialog progressDialog;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public static PaymentResponse newInstance(Rewards rewards) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.DATA, rewards);
        PaymentResponse fragment = new PaymentResponse();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRewards = (Rewards) getArguments().getSerializable(Constants.DATA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.payment_review, container, false);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        iv_payment_project_image = (ImageView) view.findViewById(R.id.iv_payment_project_image);

        iv_payment_project_favourite = (ImageView) view.findViewById(R.id.iv_payment_project_favourite);

        iv_payment_project_favourite.setOnClickListener(this);

        tv_payment_project_title = (TextView) view.findViewById(R.id.tv_payment_project_title);

        tv_payment_project_by = (TextView) view.findViewById(R.id.tv_payment_project_by);

        tv_payment_project_category = (TextView) view.findViewById(R.id.tv_payment_project_category);

        tv_payment_project_location = (TextView) view.findViewById(R.id.tv_payment_project_location);

        tv_payment_project_supported = (TextView) view.findViewById(R.id.tv_payment_project_supported);

        tv_payment_project_goal = (TextView) view.findViewById(R.id.tv_payment_project_goal);

        tv_payment_project_days_left = (TextView) view.findViewById(R.id.tv_payment_project_days_left);

        payment_project_progress = (ProgressBar) view.findViewById(R.id.payment_project_progress);

        tv_rewards = (TextView) view.findViewById(R.id.tv_rewards);

        tv_availability = (TextView) view.findViewById(R.id.tv_availability);

        tv_supported_amount = (TextView) view.findViewById(R.id.tv_supported_amount);

        tv_currency_rate = (TextView) view.findViewById(R.id.tv_currency_rate);

        tv_kd_amount = (TextView) view.findViewById(R.id.tv_kd_amount);

        tv_payment_type = (TextView) view.findViewById(R.id.tv_payment_type);

        tv_payment_id = (TextView) view.findViewById(R.id.tv_payment_id);

        tv_tracking_id = (TextView) view.findViewById(R.id.tv_tracking_id);

        tv_date_time = (TextView) view.findViewById(R.id.tv_date_time);

        String data = JaribhaPrefrence.getPref(activity, Constants.PAYMENT_JSON, "");
        if (!TextUtils.isNullOrEmpty(data)) {
            try {
                JSONObject object = new JSONObject(data);
                Pledge pledge = new Gson().fromJson(object.getJSONObject("data").getJSONObject("Pledge").toString(), Pledge.class);
                tv_rewards.setText(mRewards.description);
                tv_availability.setText(mRewards.limit);
                tv_supported_amount.setText("$" + pledge.support_amount_usd);
                tv_currency_rate.setText(pledge.currency_rate);
                tv_kd_amount.setText("KD" + pledge.support_amount_kd);
                tv_payment_type.setText(pledge.pay_type);
                tv_payment_id.setText(pledge.payment_id);
                tv_tracking_id.setText(pledge.tracking_id);
                tv_date_time.setText(pledge.created);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        loadUpdates();

        return view;
    }

    public void loadUpdates() {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);
            projectJson.put("user_id", getUser().id);
            projectJson.put("user_token", getUser().user_token);
            projectJson.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));

            GetProjectInfo mAuthTask = new GetProjectInfo(projectJson);
            mAuthTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_payment_project_favourite:
                if (isInternetConnected())
                    manageFavourite(projects);
                else
                    showNetworkDialog();
                break;
        }
    }

    private void manageFavourite(final GetProjects object) {
        //showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", ((BaseAppCompatActivity) getContext()).getUser().id);
            jsonObject.put("user_token", ((BaseAppCompatActivity) getContext()).getUser().user_token);
            jsonObject.put("project_id", object.id);
            AddFavouriteTask addFavouriteTask = new AddFavouriteTask(jsonObject, new AddFavourite() {
                @Override
                public void OnSuccess(JSONObject response) {
                    Log.d(getClass().getName(), response.toString());
                    //hideLoading();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = response.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String msg = jsonObject.optString("msg");
                        if (status) {
                            object.is_favourite = msg.equalsIgnoreCase("Favourite Added");
                            //notifyDataSetChanged();
                            if (object.is_favourite) {
                                Utils.updateFavoriteCount(activity, 1);
                                iv_payment_project_favourite.setImageResource(R.drawable.ic_fav_selected);
                            } else {
                                Utils.updateFavoriteCount(activity, -1);
                                iv_payment_project_favourite.setImageResource(R.drawable.fav_button);
                            }
                        } else {
                            switch (msg) {
                                case "Data Not Found":
                                    ((BaseAppCompatActivity) getContext()).showDataNotFoundDialog();
                                    break;
                                case "User Not Found":
                                    ((BaseAppCompatActivity) getContext()).showSessionDialog();
                                    break;
                                default:
                                    ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                                    break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                    }
                }

                @Override
                public void OnFail() {
                    //hideLoading();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                }
            });

            addFavouriteTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class GetProjectInfo extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetProjectInfo(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showLoading();
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECT_DETAIL, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        projects = new Gson().fromJson(jsonObject.optJSONObject("data").optJSONObject("Project").toString(), GetProjects.class);

                        UserData userData = new Gson().fromJson(jsonObject.optJSONObject("data").optJSONObject("User").toString(), UserData.class);

                        displayImage(activity, iv_payment_project_image, projects.image_url, ContextCompat.getDrawable(activity, R.drawable.server_error_placeholder));

                        if (projects.is_favourite) {
                            iv_payment_project_favourite.setImageResource(R.drawable.ic_fav_selected);
                        } else {
                            iv_payment_project_favourite.setImageResource(R.drawable.fav_button);
                        }

                        tv_payment_project_title.setText(projects.title);

                        if (isAdded())
                            tv_payment_project_by.setText(getString(R.string.by) + userData.name);

                        tv_payment_project_category.setText(projects.category_name);

                        tv_payment_project_location.setText(projects.city_name + "," + projects.country_name);

                        payment_project_progress.setProgress(getProgressPercent(Double.valueOf(projects.total_support_amount), Double.valueOf(projects.goal)));

                        tv_payment_project_supported.setText(getSupportPercentage(Double.valueOf(projects.total_support_amount), Double.valueOf(projects.goal)) + "%");

                        tv_payment_project_goal.setText("$" + projects.goal);

                        tv_payment_project_days_left.setText(projects.period);

                    } else {
                        //hideLoading();
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        switch (msg) {
                            case "Data Not Found":
                                showDataNotFoundDialog();
                                break;
                            case "User Not Found":
                                showSessionDialog();
                                break;
                            case "Project not Found":
//                            dialogWithTwoButton();
                                break;
                            default:
                                showServerErrorDialog();
                                break;
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
    }
}
