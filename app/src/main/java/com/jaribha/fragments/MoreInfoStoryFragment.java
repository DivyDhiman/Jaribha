package com.jaribha.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.models.ProjectStory;
import com.jaribha.utility.FetchImageUrl;

public class MoreInfoStoryFragment extends BaseFragment {

    ProjectStory projectStory;
    TextView textDesc;
    public String htmlContent;
    int ScreenW,ScreenH;
    Context context;

    public static MoreInfoStoryFragment newInstance(ProjectStory projectStory) {
        Bundle args = new Bundle();
        args.putSerializable("data", projectStory);
        MoreInfoStoryFragment fragment = new MoreInfoStoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectStory = (ProjectStory) getArguments().getSerializable("data");
        context = getActivity().getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_info_story, container, false);
        textDesc = (TextView) view.findViewById(R.id.storyDesc);

        htmlContent = projectStory.prjdesc;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels;
        float dpHeight = displayMetrics.heightPixels;

        ScreenW = (int) dpWidth;
        ScreenH = (int) dpHeight;

        try {
            textDesc.setText(Html.fromHtml(htmlContent, Images, null));
        }
        catch (Exception ex) {

        }
        return view;
    }

    private Html.ImageGetter Images = new Html.ImageGetter() {

        public Drawable getDrawable(String source) {

            Drawable drawable = null;
            FetchImageUrl fiu = new FetchImageUrl(context,source);
            try {
                fiu.execute().get();
                drawable = fiu.GetImage();
            }
            catch (Exception e) {
                drawable = getResources().getDrawable(R.drawable.server_error_placeholder);
            }
            // to display image,center of screen
            int imgH = drawable.getIntrinsicHeight();
            int imgW = drawable.getIntrinsicWidth();
            int padding =20;
            int realWidth = ScreenW-(2*padding);
            int realHeight = imgH * realWidth/imgW;
            drawable.setBounds(padding,0,realWidth ,realHeight);
            return drawable;
        }
    };

    }
