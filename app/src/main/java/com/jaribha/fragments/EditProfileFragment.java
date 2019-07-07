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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.djhs16.net.JSONParser;
import com.djhs16.utils.AppUtils;
import com.djhs16.utils.ImageIntentHandler;
import com.google.gson.Gson;
import com.jaribha.R;
import com.jaribha.activity.CropperActivity;
import com.jaribha.activity.HomeScreenActivity;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.adapters.CityAdapter;
import com.jaribha.adapters.CountryAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.customviews.TextDrawable;
import com.jaribha.models.CityBean;
import com.jaribha.models.CountryBean;
import com.jaribha.models.UserData;
import com.jaribha.utility.SessionManager;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utility;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditProfileFragment extends BaseFragment implements View.OnClickListener {

    private BezelImageView biv_edit_profile;

    private EditText edt_user_email, edt_user_full_name, edt_user_biography, edt_user_country, edt_user_city, edt_user_address;

    private EditText edt_user_contact_number, edt_user_web_site, edt_user_fb_page, edt_user_twitter_page, edt_user_linkedin_page;

    private EditText edt_user_google_page, edt_user_youtube_page, edt_user_instagram_page, edt_user_others;

    Button btn_user_update;

    TextDrawable.IBuilder mDrawableBuilder;

    private ListPopupWindow lpwCountry;

    private CountryAdapter countryAdapter;

    private CityAdapter cityAdapter;

    private String countryId = "", cityId = "";

    private CountryBean countryBean;

    /**
     * Keep track of the UserUpdateTask to ensure we can cancel it if requested.
     */
    private UserUpdateTask mAuthTask = null;

    private static final int EXTERNAL_STORAGE_PERMISSION_REQUEST = 23;

    private ProgressDialog progressDialog;

    private Activity activity;

    private TextDrawable drawable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        edt_user_country = (EditText) view.findViewById(R.id.edt_user_country);
        edt_user_country.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edt_user_city = (EditText) view.findViewById(R.id.edt_user_city);
        edt_user_city.setImeOptions(EditorInfo.IME_ACTION_DONE);

        btn_user_update = (Button) view.findViewById(R.id.btn_user_update);
        btn_user_update.setOnClickListener(this);

        biv_edit_profile = (BezelImageView) view.findViewById(R.id.biv_edit_profile);
        biv_edit_profile.setOnClickListener(this);
        biv_edit_profile.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mDrawableBuilder = TextDrawable.builder().beginConfig()
                .withBorder(4)
                .endConfig()
                .round();

        String text = getUser().name.trim();
        text = String.valueOf(text.charAt(0));
        drawable = mDrawableBuilder.build(text, Color.parseColor("#EA5D4C"));

        countryAdapter = new CountryAdapter(activity, countryList);
        cityAdapter = new CityAdapter(activity, cityList);

        if (TextUtils.isNullOrEmpty(getUser().pictureurl)) {
            biv_edit_profile.setImageDrawable(drawable);
        } else {
            displayImage(activity, biv_edit_profile, getUser().pictureurl, drawable);
        }

        edt_user_email = (EditText) view.findViewById(R.id.edt_user_email);
        edt_user_email.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().email)) {
            edt_user_email.setText(getUser().email);
        }

        edt_user_full_name = (EditText) view.findViewById(R.id.edt_user_full_name);
        edt_user_full_name.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().name)) {
            edt_user_full_name.setText(getUser().name);
        }

        edt_user_biography = (EditText) view.findViewById(R.id.edt_user_biography);
        edt_user_biography.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().bio)) {
            edt_user_biography.setText(getUser().bio);
        }

        edt_user_country = (EditText) view.findViewById(R.id.edt_user_country);
        edt_user_country.setImeOptions(EditorInfo.IME_ACTION_DONE);

        edt_user_country.setOnClickListener(this);
        if (!TextUtils.isNullOrEmpty(getUser().country_id)) {
            countryId = getUser().country_id;
            edt_user_country.setText(getUser().countryname);
        }

        edt_user_city = (EditText) view.findViewById(R.id.edt_user_city);
        edt_user_city.setImeOptions(EditorInfo.IME_ACTION_DONE);

        edt_user_city.setOnClickListener(this);
        if (!TextUtils.isNullOrEmpty(getUser().city_id)) {
            cityId = getUser().city_id;
            edt_user_city.setText(getUser().cityname);
        }

        edt_user_address = (EditText) view.findViewById(R.id.edt_user_address);
        edt_user_address.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().address)) {
            edt_user_address.setText(getUser().address);
        }

        edt_user_contact_number = (EditText) view.findViewById(R.id.edt_user_contact_number);
        edt_user_contact_number.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().phone)) {
            edt_user_contact_number.setText(getUser().phone);
        }

        edt_user_web_site = (EditText) view.findViewById(R.id.edt_user_web_site);
        edt_user_web_site.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().website)) {
            edt_user_web_site.setText(getUser().website);
        }

        edt_user_fb_page = (EditText) view.findViewById(R.id.edt_user_fb_page);
        edt_user_fb_page.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().facebook)) {
            edt_user_fb_page.setText(getUser().facebook);
        }

        edt_user_twitter_page = (EditText) view.findViewById(R.id.edt_user_twitter_page);
        edt_user_twitter_page.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().twitter)) {
            edt_user_twitter_page.setText(getUser().twitter);
        }

        edt_user_linkedin_page = (EditText) view.findViewById(R.id.edt_user_linkedin_page);
        edt_user_linkedin_page.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().linkedin)) {
            edt_user_linkedin_page.setText(getUser().linkedin);
        }

        edt_user_google_page = (EditText) view.findViewById(R.id.edt_user_google_page);
        edt_user_google_page.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().googleplus)) {
            edt_user_google_page.setText(getUser().googleplus);
        }

        edt_user_youtube_page = (EditText) view.findViewById(R.id.edt_user_youtube_page);
        edt_user_youtube_page.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().youtube)) {
            edt_user_youtube_page.setText(getUser().youtube);
        }

        edt_user_instagram_page = (EditText) view.findViewById(R.id.edt_user_instagram_page);
        edt_user_instagram_page.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().instagram)) {
            edt_user_instagram_page.setText(getUser().instagram);
        }

        edt_user_others = (EditText) view.findViewById(R.id.edt_user_others);
        edt_user_others.setImeOptions(EditorInfo.IME_ACTION_DONE);

        if (!TextUtils.isNullOrEmpty(getUser().others)) {
            edt_user_others.setText(getUser().others);
        }

        lpwCountry = new ListPopupWindow(activity);
        lpwCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryBean = countryList.get(position);
                String item = countryBean.name_eng;
                edt_user_country.setText(item);
                edt_user_city.setText("");
                cityId = "";
                cityList.clear();
                lpwCountry.dismiss();
                cityAdapter.notifyDataSetChanged();
                countryId = countryBean.id;
                if (isInternetConnected())
                    getCityByCountry(countryBean, 0);
                else
                    showNetworkDialog();
            }
        });

        if (isInternetConnected())
            getAllCountry();
        else
            showNetworkDialog();

        return view;
    }

    private void getAllCountry() {
        //showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", getUser().id);
            jsonObject.put("user_token", getUser().user_token);
            jsonObject.put("country_id", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new GetCountryTask(jsonObject).execute();
    }

    private void getCityByCountry(CountryBean countryBean, int nextOffset) {
        // showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", getUser().id);
            jsonObject.put("user_token", getUser().user_token);
            jsonObject.put("country_code", countryBean.code);
            jsonObject.put("offset", nextOffset);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isInternetConnected())
            new GetUserCityByCountryTask(jsonObject).execute();
        else
            showNetworkDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == 2222 && resultCode == Activity.RESULT_OK) {
            if (result != null) {
                biv_edit_profile.setImageURI(Uri.parse(result.getStringExtra("cropped_uri")));
                imagePath = Utils.getRealPathFromURI(activity, Uri.parse(result.getStringExtra("cropped_uri")));
            }

        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                if (TextUtils.isNullOrEmpty(getUser().pictureurl)) {
                    biv_edit_profile.setImageDrawable(drawable);
                } else {
                    displayImage(activity, biv_edit_profile, getUser().pictureurl, drawable);
                }

            } else {
                try {
                    ImageIntentHandler intentHandler =
                            new ImageIntentHandler(getActivity(), mImagePair)
                                    .folder(getString(R.string.app_name)).sizePx(1200);

                    intentHandler.handleIntent(requestCode, resultCode, result);
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                    showToast("Image size is too large.Please upload small image.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (mImagePair != null && !TextUtils.isNullOrEmpty(mImagePair.imagePath)) {
                    //imagePath = mImagePair == null ? null : mImagePair.imagePath;
                    Intent data = new Intent(activity, CropperActivity.class);
                    data.putExtra("imagePath", mImagePair.imagePath);
                    data.putExtra("isEditProfile", true);
                    startActivityForResult(data, 2222);
                } else {
                    showToast(getString(R.string.error));
                }
            }
        }
    }

    String imageUpload = "";
    String imagePath = "";

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {

            case R.id.btn_user_update:
                edt_user_full_name.setError(null);
                edt_user_address.setError(null);
                edt_user_biography.setError(null);
                edt_user_web_site.setError(null);
                edt_user_fb_page.setError(null);
                edt_user_twitter_page.setError(null);
                edt_user_linkedin_page.setError(null);
                edt_user_google_page.setError(null);
                edt_user_youtube_page.setError(null);
                edt_user_others.setError(null);

                boolean cancel = false;
                View focusView = null;
                if (!TextUtils.isNullOrEmpty(imagePath)) {
                    imageUpload = Utils.bitmapToBase64(imagePath);
                }

                if (TextUtils.isNullOrEmpty(edt_user_email.getText().toString())) {
                    edt_user_email.setError(getString(R.string.error_field_required));
                    focusView = edt_user_email;
                    cancel = true;
                } else if (!TextUtils.isValidEmail(edt_user_email.getText().toString())) {
                    edt_user_email.setError(getString(R.string.incorrect_email));
                    focusView = edt_user_email;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_user_full_name.getText().toString())) {
                    edt_user_full_name.setError(getString(R.string.error_field_required));
                    focusView = edt_user_full_name;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_user_biography.getText().toString())) {
                    edt_user_biography.setError(getString(R.string.error_field_required));
                    focusView = edt_user_biography;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_user_city.getText().toString())) {
                    edt_user_city.setError(getString(R.string.error_field_required));
                    focusView = edt_user_city;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_user_country.getText().toString())) {
                    edt_user_country.setError(getString(R.string.error_field_required));
                    focusView = edt_user_country;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_user_address.getText().toString())) {
                    edt_user_address.setError(getString(R.string.error_field_required));
                    focusView = edt_user_address;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(edt_user_contact_number.getText().toString())) {
                    edt_user_contact_number.setError(getString(R.string.error_field_required));
                    focusView = edt_user_contact_number;
                    cancel = true;
                } else if (!TextUtils.isValidMobile(edt_user_contact_number.getText().toString())) {
                    edt_user_contact_number.setError(getString(R.string.incorrect_mobile));
                    focusView = edt_user_contact_number;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(edt_user_web_site.getText().toString()) && !Utils.checkURL(edt_user_web_site.getText().toString())) {
                    edt_user_web_site.setError(getString(R.string.incorrect_url));
                    focusView = edt_user_web_site;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(edt_user_fb_page.getText().toString()) && !Utils.checkURL(edt_user_fb_page.getText().toString())) {
                    edt_user_fb_page.setError(getString(R.string.incorrect_url));
                    focusView = edt_user_fb_page;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(edt_user_twitter_page.getText().toString()) && !Utils.checkURL(edt_user_twitter_page.getText().toString())) {
                    edt_user_twitter_page.setError(getString(R.string.incorrect_url));
                    focusView = edt_user_twitter_page;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(edt_user_linkedin_page.getText().toString()) && !Utils.checkURL(edt_user_linkedin_page.getText().toString())) {
                    edt_user_linkedin_page.setError(getString(R.string.incorrect_url));
                    focusView = edt_user_linkedin_page;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(edt_user_google_page.getText().toString()) && !Utils.checkURL(edt_user_google_page.getText().toString())) {
                    edt_user_google_page.setError(getString(R.string.incorrect_url));
                    focusView = edt_user_google_page;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(edt_user_youtube_page.getText().toString()) && !Utils.checkURL(edt_user_youtube_page.getText().toString())) {
                    edt_user_youtube_page.setError(getString(R.string.incorrect_url));
                    focusView = edt_user_youtube_page;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(edt_user_instagram_page.getText().toString()) && !Utils.checkURL(edt_user_instagram_page.getText().toString())) {
                    edt_user_instagram_page.setError(getString(R.string.incorrect_url));
                    focusView = edt_user_instagram_page;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(edt_user_others.getText().toString()) && !Utils.checkURL(edt_user_others.getText().toString())) {
                    edt_user_others.setError(getString(R.string.incorrect_url));
                    focusView = edt_user_others;
                    cancel = true;
                }
                if (cancel) {
                    focusView.requestFocus();
                } else {

                    if (isInternetConnected()) {
                        attemptToUpdateUser();
                    } else {
                        showNetworkDialog();
                    }
                }
                break;

            case R.id.edt_user_country:
                lpwCountry.setAdapter(countryAdapter);
                lpwCountry.setAnchorView(edt_user_country);
                lpwCountry.setModal(true);
                lpwCountry.show();
                break;

            case R.id.edt_user_city:
                PopupWindow popUp = cityPopUpWindow();
                popUp.showAsDropDown(v, 0, 0);

                break;

            case R.id.biv_edit_profile:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    showDialog1();
                } else {
                    requestExternalStoragePermission();
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
        popup.setWidth(Utils.getDeviceWidth(activity) - 40);
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
                if (nextOffset != 0 && nextOffset != -1 && cityList.size() >= nextOffset) {
                    getCityByCountry(countryBean, nextOffset);
                }
                return true;
            }
        });

        return popup;
    }

    private void onItemClickListener(int pos, PopupWindow popupWindow) {
        String item = cityList.get(pos).name_eng;
        cityId = cityList.get(pos).id;
        edt_user_city.setText(item);
        popupWindow.dismiss();
    }

    private void attemptToUpdateUser() {
        //showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", getUser().id);
            jsonObject.put("user_token", getUser().user_token);
            jsonObject.put("name", edt_user_full_name.getText().toString());
            jsonObject.put("address", edt_user_address.getText().toString());
            jsonObject.put("country_id", countryId);
            jsonObject.put("city_id", cityId);
            jsonObject.put("phone", edt_user_contact_number.getText().toString());
            jsonObject.put("bio", edt_user_biography.getText().toString());
            jsonObject.put("website", edt_user_web_site.getText().toString());
            jsonObject.put("facebook", edt_user_fb_page.getText().toString());
            jsonObject.put("twitter", edt_user_twitter_page.getText().toString());
            jsonObject.put("linkedin", edt_user_linkedin_page.getText().toString());
            jsonObject.put("googleplus", edt_user_google_page.getText().toString());
            jsonObject.put("youtube", edt_user_youtube_page.getText().toString());
            jsonObject.put("instagram", edt_user_instagram_page.getText().toString());
            jsonObject.put("others", edt_user_others.getText().toString());

            if (!TextUtils.isNullOrEmpty(imagePath) && new File(imagePath).exists()) {
                jsonObject.put("image", Utils.bitmapToBase64(imagePath));
            } else {
                jsonObject.put("image", "");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mAuthTask = new UserUpdateTask(jsonObject);
        mAuthTask.execute((Void) null);
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserUpdateTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        UserUpdateTask(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.UPDATE_PROFILE, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            mAuthTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String message = jsonObject.getString("msg");
                    if (status) {
                        UserData user = new Gson().fromJson(jsonObject.getJSONObject("data").toString(), UserData.class);
                        SessionManager.getInstance(activity).saveUser(user);
                        showToast(message);
                        Intent intent2 = new Intent(getActivity().getApplicationContext(), HomeScreenActivity.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
                        getActivity().finish();
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
            mAuthTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    ArrayList<CityBean> cityList = new ArrayList<>();

    public class GetUserCityByCountryTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject jsonObject;

        GetUserCityByCountryTask(JSONObject params) {
            this.jsonObject = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_CITIES_BY_COUNTRIES_V1, jsonObject);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            //mAuthTask = null;
            //  hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    // String msg = jsonObject.getString("msg");
                    nextOffset = Integer.parseInt(jsonObject.getString("nextOffset"));
                    if (status) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        //cityList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject countryJson = jsonArray.getJSONObject(i).getJSONObject("City");
                            CityBean cityBean = new Gson().fromJson(countryJson.toString(), CityBean.class);
                            cityList.add(cityBean);
                        }
                        cityAdapter.notifyDataSetChanged();
                    } else {
                        if (jsonObject.getString("msg").equals("Data Not Found")) {
                            showToast(getString(R.string.cities_not_found));
                        } else if (jsonObject.getString("msg").equals("User Not Found")) {
                            showSessionDialog();
                        } else {
                            showServerErrorDialog();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showServerErrorDialog();
            }
        }

        @Override
        protected void onCancelled() {
            //mAuthTask = null;
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }

    ArrayList<CountryBean> countryList = new ArrayList<>();

    /**
     * Represents an asynchronous GetUserCity task used to Get user city from city code
     */
    public class GetCountryTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject jsonObject;

        GetCountryTask(JSONObject params) {
            this.jsonObject = params;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.GET_COUNTRIES, jsonObject);
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

                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject countryJson = jsonArray.getJSONObject(i).getJSONObject("Country");
                            CountryBean countryBean = new Gson().fromJson(countryJson.toString(), CountryBean.class);
                            countryList.add(countryBean);
                        }
                    } else {
                        if (jsonObject.getString("msg").equals("Data Not Found")) {
                            showToast("Countries not found");
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
            /*if (isInternetConnected())
                getCityByCountry(new CountryBean(getUser().country_id), 0);
            else
                showNetworkDialog();*/
        }

        @Override
        protected void onCancelled() {
            //hideLoading();
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        }
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
                        mImagePair = new ImageIntentHandler.ImagePair(biv_edit_profile, f.getAbsolutePath());
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(takePictureIntent, ImageIntentHandler.REQUEST_CAPTURE);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.camera_error), Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                } else if (which == 1) {
                    try {
                        mImagePair = new ImageIntentHandler.ImagePair(biv_edit_profile, null);
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, ImageIntentHandler.REQUEST_GALLERY);
                        dialog.dismiss();
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                        showToast("Image size is too large.Please upload small image.");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
//            case EXTERNAL_STORAGE_PERMISSION_REQUEST: {
//                Map<String, Integer> perms = new HashMap<>();
//                // Initial
//                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
//                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
//
//                // Fill with results
//                for (int i = 0; i < permissions.length; i++)
//                    perms.put(permissions[i], grantResults[i]);
//                // Check for ACCESS_FINE_LOCATION
//                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                    // All Permissions Granted
//                    showDialog1();
//                } else {
//                    // Permission Denied
//                    showToast("Some Permission is Denied");
//                }
//            }
//            break;
//
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);


            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showDialog1();
                } else {
                    //code for deny
                    // Permission Denied
                    showToast("Some Permission is Denied");
                }
                break;
        }
    }

}
