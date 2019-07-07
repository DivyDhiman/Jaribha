package com.jaribha.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaribha.R;
import com.jaribha.adapters.MoreInfoQAAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.models.AddFAQ;
import com.jaribha.models.ProjectStory;

import java.util.ArrayList;
import java.util.List;

public class MoreInfoFAQsFragment extends BaseFragment {

    ProjectStory projectStory;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public static MoreInfoFAQsFragment newInstance(ProjectStory projectStory) {
        Bundle args = new Bundle();
        args.putSerializable("data", projectStory);
        MoreInfoFAQsFragment fragment = new MoreInfoFAQsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectStory = (ProjectStory) getArguments().getSerializable("data");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more_info_faqs, container, false);
        ListView list_que_ans = (ListView) view.findViewById(R.id.list_que_ans);
        try {
            ArrayList<AddFAQ> faqList = new Gson().fromJson(projectStory.question_answers,
                    new TypeToken<List<AddFAQ>>() {
                    }.getType());
            list_que_ans.setAdapter(new MoreInfoQAAdapter(activity, faqList));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}
