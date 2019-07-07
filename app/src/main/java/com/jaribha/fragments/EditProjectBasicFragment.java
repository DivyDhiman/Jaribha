package com.jaribha.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.djhs16.net.JSONParser;
import com.djhs16.utils.AppUtils;
import com.djhs16.utils.ImageIntentHandler;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.CropperActivity;
import com.jaribha.activity.EditProjectActivity;
import com.jaribha.adapters.CategoryAdapter;
import com.jaribha.adapters.CityAdapter;
import com.jaribha.adapters.CommunityAdapter;
import com.jaribha.adapters.CountryAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.interfaces.GetCountry;
import com.jaribha.models.Category;
import com.jaribha.models.CityBean;
import com.jaribha.models.Community;
import com.jaribha.models.CountryBean;
import com.jaribha.models.GetProjects;
import com.jaribha.models.UserData;
import com.jaribha.server_communication.GetCountryTask;
import com.jaribha.utility.Constants;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditProjectBasicFragment extends BaseFragment implements View.OnClickListener {

    Button btn_basic_next;

    private EditText select_category, selectCountry, selectCity, selectPeriod, selectDollar, selectCommunity;

    EditText edt_project_title;

    private ListPopupWindow lpwCategory, lpwCountry, lpwPeriod, lpwCommunity;

    ArrayList<String> periodList = new ArrayList<>();

    CountryAdapter countryAdapter;

    CategoryAdapter categoryAdapter;

    CityAdapter cityAdapter;

    CommunityAdapter communityAdapter;

    TextView tv_image_error;

    ImageView project_image;

    RelativeLayout layout_image;

    Category category;

    CountryBean countryBean;

    CityBean cityBean;

    Community community;

    TextView tv_project_title, projectType, tv_location, tv_goal, tv_days_left,tv_remaining_count;

    EditText description;

    JSONObject mObject;

    private ProgressDialog progressDialog;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    private final static int EXTERNAL_STORAGE_PERMISSION_REQUEST = 100;

    public static EditProjectBasicFragment newInstance(JSONObject jsonObject) {
        Bundle args = new Bundle();
        args.putString(Constants.DATA, jsonObject.toString());
        EditProjectBasicFragment fragment = new EditProjectBasicFragment();
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

    GetProjects projects;

    @SuppressLint({"InflateParams", "SimpleDateFormat"})
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project_basic, null);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        btn_basic_next = (Button) view.findViewById(R.id.btn_basic_next);
        btn_basic_next.setOnClickListener(this);

        projectType = (TextView) view.findViewById(R.id.projectType);

        tv_location = (TextView) view.findViewById(R.id.tv_location);

        description = (EditText) view.findViewById(R.id.description);

        tv_goal = (TextView) view.findViewById(R.id.tv_goal);

        tv_days_left = (TextView) view.findViewById(R.id.tv_days_left);

        select_category = (EditText) view.findViewById(R.id.select_category);
        select_category.setOnClickListener(this);

        selectCountry = (EditText) view.findViewById(R.id.selectCountry);
        selectCountry.setOnClickListener(this);

        selectCity = (EditText) view.findViewById(R.id.selectCity);
        selectCity.setOnClickListener(this);

        selectPeriod = (EditText) view.findViewById(R.id.selectPeriod);
        selectPeriod.setOnClickListener(this);

        tv_remaining_count = (TextView) view.findViewById(R.id.tv_remaining_count);

        selectDollar = (EditText) view.findViewById(R.id.selectDollar);
        selectDollar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_goal.setText(String.format("$%s", s));
            }

            @Override
            public void afterTextChanged(Editable mEdit) {
                if (mEdit.toString().length() > 0 && mEdit.toString().charAt(0) == '0') {
                    if (isAdded())
                        showToast(getString(R.string.first_letter_not_zero));

                    selectDollar.setText("");
                    tv_goal.setText("");
                }
            }
        });

        selectCommunity = (EditText) view.findViewById(R.id.selectCommunity);
        selectCommunity.setOnClickListener(this);

        tv_project_title = (TextView) view.findViewById(R.id.tv_project_title);

        edt_project_title = (EditText) view.findViewById(R.id.edt_project_title);
        edt_project_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_project_title.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_short_discription = (EditText) view.findViewById(R.id.edt_short_discription);
        edt_short_discription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                description.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = 200;
                tv_remaining_count.setText(String.format(Locale.getDefault(), "%d/%d", s.toString().length(), count));
            }
        });

        tv_image_error = (TextView) view.findViewById(R.id.tv_image_error);

        project_image = (ImageView) view.findViewById(R.id.project_image);

        layout_image = (RelativeLayout) view.findViewById(R.id.layout_image);
        layout_image.setOnClickListener(this);

        for (int i = 1; i < 9; i++) {
            int i1 = i * 15;
            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DAY_OF_YEAR, i1);
            Date daysBeforeDate = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(daysBeforeDate);
            periodList.add(String.format(Locale.getDefault(), "%d %s (%s)", i1, getString(R.string.days), date));
        }

        categoryAdapter = new CategoryAdapter(activity, categoryList);
        lpwCategory = new ListPopupWindow(activity);
        lpwCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category = categoryList.get(position);
                if (isArabic()) {
                    select_category.setText(category.name_ara);
                    projectType.setText(String.format("%s%s %s %s", getString(R.string.by), getUser().name, getString(R.string.in), category.name_ara));
                } else {
                    select_category.setText(category.name_eng);
                    projectType.setText(String.format("%s%s %s %s", getString(R.string.by), getUser().name, getString(R.string.in), category.name_eng));
                }
                lpwCategory.dismiss();
            }
        });

        lpwPeriod = new ListPopupWindow(activity);
        lpwPeriod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = periodList.get(position);
                selectPeriod.setText(item);
                tv_days_left.setText(item.split(getString(R.string.days))[0].trim());
                lpwPeriod.dismiss();
            }
        });

        countryAdapter = new CountryAdapter(activity, countryList);
        lpwCountry = new ListPopupWindow(activity);
        lpwCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryBean = countryList.get(position);
                selectCountry.setText(countryBean.name_eng);
                tv_location.setText(countryBean.name_eng);
                cityArrayList.clear();
                cityAdapter.notifyDataSetChanged();
                selectCity.setText("");
                lpwCountry.dismiss();
                getCity(countryBean, 0);
            }
        });

        cityAdapter = new CityAdapter(activity, cityArrayList);

        communityAdapter = new CommunityAdapter(activity, communityList);
        lpwCommunity = new ListPopupWindow(activity);
        lpwCommunity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                community = communityList.get(position);
                selectCommunity.setText(community.name_eng);
                lpwCommunity.dismiss();
            }
        });

        setData();

        if (isInternetConnected()) {
            if (progressDialog != null && !progressDialog.isShowing())
                progressDialog.show();
            getCategory();
        } else {
            showNetworkDialog();
        }

        return view;
    }

    Community rCommunity;

    @SuppressLint("SimpleDateFormat")
    private void setData() {
        try {
            rCommunity = new Gson().fromJson(mObject.getJSONObject("Community").toString(), Community.class);

            projects = new Gson().fromJson(mObject.optJSONObject("Project").toString(), GetProjects.class);

            UserData userData = new Gson().fromJson(mObject.optJSONObject("User").toString(), UserData.class);

            StringBuilder builder = new StringBuilder();
            if (isAdded())
                builder.append(getString(R.string.by));

            if (!TextUtils.isNullOrEmpty(userData.name)) {
                if (isAdded())
                    builder.append(userData.name).append(" ").append(getString(R.string.in)).append(" ");
            } else {
                if (isAdded())
                    builder.append(" ").append(getString(R.string.in)).append(" ");
            }
            if (!TextUtils.isNullOrEmpty(projects.category_name)) {
                builder.append(projects.category_name);
                select_category.setText(projects.category_name);
            }

            projectType.setText(builder);

            if (!TextUtils.isNullOrEmpty(projects.country_name)) {
                selectCountry.setText(projects.country_name);
                tv_location.setText(projects.country_name);
            }

            if (!TextUtils.isNullOrEmpty(projects.description)) {
                description.setText(projects.description);
                edt_short_discription.setText(projects.description);
            }

            if (!TextUtils.isNullOrEmpty(projects.goal)) {
                tv_goal.setText(String.format("$%s", projects.goal));
                selectDollar.setText(projects.goal);
            }

            if (!TextUtils.isNullOrEmpty(projects.city_name))
                selectCity.setText(projects.city_name);

            if (!TextUtils.isNullOrEmpty(projects.period)) {
                tv_days_left.setText(projects.period);

                Calendar cal = GregorianCalendar.getInstance();
                cal.setTime(new Date());
                cal.add(Calendar.DAY_OF_YEAR, Integer.valueOf(projects.period));
                Date daysBeforeDate = cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date = sdf.format(daysBeforeDate);
                if (isAdded())
                    selectPeriod.setText(projects.period + " " + getString(R.string.days) + " (" + date + ")");
            }

            if (!TextUtils.isNullOrEmpty(rCommunity.name_eng))
                selectCommunity.setText(rCommunity.name_eng);

            if (!TextUtils.isNullOrEmpty(projects.title)) {
                tv_project_title.setText(projects.title);
                edt_project_title.setText(projects.title);
            }

            if (!TextUtils.isNullOrEmpty(projects.image_url))
                displayImage(activity, project_image, projects.image_url, ContextCompat.getDrawable(activity, R.drawable.server_error_placeholder));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ArrayList<Category> categoryList = new ArrayList<>();

    public void getCategory() {
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
                    jsonObject.put("category_id", "");
                    jsonObject.put("offset", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.GET_CATEGORY, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject responce) {
                if (responce != null) {
                    try {
                        JSONObject jsonObject = responce.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("msg");
                        if (status) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject countryJson = jsonArray.getJSONObject(i).getJSONObject("Category");
                                Category category = new Gson().fromJson(countryJson.toString(), Category.class);
                                categoryList.add(category);
                            }
                            categoryAdapter.notifyDataSetChanged();
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
                if (isInternetConnected()) {
                    getCountries();
                } else {
                    showNetworkDialog();
                }
            }

            @Override
            protected void onCancelled() {
            }
        }.execute();
    }

    ArrayList<CountryBean> countryList = new ArrayList<>();

    public void getCountries() {


        countryList.clear();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", getUser().id);
            jsonObject.put("user_token", getUser().user_token);
            jsonObject.put("country_id", "");
            GetCountryTask countryTask = new GetCountryTask(jsonObject, new GetCountry() {
                @Override
                public void OnSuccess(ArrayList<CountryBean> list) {
                    //hideLoading();

                    countryList.addAll(list);
                    countryAdapter.notifyDataSetChanged();
                }

                @Override
                public void OnFail(String s) {
                    //hideLoading();

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

        if (isInternetConnected()) {
            getCommunity();
        } else {
            showNetworkDialog();
        }
    }

    ArrayList<Community> communityList = new ArrayList<>();

    public void getCommunity() {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.GET_COMMUNITIES_NAMES, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject responce) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String message = jsonObject.getString("msg");
                    if (status) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Community community = new Community();
                        if (isAdded())
                            community.name_eng = getString(R.string.select_no_community);
                        community.id = "0";
                        communityList.add(community);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Community countryBean = new Gson().fromJson(jsonArray.getJSONObject(i).getJSONObject("Community").toString(), Community.class);
                            communityList.add(countryBean);
                        }
                        communityAdapter.notifyDataSetChanged();
                    } else {
                        if (message.equalsIgnoreCase("User Not Found")) {
                            showSessionDialog();
                        } else if (message.equalsIgnoreCase("Data Not Found")) {
                            showDataNotFoundDialog();
                        } else {
                            showServerErrorDialog();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showServerErrorDialog();
                }
            }

            @Override
            protected void onCancelled() {
            }
        }.execute();
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

    EditText edt_short_discription;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == 2222 && resultCode == Activity.RESULT_OK) {
            //  handleCrop(result);
            if (result != null) {
                project_image.setImageURI(Uri.parse(result.getStringExtra("cropped_uri")));
                //Picasso.with(activity).load(Uri.parse(result.getStringExtra("cropped_uri"))).into(project_image);
                imagePath = Utils.getRealPathFromURI(activity, Uri.parse(result.getStringExtra("cropped_uri")));
            }

        } else if (requestCode == Activity.RESULT_CANCELED) {
            if (!TextUtils.isNullOrEmpty(projects.image_url))
                displayImage(activity, project_image, projects.image_url, ContextCompat.getDrawable(activity, R.drawable.server_error_placeholder));

        } else {
            ImageIntentHandler intentHandler =
                    new ImageIntentHandler(getActivity(), mImagePair)
                            .folder(getString(R.string.app_name)).sizePx(2400, 1800);

            intentHandler.handleIntent(requestCode, resultCode, result);
            if (mImagePair != null) {
                //imagePath = mImagePair == null ? null : mImagePair.imagePath;
                Intent data = new Intent(activity, CropperActivity.class);
                data.putExtra("imagePath", mImagePair.imagePath);
                data.putExtra("isBasic", true);
                startActivityForResult(data, 2222);
            } else {
                showToast(getString(R.string.error));
            }
        }
    }

    String imagePath;

    private void attemptToAddProject() {
        edt_project_title.setError(null);
        select_category.setError(null);
        edt_short_discription.setError(null);
        selectCountry.setError(null);
        selectCity.setError(null);
        selectPeriod.setError(null);
        selectDollar.setError(null);
        selectCommunity.setError(null);
        tv_image_error.setError(null);


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isNullOrEmpty(edt_project_title.getText().toString().trim())) {
            edt_project_title.setError(getString(R.string.error_field_required));
            focusView = edt_project_title;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(select_category.getText().toString().trim())) {
            select_category.setError(getString(R.string.error_field_required));
            focusView = select_category;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_short_discription.getText().toString().trim())) {
            edt_short_discription.setError(getString(R.string.error_field_required));
            focusView = edt_short_discription;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(selectCountry.getText().toString().trim())) {
            selectCountry.setError(getString(R.string.error_field_required));
            focusView = selectCountry;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(selectCity.getText().toString().trim())) {
            selectCity.setError(getString(R.string.error_field_required));
            focusView = selectCity;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(selectPeriod.getText().toString().trim())) {
            selectPeriod.setError(getString(R.string.error_field_required));
            focusView = selectPeriod;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(selectDollar.getText().toString().trim())) {
            selectDollar.setError(getString(R.string.error_field_required));
            focusView = selectDollar;
            cancel = true;
        } else if (!TextUtils.isNullOrEmpty(selectDollar.getText().toString().trim())) {
            int value = Integer.parseInt(selectDollar.getText().toString().trim());
            if (value < 1000) {
                selectDollar.setError(getString(R.string.greater_thn_1000));
                focusView = selectDollar;
                cancel = true;
            } else if (value > 5000000) {
                selectDollar.setError(getString(R.string.smaller_thn_5000000));
                focusView = selectDollar;
                cancel = true;
            }
        } else if (TextUtils.isNullOrEmpty(selectCommunity.getText().toString().trim())) {
            selectCommunity.setError(getString(R.string.error_field_required));
            focusView = selectCommunity;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(imagePath)) {
            if (TextUtils.isNullOrEmpty(projects.image_url)) {
                tv_image_error.setError(getString(R.string.error_field_required));
                focusView = tv_image_error;
                cancel = true;
            }
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
                    if (countryBean != null)
                        jsonObject.put("country_id", countryBean.id);
                    else
                        jsonObject.put("country_id", projects.country_id);

                    if (cityBean != null)
                        jsonObject.put("city_id", cityBean.id);
                    else
                        jsonObject.put("city_id", projects.city_id);

                    jsonObject.put("title", edt_project_title.getText().toString());
                    jsonObject.put("description", edt_short_discription.getText().toString());

                    if (category != null)
                        jsonObject.put("category_id", category.id);
                    else
                        jsonObject.put("category_id", projects.category_id);

                    jsonObject.put("period", selectPeriod.getText().toString().split("Days")[0].trim());

                    if (community != null)
                        jsonObject.put("community_id", community.id);
                    else
                        jsonObject.put("community_id", rCommunity.id);

                    if (TextUtils.isNullOrEmpty(imagePath)) {
                        jsonObject.put("image", "");
                    } else {
                        jsonObject.put("image", Utils.bitmapToBase64(imagePath));
                    }

                    jsonObject.put("goal", tv_goal.getText().toString().replace("$", ""));
                    jsonObject.put("exist_prjct_id", projects.id);
                    addProject(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showNetworkDialog();
            }
        }
    }

    public void addProject(final JSONObject jsonObject) {
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
                return new JSONParser().getJsonObjectFromUrl1(Urls.ADD_PROJECT, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject response) {
                //hideLoading();
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                if (response != null) {
                    try {
                        // {"result":{"status":true,"msg":"Project Saved","data":{"project_id":"395"}}}
                        JSONObject jsonObject = response.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("msg");
                        if (status) {
                            ((EditProjectActivity) activity).firstLoad = false;
                            if (isTabletDevice())
                                ((EditProjectActivity) activity).displayFragment(2, true);
                            else {
                                ((EditProjectActivity) activity).displayTab(2, true);
                            }
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

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.layout_image:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    showDialog1();
                } else {
                    requestExternalStoragePermission();
                }
                break;

            case R.id.select_category:
                lpwCategory.setAdapter(categoryAdapter);
                lpwCategory.setAnchorView(select_category);
                lpwCategory.setModal(true);
                lpwCategory.show();

                break;
            case R.id.selectCountry:
                lpwCountry.setAdapter(countryAdapter);
                lpwCountry.setAnchorView(selectCountry);
                lpwCountry.setModal(true);
                lpwCountry.show();

                break;
            case R.id.selectCity:
                PopupWindow popUp = cityPopUpWindow();
                popUp.showAsDropDown(v, 0, 0);
                break;
            case R.id.selectPeriod:
                lpwPeriod.setAdapter(new ArrayAdapter<>(activity,
                        android.R.layout.simple_list_item_1, periodList));
                lpwPeriod.setAnchorView(selectPeriod);
                lpwPeriod.setModal(true);
                lpwPeriod.show();

                break;
            case R.id.selectDollar:
//                lpwDollar.setAdapter(new ArrayAdapter<>(activity,
//                        android.R.layout.simple_list_item_1, dollar));
//                lpwDollar.setAnchorView(selectDollar);
//                lpwDollar.setModal(true);
//                lpwDollar.show();

                break;
            case R.id.selectCommunity:
                lpwCommunity.setAdapter(communityAdapter);
                lpwCommunity.setAnchorView(selectCommunity);
                lpwCommunity.setModal(true);
                lpwCommunity.show();

                break;
            case R.id.btn_basic_next:
                if (isInternetConnected()) {
                    attemptToAddProject();
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
    @SuppressLint("InflateParams")
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
        selectCity.setText(cityBean.name_eng);
        popupWindow.dismiss();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestExternalStoragePermission() {

        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Storage");
        if (!addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("Camera");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        EXTERNAL_STORAGE_PERMISSION_REQUEST);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    EXTERNAL_STORAGE_PERMISSION_REQUEST);
            return;
        }

        showDialog1();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), okListener)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    ImageIntentHandler.ImagePair mImagePair;

    private void showDialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.add_image_from));
        builder.setItems(new String[]{getString(R.string.capture), getString(R.string.gallery)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = AppUtils.createImageFile();
                    if ((f != null) && f.exists()) {
                        mImagePair = new ImageIntentHandler.ImagePair(project_image, f.getAbsolutePath());
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(takePictureIntent, ImageIntentHandler.REQUEST_CAPTURE);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.camera_error), Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                } else if (which == 1) {
                    mImagePair = new ImageIntentHandler.ImagePair(project_image, null);
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, ImageIntentHandler.REQUEST_GALLERY);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case EXTERNAL_STORAGE_PERMISSION_REQUEST: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);

                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    showDialog1();
                } else {
                    // Permission Denied
                    showToast("Some Permission is Denied");
                }
            }
            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
