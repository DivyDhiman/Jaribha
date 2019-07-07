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
import com.jaribha.activity.PaymentActivity;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.fonts.FontButton;
import com.jaribha.interfaces.AddFavourite;
import com.jaribha.models.GetProjects;
import com.jaribha.models.Rewards;
import com.jaribha.models.UserData;
import com.jaribha.server_communication.AddFavouriteTask;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class PaymentConfirmation extends BaseFragment implements View.OnClickListener {

    FontButton nextBtn;

    Rewards mRewards;

    TextView tv_payment_estimate_delivery, tv_payment_project_title, tv_payment_project_by, tv_payment_project_category, tv_payment_project_location;

    TextView tv_payment_project_supported, tv_payment_project_goal, tv_payment_project_days_left, tv_payment_edit;

    TextView tv_payment_support_amount, tv_payment_kd_amount, tv_payment_type, tv_selected_reward, tv_shipping;

    ImageView iv_payment_project_image, iv_payment_project_favourite;

    ProgressBar payment_project_progress;

    JSONObject paymentJsonObject;

    GetProjects projects;

    private ProgressDialog progressDialog;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }


    public static PaymentConfirmation newInstance(Rewards rewards) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.DATA, rewards);
        PaymentConfirmation fragment = new PaymentConfirmation();
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
        View view = inflater.inflate(R.layout.payment_confirmation, container, false);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        nextBtn = (FontButton) view.findViewById(R.id.reviewPayBtn);
        nextBtn.setOnClickListener(this);
        if (isAdded())
            nextBtn.setText(activity.getResources().getString(R.string.next));

        tv_payment_estimate_delivery = (TextView) view.findViewById(R.id.tv_payment_estimate_delivery);
        if (isAdded())
            tv_payment_estimate_delivery.setText(activity.getResources().getString(R.string.estimated_delivery) + " " + mRewards.month + "-" + mRewards.year);

        iv_payment_project_image = (ImageView) view.findViewById(R.id.iv_payment_project_image);

        iv_payment_project_favourite = (ImageView) view.findViewById(R.id.iv_payment_project_favourite);

        tv_payment_project_title = (TextView) view.findViewById(R.id.tv_payment_project_title);

        tv_payment_project_by = (TextView) view.findViewById(R.id.tv_payment_project_by);

        tv_payment_project_category = (TextView) view.findViewById(R.id.tv_payment_project_category);

        tv_payment_project_location = (TextView) view.findViewById(R.id.tv_payment_project_location);

        tv_payment_project_supported = (TextView) view.findViewById(R.id.tv_payment_project_supported);

        tv_payment_project_goal = (TextView) view.findViewById(R.id.tv_payment_project_goal);

        tv_payment_project_days_left = (TextView) view.findViewById(R.id.tv_payment_project_days_left);

        tv_payment_support_amount = (TextView) view.findViewById(R.id.tv_payment_support_amount);

        tv_payment_kd_amount = (TextView) view.findViewById(R.id.tv_payment_kd_amount);

        tv_payment_type = (TextView) view.findViewById(R.id.tv_payment_type);

        tv_selected_reward = (TextView) view.findViewById(R.id.tv_selected_reward);

        tv_shipping = (TextView) view.findViewById(R.id.tv_shipping);

        tv_payment_edit = (TextView) view.findViewById(R.id.tv_payment_edit);
        tv_payment_edit.setOnClickListener(this);
        iv_payment_project_favourite.setOnClickListener(this);
        payment_project_progress = (ProgressBar) view.findViewById(R.id.payment_project_progress);

        try {
            paymentJsonObject = new JSONObject(JaribhaPrefrence.getPref(activity, Constants.PAYMENT_DATA, ""));
            tv_payment_support_amount.setText("$" + paymentJsonObject.getString("support_amount_usd"));
            tv_payment_type.setText(paymentJsonObject.getString("payment_type"));
            tv_selected_reward.setText(mRewards.description);
            tv_shipping.setText(mRewards.shipping);

            JSONObject paymentJson = new JSONObject();
            paymentJson.put("apikey", Urls.API_KEY);
            paymentJson.put("user_id", getUser().id);
            paymentJson.put("user_token", getUser().user_token);
            paymentJson.put("amount", paymentJsonObject.getString("support_amount_usd"));

            usdToKwd(paymentJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.reviewPayBtn:
                if (isInternetConnected()) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("apikey", Urls.API_KEY);
                        jsonObject.put("user_id", getUser().id);
                        jsonObject.put("user_token", getUser().user_token);
                        jsonObject.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));
                        jsonObject.put("reward_id", mRewards.id);
                        jsonObject.put("ip", Utils.getIPAddress(true));
                        jsonObject.put("support_amount_usd", paymentJsonObject.getString("support_amount_usd"));
                        jsonObject.put("address", paymentJsonObject.getString("address"));
                        jsonObject.put("country_id", paymentJsonObject.getString("country_id"));
                        jsonObject.put("city_id", paymentJsonObject.getString("city_id"));
                        jsonObject.put("phone", paymentJsonObject.getString("phone"));
                        jsonObject.put("language", JaribhaPrefrence.getPref(activity, Constants.PROJECT_LANGUAGE, ""));
                        jsonObject.put("device_type", Constants.DEVICE_TYPE);
                        if (paymentJsonObject.getString("payment_type").equalsIgnoreCase("KNET")) {
                            attemptToMakePayment(Urls.KNET_PAYMENT, jsonObject);
                        } else {
                            attemptToMakePayment(Urls.CREDIT_CARD_PAYMENT, jsonObject);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    showNetworkDialog();
                }
                break;

            case R.id.iv_payment_project_favourite:
                if (isInternetConnected())
                    manageFavourite(projects);
                else
                    showNetworkDialog();
                break;

            case R.id.tv_payment_edit:
                ((PaymentActivity) activity).backToFragment(new SetupFragment());
                break;

            default:
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

    private void usdToKwd(final JSONObject nameValuePairs) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                return new JSONParser().getJsonObjectFromUrl1(Urls.USD_TO_KWD, nameValuePairs);
            }

            @Override
            protected void onPostExecute(final JSONObject responce) {
                if (responce != null) {
                    try {
                        JSONObject jsonObject = responce.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String msg = jsonObject.optString("msg");
                        if (status) {
                            tv_payment_kd_amount.setText(jsonObject.getJSONObject("data").getString("final_value") + "KD");
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
            }
        }.execute();

        loadUpdates();
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

    public class GetProjectInfo extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        GetProjectInfo(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //oading();
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
                        projects.status = Utils.setProjectStatus(projects);
                        projects.days_left = Utils.getDaysLeft(projects);
                        UserData userData = new Gson().fromJson(jsonObject.optJSONObject("data").optJSONObject("User").toString(), UserData.class);

                        displayImage(activity, iv_payment_project_image, projects.image_url, ContextCompat.getDrawable(activity, R.drawable.server_error_placeholder));

                        if (projects.is_favourite) {
                            iv_payment_project_favourite.setImageResource(R.drawable.ic_fav_selected);
                        } else {
                            iv_payment_project_favourite.setImageResource(R.drawable.fav_button);
                        }

                        tv_payment_project_title.setText(projects.title);

                        tv_payment_project_by.setText(getString(R.string.by) + userData.name);

                        tv_payment_project_category.setText(projects.category_name);

                        tv_payment_project_location.setText(projects.city_name + "," + projects.country_name);

                        if(projects.total_support_amount != null || projects.goal != null)
                        payment_project_progress.setProgress(getProgressPercent(Double.valueOf(projects.total_support_amount), Double.valueOf(projects.goal)));

                        tv_payment_project_supported.setText(getSupportPercentage(Double.valueOf(projects.total_support_amount), Double.valueOf(projects.goal)) + "%");

                        tv_payment_project_goal.setText("$" + projects.goal);

                        tv_payment_project_days_left.setText(projects.days_left);

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

    public void attemptToMakePayment(final String mUrl, final JSONObject jsonObject) {
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
                return new JSONParser().getJsonObjectFromUrl1(mUrl, jsonObject);
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
                        String message = jsonObject.getString("msg");
                        if (status) {
                            JaribhaPrefrence.setPref(activity, Constants.PAYMENT_JSON, jsonObject.toString());
                            Constants.second = true;
                            ((PaymentActivity) activity).displayFragment(2, true);
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
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        }.execute();
    }
}
