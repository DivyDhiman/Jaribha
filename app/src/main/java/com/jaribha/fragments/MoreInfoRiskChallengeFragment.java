package com.jaribha.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.models.ProjectStory;

public class MoreInfoRiskChallengeFragment extends BaseFragment {

    ProjectStory projectStory;

    public static MoreInfoRiskChallengeFragment newInstance(ProjectStory projectStory) {
        Bundle args = new Bundle();
        args.putSerializable("data", projectStory);
        MoreInfoRiskChallengeFragment fragment = new MoreInfoRiskChallengeFragment();
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
        View view = inflater.inflate(R.layout.fragment_more_info_risk_challenge, container, false);
        TextView textDesc = (TextView) view.findViewById(R.id.riskChallenge);
        textDesc.setText(Html.fromHtml(projectStory.riskchallenges));
        return view;
    }
}
