package com.jaribha.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.djhs16.net.JSONParser;
import com.djhs16.utils.AppUtils;
import com.djhs16.utils.ImageIntentHandler;
import com.eyeem.recyclerviewtools.Log;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddNewCommunityActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView iv_close, communityImage;

    TextView tv_title, tv_community_count;

    BezelImageView img_user_image;

    Button submit;

    private EditText title, email, address, description, phoneCommunity, web, facebook, google, twitter, linkedin, insta, other, youtube, personName, personPhone, personEmail;

    private String titleStr, imageBase64 = "", emailStr, phoneCommunityStr, descriptionStr, webStr, facebookStr, googleStr, twitterStr, instaStr, linkedinStr, otherStr, youtubeStr, personEmailStr, personPhoneStr, personNameStr, addressStr;

    private final static int EXTERNAL_STORAGE_PERMISSION_REQUEST = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_community);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tv_title = (TextView) findViewById(R.id.tv_title);

        communityImage = (ImageView) findViewById(R.id.communityImage);

        iv_close = (ImageView) findViewById(R.id.iv_close);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);

        submit = (Button) findViewById(R.id.submit);

        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.add_new_community));

        title = (EditText) findViewById(R.id.communityTitle);

        email = (EditText) findViewById(R.id.communityEmail);

        address = (EditText) findViewById(R.id.communityAddress);

        phoneCommunity = (EditText) findViewById(R.id.communityPhone);

        web = (EditText) findViewById(R.id.communityWeb);

        facebook = (EditText) findViewById(R.id.communityFb);

        google = (EditText) findViewById(R.id.communityGP);

        twitter = (EditText) findViewById(R.id.communityTwitter);

        insta = (EditText) findViewById(R.id.communityInsta);

        youtube = (EditText) findViewById(R.id.communityYouTube);

        linkedin = (EditText) findViewById(R.id.communityLinkedin);

        other = (EditText) findViewById(R.id.communityOther);

        personName = (EditText) findViewById(R.id.personName);

        personEmail = (EditText) findViewById(R.id.personEmail);

        personPhone = (EditText) findViewById(R.id.personNumber);

        communityImage.setOnClickListener(this);

        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(this);

        tv_community_count = (TextView) findViewById(R.id.tv_community_count);

        description = (EditText) findViewById(R.id.communityDesc);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int count = 200;
                tv_community_count.setText(String.format(Locale.getDefault(), "%d/%d", s.toString().length(), count));
            }
        });

        img_user_image.setVisibility(View.VISIBLE);
        displayUserImage(img_user_image);
        img_user_image.setOnClickListener(this);

        submit.setOnClickListener(this);
    }

    String imagePath;

    @Override
    public void onClick(View v) {
        hideKeyBoard(v);
        switch (v.getId()) {

            case R.id.iv_close:
                finish();
                break;
            case R.id.communityImage:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    showDialog();
                } else {
                    requestExternalStoragePermission();
                }
                break;
            case R.id.img_user_image:
                startActivity(new Intent(getActivity(), MyPortfolioActivity.class));
                break;

            case R.id.submit:
                title.setError(null);
                email.setError(null);
                address.setError(null);
                phoneCommunity.setError(null);
                description.setError(null);
                web.setError(null);
                facebook.setError(null);
                google.setError(null);
                twitter.setError(null);
                insta.setError(null);
                linkedin.setError(null);
                other.setError(null);
                youtube.setError(null);
                personEmail.setError(null);
                personPhone.setError(null);
                personName.setError(null);

                titleStr = title.getText().toString().trim();
                emailStr = email.getText().toString().trim();
                addressStr = address.getText().toString().trim();
                phoneCommunityStr = phoneCommunity.getText().toString().trim();
                descriptionStr = description.getText().toString().trim();
                webStr = web.getText().toString().trim();
                facebookStr = facebook.getText().toString().trim();
                googleStr = google.getText().toString().trim();
                twitterStr = twitter.getText().toString().trim();
                instaStr = insta.getText().toString().trim();
                linkedinStr = linkedin.getText().toString().trim();
                otherStr = other.getText().toString().trim();
                youtubeStr = youtube.getText().toString().trim();
                personEmailStr = personEmail.getText().toString().trim();
                personPhoneStr = personPhone.getText().toString().trim();
                personNameStr = personName.getText().toString().trim();

                boolean cancel = false;
                View focusView = null;


                if (TextUtils.isNullOrEmpty(titleStr)) {
                    title.setError(getString(R.string.error_field_required));
                    focusView = title;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(emailStr)) {
                    email.setError(getString(R.string.error_field_required));
                    focusView = email;
                    cancel = true;
                } else if (!Utils.isValidEmailAddress(emailStr)) {
                    email.setError(getString(R.string.error_invalid_email));
                    focusView = email;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(descriptionStr)) {
                    description.setError(getString(R.string.error_field_required));
                    focusView = description;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(addressStr)) {
                    address.setError(getString(R.string.error_field_required));
                    focusView = address;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(phoneCommunityStr)) {
                    phoneCommunity.setError(getString(R.string.error_field_required));
                    focusView = phoneCommunity;
                    cancel = true;
                } else if (!TextUtils.isValidMobile(phoneCommunityStr)) {
                    phoneCommunity.setError(getString(R.string.incorrect_mobile));
                    focusView = phoneCommunity;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(webStr)) {
                    web.setError(getString(R.string.error_field_required));
                    focusView = web;
                    cancel = true;
                } else if (!Utils.checkURL(webStr)) {
                    web.setError(getString(R.string.incorrect_url));
                    focusView = web;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(facebook.getText().toString()) && !Utils.checkURL(facebook.getText().toString())) {
                    facebook.setError(getString(R.string.incorrect_url));
                    focusView = facebook;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(twitter.getText().toString()) && !Utils.checkURL(twitter.getText().toString())) {
                    twitter.setError(getString(R.string.incorrect_url));
                    focusView = twitter;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(linkedin.getText().toString()) && !Utils.checkURL(linkedin.getText().toString())) {
                    linkedin.setError(getString(R.string.incorrect_url));
                    focusView = linkedin;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(google.getText().toString()) && !Utils.checkURL(google.getText().toString())) {
                    google.setError(getString(R.string.incorrect_url));
                    focusView = google;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(youtube.getText().toString()) && !Utils.checkURL(youtube.getText().toString())) {
                    youtube.setError(getString(R.string.incorrect_url));
                    focusView = youtube;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(insta.getText().toString()) && !Utils.checkURL(insta.getText().toString())) {
                    insta.setError(getString(R.string.incorrect_url));
                    focusView = insta;
                    cancel = true;
                } else if (!TextUtils.isNullOrEmpty(other.getText().toString()) && !Utils.checkURL(other.getText().toString())) {
                    other.setError(getString(R.string.incorrect_url));
                    focusView = other;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(personNameStr)) {
                    personName.setError(getString(R.string.error_field_required));
                    focusView = personName;
                    cancel = true;
                } else if (TextUtils.isNullOrEmpty(personEmailStr)) {
                    personEmail.setError(getString(R.string.error_field_required));
                    focusView = personEmail;
                    cancel = true;
                } else if (!Utils.isValidEmailAddress(personEmailStr)) {
                    personEmail.setError(getString(R.string.error_invalid_email));
                    focusView = personEmail;
                    cancel = true;
                } else if (!TextUtils.isValidMobile(personPhoneStr)) {
                    personPhone.setError(getString(R.string.incorrect_mobile));
                    focusView = personPhone;
                    cancel = true;
                }
                if (cancel) {
                    focusView.requestFocus();
                } else {
                    if (TextUtils.isNullOrEmpty(imagePath)) {
                        // imageBase64 = Utils.bitmapToBase64(imagePath);
                        Toast.makeText(this, getString(R.string.please_select_image), Toast.LENGTH_LONG).show();
                    } else {
                        imageBase64 = Utils.bitmapToBase64(imagePath);
                        if (isInternetConnected())
                            addCommunity();
                        else
                            showAlertDialog(getResources().getString(R.string.internet));
                    }
                }
                break;

            default:
                break;

        }
    }

    public void addCommunity() {
        try {
            JSONObject projectJson = new JSONObject();
            projectJson.put("apikey", Urls.API_KEY);
            projectJson.put("user_id", getUser().id);
            projectJson.put("user_token", getUser().user_token);
            projectJson.put("community_id", "");
            projectJson.put("name_eng", titleStr);
            projectJson.put("name_ara", titleStr);

            /*if (!isArabic()) {
                projectJson.put("name_eng", titleStr);
                projectJson.put("name_ara", "");
            } else {
                projectJson.put("name_eng", "");
                projectJson.put("name_ara", titleStr);
            }*/
            projectJson.put("email", emailStr);
            projectJson.put("descripton", descriptionStr);
            projectJson.put("address", addressStr);
            projectJson.put("mobile", phoneCommunityStr);
            projectJson.put("websiteURL", webStr);
            projectJson.put("fb_url", facebookStr);
            projectJson.put("gplus_url", googleStr);
            projectJson.put("twitter_url", twitterStr);
            projectJson.put("linkedin_url", linkedinStr);
            projectJson.put("youtube_url", youtubeStr);
            projectJson.put("instagram_url", instaStr);
            projectJson.put("pintrest_url", "");
            projectJson.put("other_url", otherStr);
            projectJson.put("personName", personNameStr);
            projectJson.put("personEmail", personEmailStr);
            projectJson.put("personMobile", personPhoneStr);
            projectJson.put("image", imageBase64);

            AddCommunityAPI mAuthTask = new AddCommunityAPI(projectJson);
            mAuthTask.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class AddCommunityAPI extends AsyncTask<Void, Void, JSONObject> {

        JSONObject nameValuePairs;

        AddCommunityAPI(JSONObject params) {
            this.nameValuePairs = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return new JSONParser().getJsonObjectFromUrl1(Urls.ADD_COMMUNITIES, nameValuePairs);
        }

        @Override
        protected void onPostExecute(final JSONObject responce) {
            hideLoading();
            if (responce != null) {
                try {
                    JSONObject jsonObject = responce.getJSONObject("result");
                    Boolean status = jsonObject.getBoolean("status");
                    String msg = jsonObject.optString("msg");
                    if (status) {
                        communityImage.setImageResource(R.drawable.image_upload_camera);
                        imagePath = null;
                        title.setText("");
                        email.setText("");
                        phoneCommunity.setText("");
                        personEmail.setText("");
                        personName.setText("");
                        personPhone.setText("");
                        description.setText("");
                        address.setText("");

                        web.setText("");
                        linkedin.setText("");
                        insta.setText("");
                        facebook.setText("");
                        google.setText("");
                        twitter.setText("");
                        youtube.setText("");
                        other.setText("");

                        showCustomeDialog(R.drawable.right_icon, getString(R.string.success), getString(R.string.community_added), getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                        //showAlertDialog(getString(R.string.community_added));
                    } else {
                        switch (msg) {
                            case "Data Not Found":
                                showDataNotFoundDialog();
                                break;
                            case "User Not Found":
                                showSessionDialog();
                                break;
                            default:
                                showServerDialogDialog();
                                break;
                        }
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
            hideLoading();
        }
    }

//    private void showDialog() {
//        // display Image Picker
//        new Picker.Builder(getActivity(), pickListener, R.style.MIP_theme)
//                .setPickMode(Picker.PickMode.SINGLE_IMAGE)
//                .build()
//                .startActivity();
//    }
//
//    // Image picker listener
//    Picker.PickListener pickListener = new Picker.PickListener() {
//        @Override
//        public void onPickedSuccessfully(ArrayList<ImageEntry> images) {
//            if (Utils.checkForCommunityImage(getActivity(), images.get(0).path)) {
//                imagePath = ImageUtils.getRightAngleImage(images.get(0).path);
//                Uri uri = Uri.fromFile(new File(imagePath));
//                beginCrop(uri);
////                Picasso.with(getActivity())
////                        .load(uri)
////                        .placeholder(R.drawable.server_error_placeholder)
////                        .error(R.drawable.server_error_placeholder)
////                        .into(communityImage);
//            }
//        }
//
//        @Override
//        public void onCancel() {
//
//        }
//    };

    ImageIntentHandler.ImagePair mImagePair;

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.add_image_from));
        builder.setItems(new String[]{getString(R.string.capture), getString(R.string.gallery)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = AppUtils.createImageFile();
                    if ((f != null) && f.exists()) {
                        mImagePair = new ImageIntentHandler.ImagePair(communityImage, f.getAbsolutePath());
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                        startActivityForResult(takePictureIntent, ImageIntentHandler.REQUEST_CAPTURE);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.camera_error), Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                } else if (which == 1) {
                    mImagePair = new ImageIntentHandler.ImagePair(communityImage, null);
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, ImageIntentHandler.REQUEST_GALLERY);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == 2222 && resultCode == Activity.RESULT_OK) {
            //  handleCrop(result);
            if (result != null) {
                Picasso.with(this).load(Uri.parse(result.getStringExtra("cropped_uri"))).into(communityImage);
                imagePath = Utils.getRealPathFromURI(this, Uri.parse(result.getStringExtra("cropped_uri")));
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(getClass().getName(), "Cancelled");
        } else {
            ImageIntentHandler intentHandler =
                    new ImageIntentHandler(getActivity(), mImagePair)
                            .folder(getString(R.string.app_name)).sizePx(1200);

            intentHandler.handleIntent(requestCode, resultCode, result);
            if (mImagePair != null) {
                //imagePath = mImagePair == null ? null : mImagePair.imagePath;
                Intent data = new Intent(this, CropperActivity.class);
                data.putExtra("imagePath", mImagePair.imagePath);
                data.putExtra("isAddCommunity", true);
                startActivityForResult(data, 2222);
            } else {
                showToast(getString(R.string.error));
            }
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

        showDialog();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(AddNewCommunityActivity.this)
                .setMessage(message)
                .setPositiveButton(getString(R.string.ok), okListener)
                .setNegativeButton(getString(R.string.cancel), okListener)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
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
                    showDialog();
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
