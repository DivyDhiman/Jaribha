package com.jaribha.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.adapters.ProjectHistoryAdapter;
import com.jaribha.base.BaseFragment;
import com.jaribha.models.ProjectHistoryBean;
import com.jaribha.utility.Constants;

import java.util.ArrayList;


public class HistorySupportersFragment extends BaseFragment {

    ListView project_history_list;

    ProjectHistoryAdapter historyAdapter;

    ArrayList<ProjectHistoryBean> projectHistoryList;

    TextView noItems;

    Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    public static HistorySupportersFragment newInstance(ArrayList<ProjectHistoryBean> list) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.DATA, list);
        HistorySupportersFragment fragment = new HistorySupportersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectHistoryList = (ArrayList<ProjectHistoryBean>) getArguments().getSerializable(Constants.DATA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_sponsors, container, false);
        noItems = (TextView) view.findViewById(R.id.noSponsorHis);
        historyAdapter = new ProjectHistoryAdapter(activity, projectHistoryList);

        project_history_list = (ListView) view.findViewById(R.id.project_history_list);
        project_history_list.setVisibility(View.VISIBLE);
        project_history_list.setAdapter(historyAdapter);
        if (projectHistoryList.size() == 0) {
            project_history_list.setVisibility(View.GONE);
            noItems.setVisibility(View.VISIBLE);
            if (isAdded())
                noItems.setText(getString(R.string.no_records));
        }

        return view;
    }
}
