package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.djhs16.net.JSONParser;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.PaymentActivity;
import com.jaribha.activity.PrivacyActivity;
import com.jaribha.activity.TermsActivity;
import com.jaribha.adapters.CityAdapter;
import com.jaribha.adapters.CountryAdapter;
import com.jaribha.adapters.PaymentRewardsAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.customviews.ExpandableHeightListView;
import com.jaribha.customviews.fonts.FontButton;
import com.jaribha.customviews.fonts.FontCheckBox;
import com.jaribha.customviews.fonts.FontTextView;
import com.jaribha.interfaces.GetCountry;
import com.jaribha.models.CityBean;
import com.jaribha.models.CountryBean;
import com.jaribha.models.Rewards;
import com.jaribha.server_communication.GetCountryTask;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SetupFragment extends BaseFragment implements View.OnClickListener {

    FontButton nextBtn;

    FontCheckBox termsCb;

    FontTextView headerTv,termsCheckBox,terms,and,privacy;

    boolean selected = false;

    RadioGroup payGroup;

    ExpandableHeightListView listPaymentRewards;

    PaymentRewardsAdapter adapter;

    ArrayList<Rewards> rewardsList = new ArrayList<>();

    EditText edt_payment_amount, edt_payment_address, edt_payment_mobile, edt_payment_city, edt_payment_country;

    ListPopupWindow lpwCountry;

    CountryAdapter countryAdapter;

    CityAdapter cityAdapter;

    CountryBean countryBean;

    CityBean cityBean;

    Rewards rewards;

    String webUrl = Urls.KNET_PAYMENT;

    String payment_type = "KNET";

    String first = "";

    private ProgressDialog progressDialog;

    Activity activity;
    private Intent intent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public static SetupFragment newInstance(Rewards rewards) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.DATA, rewards);
        SetupFragment fragment = new SetupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rewards = (Rewards) getArguments().getSerializable(Constants.DATA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.set_up_payment, container, false);

        nextBtn = (FontButton) view.findViewById(R.id.setPayBtn);
        nextBtn.setOnClickListener(this);
        if (isAdded())
            nextBtn.setText(activity.getResources().getString(R.string.next));

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        termsCb = (FontCheckBox) view.findViewById(R.id.paymentTerms);

        edt_payment_amount = (EditText) view.findViewById(R.id.edt_payment_amount);

        edt_payment_amount.setText(rewards.amount);

        edt_payment_address = (EditText) view.findViewById(R.id.edt_payment_address);
        if (!TextUtils.isNullOrEmpty(getUser().address)) {
            edt_payment_address.setText(getUser().address);
        }

        edt_payment_mobile = (EditText) view.findViewById(R.id.edt_payment_mobile);

        if (!TextUtils.isNullOrEmpty(getUser().phone)) {
            edt_payment_mobile.setText(getUser().phone);
        }

        edt_payment_city = (EditText) view.findViewById(R.id.edt_payment_city);
        edt_payment_city.setOnClickListener(this);

        if (!TextUtils.isNullOrEmpty(getUser().cityname)) {
            edt_payment_city.setText(getUser().cityname);
        }

        edt_payment_country = (EditText) view.findViewById(R.id.edt_payment_country);
        edt_payment_country.setOnClickListener(this);

        if (!TextUtils.isNullOrEmpty(getUser().countryname)) {
            edt_payment_country.setText(getUser().countryname);
        }

        adapter = new PaymentRewardsAdapter(activity, rewardsList);

        payGroup = (RadioGroup) view.findViewById(R.id.payGroup);

        payGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.knet_radio) {
                    webUrl = Urls.KNET_PAYMENT;
                    payment_type = "KNET";
                } else {
                    webUrl = Urls.CREDIT_CARD_PAYMENT;
                    payment_type = "CREDIT";
                }
            }
        });

        listPaymentRewards = (ExpandableHeightListView) view.findViewById(R.id.listPaymentRewards);
        listPaymentRewards.setExpanded(true);
        listPaymentRewards.setAdapter(adapter);
        listPaymentRewards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < rewardsList.size(); i++) {
                    rewardsList.get(i).selected = false;
                }

                Rewards rewards = rewardsList.get(position);
                rewards.selected = true;
                edt_payment_amount.setText(rewards.amount);
                adapter.notifyDataSetChanged();
            }
        });

        headerTv = (FontTextView) view.findViewById(R.id.headerTv);

        termsCheckBox = (FontTextView) view.findViewById(R.id.termsCheckBox);
        terms = (FontTextView) view.findViewById(R.id.terms);
        and = (FontTextView) view.findViewById(R.id.and);
        privacy = (FontTextView) view.findViewById(R.id.privacy);

        termsCheckBox.setOnClickListener(this);
        terms.setOnClickListener(this);
        and.setOnClickListener(this);
        privacy.setOnClickListener(this);

        if (isAdded()) {
            first = getString(R.string.to_chenge_support_amount) + "<br>" + getString(R.string.its_up_to_you);
        }
        if (isAdded()) {
            String next = "<font color='#FF705C'>$10</font>" + getString(R.string.or_more);
            headerTv.setText(Html.fromHtml(first + next));
        }
        termsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selected = isChecked;
            }
        });

        cityAdapter = new CityAdapter(activity, cityArrayList);
        countryAdapter = new CountryAdapter(activity, countryList);

        lpwCountry = new ListPopupWindow(activity);

        lpwCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryBean = countryList.get(position);
                edt_payment_country.setText(countryBean.name_eng);
                lpwCountry.dismiss();
                cityArrayList.clear();
                edt_payment_city.setText("");
                cityAdapter.notifyDataSetChanged();
                getCity(countryBean, 0);
            }
        });


