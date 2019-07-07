package com.jaribha.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Utils;

import java.io.File;


public class CropperActivity extends BaseAppCompatActivity implements View.OnClickListener {

    CropImageView cropImageView;

    Uri uri;

    ImageView iv_close;

    TextView tv_title, tv_sub_title, tv_apply;

    ViewPager viewPager;

    BezelImageView img_user_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cropper_view);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.GONE);
        displayUserImage(img_user_image);
        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setImageResource(R.drawable.close_icon);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(this);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText("");

        tv_sub_title = (TextView) findViewById(R.id.tv_sub_title);
        tv_sub_title.setVisibility(View.GONE);
        tv_sub_title.setText(JaribhaPrefrence.getPref(this, Constants.FILTER_COUNT, "0") + " " + getString(R.string.results));

        tv_apply = (TextView) findViewById(R.id.tv_apply);
        tv_apply.setVisibility(View.VISIBLE);
        tv_apply.setOnClickListener(this);
        tv_apply.setText(getString(R.string.apply));
        cropImageView = (CropImageView) findViewById(R.id.cropImageView);

        showLoading();
        cropImageView.setDebug(false);
        if (getIntent().getExtras() != null) {

            String uriPath = getIntent().getStringExtra("imagePath");
            uri = Uri.fromFile(new File(uriPath));
            cropImageView.startLoad(uri, mLoadCallback);
            if (getIntent().getBooleanExtra("isEditProfile", false) || getIntent().getBooleanExtra("isAddCommunity", false) || getIntent().getBooleanExtra("aboutYou", false)) {
                cropImageView.setCropMode(CropImageView.CropMode.SQUARE);
            } else if (getIntent().getBooleanExtra("isBasic", false)) {
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_4_3);
            }
        }
    }

    // Callbacks ///////////////////////////////////////////////////////////////////////////////////

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {
            hideLoading();
        }

        @Override
        public void onError() {
            hideLoading();
        }
    };

    public Uri createSaveUri() {
        File file = new File(getActivity().getCacheDir(), "cropped"+System.currentTimeMillis());

        return Uri.fromFile(file);
    }

    private final CropCallback mCropCallback = new CropCallback() {
        @Override
        public void onSuccess(Bitmap cropped) {
           // hideLoading();
        }

        @Override
        public void onError() {
            hideLoading();
        }
    };

    private final SaveCallback mSaveCallback = new SaveCallback() {
        @Override
        public void onSuccess(Uri outputUri) {

            hideLoading();
            if (getIntent().getBooleanExtra("isEditProfile", false) || getIntent().getBooleanExtra("aboutYou", false)) {
                if (Utils.checkForValidUserImage(CropperActivity.this, Utils.getRealPathFromURI(CropperActivity.this, outputUri))) {
                    Intent data = new Intent();
                    data.putExtra("cropped_uri", outputUri.toString());
                    setResult(RESULT_OK, data);
                    finish();
                }
            } else if (getIntent().getBooleanExtra("isAddCommunity", false)) {
                if (Utils.checkForCommunityImage(CropperActivity.this, Utils.getRealPathFromURI(CropperActivity.this, outputUri))) {
                    Intent data = new Intent();
                    data.putExtra("cropped_uri", outputUri.toString());
                    setResult(RESULT_OK, data);
                    finish();
                }
            } else if (getIntent().getBooleanExtra("isBasic", false)) {
                if (Utils.checkForValidProjectImage(CropperActivity.this, Utils.getRealPathFromURI(CropperActivity.this, outputUri))) {
                    Intent data = new Intent();
                    data.putExtra("cropped_uri", outputUri.toString());
                    setResult(RESULT_OK, data);
                    finish();
                }
            }


        }

        @Override
        public void onError() {

            hideLoading();
        }
    };

    @Override
    public void onClick(View v) {

        // Intent intent;
        switch (v.getId()) {

            case R.id.iv_close:
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.tv_apply:
                showLoading();
                cropImageView.startCrop(createSaveUri(), mCropCallback, mSaveCallback);

                break;

            default:
                break;

        }
    }
}
