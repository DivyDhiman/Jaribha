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
import android.widget.AdapterView;
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
import com.jaribha.adapters.CityAdapter;
import com.jaribha.adapters.CountryAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.customviews.EndlessScrollListener;
import com.jaribha.models.CityBean;
import com.jaribha.models.CountryBean;
import com.jaribha.models.GetProjects;
import com.jaribha.models.UserData;
import com.jaribha.utility.Constants;
import com.jaribha.utility.SessionManager;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProjectAboutYouFragment extends BaseFragment implements View.OnClickListener {

    Button btn_about_next;

    private EditText edt_select_country_about, edt_select_city_about, edt_biography, edt_address, edt_mobile_number, edt_website, edt_facebook;

    private EditText edt_twitter, edt_linkedin, edt_google_plus, edt_youtube, edt_instagram, edt_other;

    private ListPopupWindow lpwCountry;

    RelativeLayout layout_about_image;

    private CountryBean countryBean;

    private CityBean cityBean;

    private ImageView iv_user_image;

    private TextView tv_image_error;

    private ProgressDialog progressDialog;

    private Activity activity;

    int nextOffset = 0;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    private final static int EXTERNAL_STORAGE_PERMISSION_REQUEST = 100;

    public static EditProjectAboutYouFragment newInstance(JSONObject jsonObject) {
        Bundle args = new Bundle();
        args.putString(Constants.DATA, jsonObject.toString());
        EditProjectAboutYouFragment fragment = new EditProjectAboutYouFragment();
        fragment.setArguments(args);
        return fragment;
    }

    JSONObject mObject;

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

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project_aboutyou, null);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        btn_about_next = (Button) view.findViewById(R.id.btn_about_next);
        btn_about_next.setOnClickListener(this);

        edt_select_country_about = (EditText) view.findViewById(R.id.edt_select_country_about);
        edt_select_country_about.setOnClickListener(this);

        edt_select_city_about = (EditText) view.findViewById(R.id.edt_select_city_about);
        edt_select_city_about.setOnClickListener(this);

        edt_biography = (EditText) view.findViewById(R.id.edt_biography);

        edt_address = (EditText) view.findViewById(R.id.edt_address);

        edt_mobile_number = (EditText) view.findViewById(R.id.edt_mobile_number);

        edt_website = (EditText) view.findViewById(R.id.edt_website);

        edt_facebook = (EditText) view.findViewById(R.id.edt_facebook);

        edt_twitter = (EditText) view.findViewById(R.id.edt_twitter);

        edt_linkedin = (EditText) view.findViewById(R.id.edt_linkedin);

        edt_google_plus = (EditText) view.findViewById(R.id.edt_google_plus);

        edt_youtube = (EditText) view.findViewById(R.id.edt_youtube);

        edt_instagram = (EditText) view.findViewById(R.id.edt_instagram);

        edt_other = (EditText) view.findViewById(R.id.edt_other);

        iv_user_image = (ImageView) view.findViewById(R.id.iv_user_image);


        tv_image_error = (TextView) view.findViewById(R.id.tv_image_error);

        countryAdapter = new CountryAdapter(activity, countryList);
        lpwCountry = new ListPopupWindow(activity);
        lpwCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryBean = countryList.get(position);
                edt_select_country_about.setText(countryBean.name_eng);
                edt_select_city_about.setText("");
                cityArrayList.clear();
                cityAdapter.notifyDataSetChanged();
                lpwCountry.dismiss();
                getCity(countryBean, 0);
            }
        });

        cityAdapter = new CityAdapter(activity, cityArrayList);

        layout_about_image = (RelativeLayout) view.findViewById(R.id.layout_about_image);
        layout_about_image.setOnClickListener(this);

        setData();

        getCountries();

        return view;
    }

    /**
     * show popup window method reuturn PopupWindow
     */
    @SuppressLint("InflateParams")
    private PopupWindow cityPopUpWindow() {
        final PopupWindow popup = new PopupWindow(getActivity());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.popup_layout, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(Utils.getDeviceWidth(getActivity()) - 80);
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
        edt_select_city_about.setText(cityBean.name_eng);
        popupWindow.dismiss();
    }

    GetProjects projects;

    private void setData() {
        try {
            projects = new Gson().fromJson(mObject.optJSONObject("Project").toString(), GetProjects.class);

            UserData userData = new Gson().fromJson(mObject.optJSONObject("User").toString(), UserData.class);

            if (!TextUtils.isNullOrEmpty(userData.countryname))
                edt_select_country_about.setText(userData.countryname);
            else if (!TextUtils.isNullOrEmpty(getUser().countryname))
                edt_select_country_about.setText(getUser().countryname);
            else
                edt_select_country_about.setText("");

            if (!TextUtils.isNullOrEmpty(userData.cityname))
                edt_select_city_about.setText(userData.cityname);
            else if (!TextUtils.isNullOrEmpty(getUser().cityname))
                edt_select_city_about.setText(getUser().cityname);
            else
                edt_select_city_about.setText("");

            if (!TextUtils.isNullOrEmpty(userData.bio))
                edt_biography.setText(userData.bio);
            else if (!TextUtils.isNullOrEmpty(getUser().bio))
                edt_biography.setText(getUser().bio);
            else
                edt_biography.setText("");

            if (!TextUtils.isNullOrEmpty(userData.address))
                edt_address.setText(userData.address);
            else if (!TextUtils.isNullOrEmpty(getUser().address))
                edt_address.setText(getUser().address);
            else
                edt_address.setText("");

            if (!TextUtils.isNullOrEmpty(userData.phone))
                edt_mobile_number.setText(userData.phone);
            else if (!TextUtils.isNullOrEmpty(getUser().phone))
                edt_mobile_number.setText(getUser().phone);
            else
                edt_mobile_number.setText("");

            if (!TextUtils.isNullOrEmpty(userData.website))
                edt_website.setText(userData.website);
            else if (!TextUtils.isNullOrEmpty(getUser().website))
                edt_website.setText(getUser().website);
            else
                edt_website.setText("");

            if (!TextUtils.isNullOrEmpty(userData.facebook))
                edt_facebook.setText(userData.facebook);
            else if (!TextUtils.isNullOrEmpty(getUser().facebook))
                edt_facebook.setText(getUser().facebook);
            else
                edt_facebook.setText("");

            if (!TextUtils.isNullOrEmpty(userData.twitter))
                edt_twitter.setText(userData.twitter);
            else if (!TextUtils.isNullOrEmpty(getUser().twitter))
                edt_twitter.setText(getUser().twitter);
            else
                edt_twitter.setText("");

            if (!TextUtils.isNullOrEmpty(userData.linkedin))
                edt_linkedin.setText(userData.linkedin);
            else if (!TextUtils.isNullOrEmpty(getUser().linkedin))
                edt_linkedin.setText(getUser().linkedin);
            else
                edt_linkedin.setText("");

            if (!TextUtils.isNullOrEmpty(userData.googleplus))
                edt_google_plus.setText(userData.googleplus);
            else if (!TextUtils.isNullOrEmpty(getUser().googleplus))
                edt_google_plus.setText(getUser().googleplus);
            else
                edt_google_plus.setText("");

            if (!TextUtils.isNullOrEmpty(userData.youtube))
                edt_youtube.setText(userData.youtube);
            else if (!TextUtils.isNullOrEmpty(getUser().youtube))
                edt_youtube.setText(getUser().youtube);
            else
                edt_youtube.setText("");

            if (!TextUtils.isNullOrEmpty(userData.instagram))
                edt_instagram.setText(userData.instagram);
            else if (!TextUtils.isNullOrEmpty(getUser().instagram))
                edt_instagram.setText(getUser().instagram);
            else
                edt_instagram.setText("");

            if (!TextUtils.isNullOrEmpty(userData.others))
                edt_other.setText(userData.others);
            else if (!TextUtils.isNullOrEmpty(getUser().others))
                edt_other.setText(getUser().others);
            else
                edt_other.setText("");

            if (!TextUtils.isNullOrEmpty(userData.pictureurl))
                displayImage(activity, iv_user_image, userData.pictureurl, ContextCompat.getDrawable(activity, R.drawable.user_pic));
            else if (!TextUtils.isNullOrEmpty(getUser().pictureurl))
                displayImage(activity, iv_user_image, getUser().pictureurl, ContextCompat.getDrawable(activity, R.drawable.user_pic));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {
            case R.id.edt_select_country_about:
                lpwCountry.setAdapter(countryAdapter);
                lpwCountry.setAnchorView(edt_select_country_about);
                lpwCountry.setModal(true);
                lpwCountry.show();
                break;

            case R.id.edt_select_city_about:
                PopupWindow popUp = cityPopUpWindow();
                popUp.showAsDropDown(v, 0, 0);
                break;

            case R.id.layout_about_image:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    showDialog1();
                } else {
                    requestExternalStoragePermission();
                }
                break;

            case R.id.btn_about_next:
                if (isInternetConnected()) {
                    attemptToAddAboutYou();
                } else {
                    showNetworkDialog();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == 2222 && resultCode == Activity.RESULT_OK) {
            //  handleCrop(result);
            if (result != null) {
                iv_user_image.setImageURI(Uri.parse(result.getStringExtra("cropped_uri")));
                // Picasso.with(activity).load(Uri.parse(result.getStringExtra("cropped_uri"))).into(iv_user_image);
                imagePath = Utils.getRealPathFromURI(activity, Uri.parse(result.getStringExtra("cropped_uri")));
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (!TextUtils.isNullOrEmpty(getUser().pictureurl))
                displayImage(activity, iv_user_image, getUser().pictureurl, ContextCompat.getDrawable(activity, R.drawable.user_pic));
        } else {
            ImageIntentHandler intentHandler =
                    new ImageIntentHandler(getActivity(), mImagePair)
                            .folder(getString(R.string.app_name)).sizePx(1200);

            intentHandler.handleIntent(requestCode, resultCode, result);
            if (mImagePair != null) {
                //imagePath = mImagePair == null ? null : mImagePair.imagePath;
                Intent data = new Intent(activity, CropperActivity.class);
                data.putExtra("imagePath", mImagePair.imagePath);
                data.putExtra("aboutYou", true);
                startActivityForResult(data, 2222);
            } else {
                showToast(getString(R.string.error));
            }
        }
    }


    String imagePath;

    private void attemptToAddAboutYou() {
        edt_biography.setError(null);
        edt_select_country_about.setError(null);
        edt_select_city_about.setError(null);
        edt_address.setError(null);
        edt_mobile_number.setError(null);
        tv_image_error.setError(null);
        edt_website.setError(null);
        edt_facebook.setError(null);
        edt_twitter.setError(null);
        edt_linkedin.setError(null);
        edt_youtube.setError(null);
        edt_google_plus.setError(null);
        edt_other.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isNullOrEmpty(imagePath) && TextUtils.isNullOrEmpty(getUser().pictureurl)) {
            tv_image_error.setError(getString(R.string.error_field_required));
            focusView = tv_image_error;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_biography.getText().toString())) {
            edt_biography.setError(getString(R.string.error_field_required));
            focusView = edt_biography;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_select_country_about.getText().toString())) {
            edt_select_country_about.setError(getString(R.string.error_field_required));
            focusView = edt_select_country_about;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_select_city_about.getText().toString())) {
            edt_select_city_about.setError(getString(R.string.error_field_required));
            focusView = edt_select_city_about;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_address.getText().toString())) {
            edt_address.setError(getString(R.string.error_field_required));
            focusView = edt_address;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_mobile_number.getText().toString())) {
            edt_mobile_number.setError(getString(R.string.error_field_required));
            focusView = edt_mobile_number;
            cancel = true;
        } else if (TextUtils.isNullOrEmpty(edt_website.getText().toString())) {
            edt_website.setError(getString(R.string.error_field_required));
            focusView = edt_website;
            cancel = true;
        } else if (!Utils.checkURL(edt_website.getText().toString())) {
            edt_website.setError(getString(R.string.incorrect_url));
            focusView = edt_website;
            cancel = true;
        } else if (!TextUtils.isNullOrEmpty(edt_facebook.getText().toString()) && !Utils.checkURL(edt_facebook.getText().toString())) {
            edt_facebook.setError(getString(R.string.incorrect_url));
            focusView = edt_facebook;
            cancel = true;
        } else if (!TextUtils.isNullOrEmpty(edt_twitter.getText().toString()) && !Utils.checkURL(edt_twitter.getText().toString())) {
            edt_twitter.setError(getString(R.string.incorrect_url));
            focusView = edt_twitter;
            cancel = true;
        } else if (!TextUtils.isNullOrEmpty(edt_linkedin.getText().toString()) && !Utils.checkURL(edt_linkedin.getText().toString())) {
            edt_linkedin.setError(getString(R.string.incorrect_url));
            focusView = edt_linkedin;
            cancel = true;
        } else if (!TextUtils.isNullOrEmpty(edt_google_plus.getText().toString()) && !Utils.checkURL(edt_google_plus.getText().toString())) {
            edt_google_plus.setError(getString(R.string.incorrect_url));
            focusView = edt_google_plus;
            cancel = true;
        } else if (!TextUtils.isNullOrEmpty(edt_youtube.getText().toString()) && !Utils.checkURL(edt_youtube.getText().toString())) {
            edt_youtube.setError(getString(R.string.incorrect_url));
            focusView = edt_youtube;
            cancel = true;
        } else if (!TextUtils.isNullOrEmpty(edt_instagram.getText().toString()) && !Utils.checkURL(edt_instagram.getText().toString())) {
            edt_instagram.setError(getString(R.string.incorrect_url));
            focusView = edt_instagram;
            cancel = true;
        } else if (!TextUtils.isNullOrEmpty(edt_other.getText().toString()) && !Utils.checkURL(edt_other.getText().toString())) {
            edt_other.setError(getString(R.string.incorrect_url));
            focusView = edt_other;
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
                    jsonObject.put("bio", edt_biography.getText().toString());

                    if (countryBean != null) {
                        jsonObject.put("country_id", countryBean.id); // *
                    } else {
                        jsonObject.put("country_id", getUser().country_id); // *
                    }

                    if (cityBean != null) {
                        jsonObject.put("city_id", cityBean.id); // *
                    } else {
                        jsonObject.put("city_id", getUser().city_id); // *
                    }

                    jsonObject.put("address", edt_address.getText().toString()); // *
                    jsonObject.put("phone", edt_mobile_number.getText().toString()); // *
                    jsonObject.put("website", edt_website.getText().toString());
                    jsonObject.put("facebookpage", edt_facebook.getText().toString());
                    jsonObject.put("twitterpage", edt_twitter.getText().toString());
                    jsonObject.put("linkedinpage", edt_linkedin.getText().toString());
                    jsonObject.put("gpluspage", edt_google_plus.getText().toString());
                    jsonObject.put("youtubepage", edt_youtube.getText().toString());
                    jsonObject.put("instagrampage", edt_instagram.getText().toString());
                    jsonObject.put("otherpage", edt_other.getText().toString());
                    if (!TextUtils.isNullOrEmpty(imagePath))
                        jsonObject.put("image", Utils.bitmapToBase64(imagePath));
                    else
                        jsonObject.put("image", "");
//                    jsonObject.put("is_store", "true");
                    addProjectAbout(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                showNetworkDialog();
            }
        }
    }

    ArrayList<CountryBean> countryList = new ArrayList<>();

    CountryAdapter countryAdapter;

    public void getCountries() {
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
                    jsonObject.put("country_id", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.GET_COUNTRIES, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject responce) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String message = jsonObject.getString("msg");
                    if (status) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject countryJson = jsonArray.getJSONObject(i).getJSONObject("Country");
                            CountryBean countryBean = new Gson().fromJson(countryJson.toString(), CountryBean.class);
                            countryList.add(countryBean);
                        }
                        countryAdapter.notifyDataSetChanged();
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
    CityAdapter cityAdapter;

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
                                if (isAdded())
                                    showToast(getString(R.string.cities_not_found));
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

    public void addProjectAbout(final JSONObject jsonObject) {
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
                return new JSONParser().getJsonObjectFromUrl1(Urls.ADD_USER_ABOUT, jsonObject);
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
                            UserData user = new Gson().fromJson(jsonObject.getJSONObject("data").toString(), UserData.class);
                            SessionManager.getInstance(activity).saveUser(user);
                            ((EditProjectActivity) activity).firstLoad = false;
                            if (!isTabletDevice())
                                ((EditProjectActivity) activity).displayTab(4, true);
                            else
                                ((EditProjectActivity) activity).displayFragment(4, true);
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
                        mImagePair = new ImageIntentHandler.ImagePair(iv_user_image, f.getAbsolutePath());
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(takePictureIntent, ImageIntentHandler.REQUEST_CAPTURE);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.camera_error), Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                } else if (which == 1) {
                    mImagePair = new ImageIntentHandler.ImagePair(iv_user_image, null);
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