//        lpwCity = new ListPopupWindow(activity);
//        lpwCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                cityBean = cityArrayList.get(position);
//                edt_payment_city.setText(cityBean.name_eng);
//                lpwCity.dismiss();
//            }
//        });

        getCountries();

        return view;
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.termsCheckBox:

                if(selected){
                    selected = false;
                    termsCb.setChecked(false);
                }else {
                    selected = true;
                    termsCb.setChecked(true);
                }
                break;
            case R.id.terms:
                if(selected){
                    selected = false;
                    termsCb.setChecked(false);
                }else {
                    selected = true;
                    termsCb.setChecked(true);
                }

                intent = new Intent(getActivity(), TermsActivity.class);
                startActivity(intent);

                break;
            case R.id.and:
                if(selected){
                    selected = false;
                    termsCb.setChecked(false);
                }else {
                    selected = true;
                    termsCb.setChecked(true);
                }
                break;
            case R.id.privacy:
                if(selected){
                    selected = false;
                    termsCb.setChecked(false);
                }else {
                    selected = true;
                    termsCb.setChecked(true);
                }

                intent = new Intent(getActivity(), PrivacyActivity.class);
                startActivity(intent);

                break;

            case R.id.edt_payment_city:
                PopupWindow popUp = cityPopUpWindow();
                popUp.showAsDropDown(v, 0, 0);
                break;

            case R.id.edt_payment_country:
                lpwCountry.setAdapter(countryAdapter);
                lpwCountry.setAnchorView(edt_payment_country);
                lpwCountry.setModal(true);
                lpwCountry.show();
                break;

            case R.id.setPayBtn:

                if (isInternetConnected()) {

                    if (selected) {
                        onNextClick();
                    } else {
                        Toast.makeText(activity, getString(R.string.please_select_terms_and_conditions_), Toast.LENGTH_LONG).show();
                    }
                } else {
                    showNetworkDialog();
                }
                break;

            default:
                break;
        }
    }

    int nextOffset = 0;

    /**
     * show popup window method reuturn PopupWindow
     */
    private PopupWindow cityPopUpWindow() {
        final PopupWindow popup = new PopupWindow(activity);
        View layout = activity.getLayoutInflater().inflate(R.layout.popup_layout, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(Utils.getDeviceWidth(activity) - 80);
        // Closes the popup window when touch outside of it - when looses focus
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // the drop down list is a list view
        ListView listCities = (ListView) layout.findViewById(R.id.listCities);
        // set our adapter and pass our pop up window contents
        listCities.setAdapter(cityAdapter);
        // set on item selected
        listCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClickListener(position, popup);
            }
        });
        listCities.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                if (nextOffset != 0 && nextOffset != -1 && cityArrayList.size() >= nextOffset) {
                    getCity(countryBean, nextOffset);
                }
                return true;
            }
        });

        return popup;
    }

    private void onItemClickListener(int pos, PopupWindow popupWindow) {
        cityBean = cityArrayList.get(pos);
        edt_payment_city.setText(cityBean.name_eng);
        popupWindow.dismiss();
    }

    private void onNextClick() {
        edt_payment_amount.setError(null);
        edt_payment_address.setError(null);
        edt_payment_country.setError(null);
        edt_payment_city.setError(null);
        edt_payment_mobile.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isNullOrEmpty(edt_payment_amount.getText().toString().trim())) {
            edt_payment_amount.setError(getString(R.string.error_field_required));
            focusView = edt_payment_amount;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_payment_address.getText().toString().trim())) {
            edt_payment_address.setError(getString(R.string.error_field_required));
            focusView = edt_payment_address;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_payment_country.getText().toString().trim())) {
            edt_payment_country.setError(getString(R.string.error_field_required));
            focusView = edt_payment_country;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_payment_city.getText().toString().trim())) {
            edt_payment_city.setError(getString(R.string.error_field_required));
            focusView = edt_payment_city;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_payment_mobile.getText().toString().trim())) {
            edt_payment_mobile.setError(getString(R.string.error_field_required));
            focusView = edt_payment_mobile;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                Constants.firse = true;

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("support_amount_usd", edt_payment_amount.getText().toString().trim());
                jsonObject.put("address", edt_payment_address.getText().toString().trim());
                jsonObject.put("phone", edt_payment_mobile.getText().toString().trim());
                jsonObject.put("payment_type", payment_type);

                if (countryBean != null)
                    jsonObject.put("country_id", countryBean.id);
                else
                    jsonObject.put("country_id", getUser().country_id);

                if (cityBean != null)
                    jsonObject.put("city_id", cityBean.id);
                else
                    jsonObject.put("city_id", getUser().city_id);

                JaribhaPrefrence.setPref(activity, Constants.PAYMENT_DATA, jsonObject.toString());

                ((PaymentActivity) activity).displayFragment(1, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    ArrayList<CountryBean> countryList = new ArrayList<>();

    public void getCountries() {
        //showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", getUser().id);
            jsonObject.put("user_token", getUser().user_token);
            jsonObject.put("country_id", "");
            GetCountryTask countryTask = new GetCountryTask(jsonObject, new GetCountry() {
                @Override
                public void OnSuccess(ArrayList<CountryBean> list) {
                    hideLoading();
                    countryList.addAll(list);
                    countryAdapter.notifyDataSetChanged();
                }

                @Override
                public void OnFail(String s) {
                    hideLoading();
                    if (s.equalsIgnoreCase("User Not Found")) {
                        showSessionDialog();
                    } else if (s.equalsIgnoreCase("Data Not Found")) {
                        showDataNotFoundDialog();
                    } else {
                        showServerErrorDialog();
                    }
                }
            });
            countryTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
            showServerErrorDialog();
        }

        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);

            if (getUser() != null) {
                projectJson.put("user_id", getUser().id);
                projectJson.put("user_token", getUser().user_token);
            } else {
                projectJson.put("user_id", "");
                projectJson.put("user_token", "");
            }
            projectJson.put("project_id", JaribhaPrefrence.getPref(activity, Constants.PROJECT_ID, ""));

            getRewards(projectJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ArrayList<CityBean> cityArrayList = new ArrayList<>();

    public void getCity(final CountryBean countryBean, final int offset) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", Urls.API_KEY);
                    jsonObject.put("user_id", getUser().id);
                    jsonObject.put("user_token", getUser().user_token);
                    jsonObject.put("country_code", countryBean.code);
                    jsonObject.put("offset", offset);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.GET_CITIES_BY_COUNTRIES_V1, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject responce) {
                if (responce != null) {
                    try {
                        JSONObject jsonObject = responce.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("msg");
                        nextOffset = Integer.parseInt(jsonObject.getString("nextOffset"));
                        if (status) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject countryJson = jsonArray.getJSONObject(i).getJSONObject("City");
                                CityBean countryBean = new Gson().fromJson(countryJson.toString(), CityBean.class);
                                cityArrayList.add(countryBean);
                            }
                            cityAdapter.notifyDataSetChanged();
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
            }
        }.execute();
    }

    private void getRewards(final JSONObject nameValuePairs) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //showLoading();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                // TODO: attempt authentication against a network service.
                // TODO: register the new account here.
                return new JSONParser().getJsonObjectFromUrl1(Urls.GET_PROJECT_REWARDS, nameValuePairs);
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
                            JSONArray jsonArray = jsonObject.optJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Rewards data = new Gson().fromJson(jsonArray.getJSONObject(i).optJSONObject("Reward").toString(), Rewards.class);
                                if (rewards.id.equals(data.id)) {
                                    data.selected = true;
                                }
                                rewardsList.add(data);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            switch (msg) {
                                case "Data Not Found":
                                    showDataNotFoundDialog();
                                    break;
                                case "User Not Found":
                                    showSessionDialog();
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
        }.execute();
    }
}
