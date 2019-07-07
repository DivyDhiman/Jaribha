package com.jaribha.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.adapters.SupporterProjectsAdapter;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.customviews.BezelImageView;
import com.jaribha.models.GetProjects;
import com.jaribha.utility.Constants;

import java.util.ArrayList;

public class SupporterCreatedActivity extends BaseAppCompatActivity implements View.OnClickListener {

    ImageView iv_close, iv_share, searchImg;

    TextView tv_title;

    BezelImageView img_user_image;

    ListView projectListView;

    GridView projectGrid;

    SupporterProjectsAdapter adapter;

    ArrayList<GetProjects> creatorsDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio_projectby_created);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        searchImg = (ImageView) findViewById(R.id.iv_search);
        searchImg.setVisibility(View.VISIBLE);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setVisibility(View.VISIBLE);
        tv_title.setText(getString(R.string.created));

        iv_close = (ImageView) findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setImageResource(R.drawable.btn_back);
        iv_close.setOnClickListener(this);

        img_user_image = (BezelImageView) findViewById(R.id.img_user_image);
        img_user_image.setVisibility(View.GONE);
        displayUserImage(img_user_image);

        iv_share = (ImageView) findViewById(R.id.iv_share);
        iv_share.setVisibility(View.GONE);

        Intent intent = getIntent();
        creatorsDataArrayList = (ArrayList<GetProjects>) intent.getSerializableExtra(Constants.DATA);

        adapter = new SupporterProjectsAdapter(getActivity(), creatorsDataArrayList);

        if (isTablet()) {
            projectGrid = (GridView) findViewById(R.id.projectsGrid);

            projectGrid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            projectListView = (ListView) findViewById(R.id.projectsList);
            adapter = new SupporterProjectsAdapter(getActivity(), creatorsDataArrayList);
            projectListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
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
