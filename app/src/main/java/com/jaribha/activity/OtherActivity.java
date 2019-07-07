package com.jaribha.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.adapters.OtherActivityAdapter;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.models.ActivityData;
import com.jaribha.utility.Constants;

import java.util.ArrayList;

public class OtherActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView iv_close;

    TextView tv_title;

    BezelImageView img_user_image;

    ListView projectsActivityList;

    ArrayList<ActivityData> activityDataArrayList;

    OtherActivityAdapter activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_projectby_activity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.activity));

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.VISIBLE);
        displayUserImage(img_user_image);
        Intent intent = getIntent();
        activityDataArrayList = (ArrayList<ActivityData>) intent.getSerializableExtra(Constants.DATA);

        projectsActivityList = (ListView) findViewById(R.id.projectsActivityList);
        activityAdapter = new OtherActivityAdapter(getActivity(), activityDataArrayList);

        projectsActivityList.setAdapter(activityAdapter);
        activityAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_close:
                finish();
                break;

            default:
                break;
        }
    }
}
