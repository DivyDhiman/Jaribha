package com.jaribha.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.activity.EditProjectActivity;
import com.jaribha.base.BaseFragment;
import com.jaribha.utility.Utils;

import java.util.regex.Pattern;

public class EditProjectGuideLineFragment extends BaseFragment implements View.OnClickListener {

    private Button btn_start_new_project;

    private TextView tv_more_info1, tv_more_info2, text1, text3;

    private TextView text2, tv_point1, tv_point1_desc, tv_point2, tv_point3, tv_point3_desc;

    private CheckBox termsCheckBox;

    private boolean checkboxChecked = false;

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project_guidelines, null);
        btn_start_new_project = (Button) view.findViewById(R.id.btn_start_new_project);

        btn_start_new_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkboxChecked) {
                    ((EditProjectActivity) activity).firstLoad = false;
                    if (!isTabletDevice())
                        ((EditProjectActivity) activity).displayTab(1, false);
                    else {
                        ((EditProjectActivity) activity).displayFragment(1, false);
                    }
                } else {
                    if (isAdded())
                        Utils.showDataToast(getString(R.string.please_accept_terms_and_condition_privacy_policy), activity);
                }
            }
        });


        if (!isTabletDevice()) {
            tv_more_info2 = (TextView) view.findViewById(R.id.tv_more_info2);
            tv_more_info2.setOnClickListener(this);

            tv_more_info1 = (TextView) view.findViewById(R.id.tv_more_info1);
            tv_more_info1.setOnClickListener(this);
        } else {
            tv_point1 = (TextView) view.findViewById(R.id.tv_point1);
            tv_point1.setOnClickListener(this);

            tv_point1_desc = (TextView) view.findViewById(R.id.tv_point1_desc);

            tv_point2 = (TextView) view.findViewById(R.id.tv_point2);
            tv_point2.setOnClickListener(this);

            tv_point3 = (TextView) view.findViewById(R.id.tv_point3);
            tv_point3.setOnClickListener(this);

            tv_point3_desc = (TextView) view.findViewById(R.id.tv_point3_desc);
        }

        text1 = (TextView) view.findViewById(R.id.text1);
        if (isAdded())
            text1.setText(Html.fromHtml(getString(R.string.should_you_be_able_to_meet_your_project_goal) + getString(R.string.terms) + "</u></font>. " + getString(R.string.should_you_unsuccessfully_meet_your_project_goal) + " <br/><br/><br/> " + getString(R.string.supporting_a_project_multiple_times)));

        text1.setLinkTextColor(Color.parseColor("#F48481"));
        Pattern pattern1 = Pattern.compile("Terms and conditions");
        Linkify.addLinks(text1, pattern1, "terms-activity://");

        text2 = (TextView) view.findViewById(R.id.text2);
        if (isAdded())
            text2.setText(Html.fromHtml(getString(R.string.jaribha_welcomes_new_project_categories) + getString(R.string.support_jaribha_id) + "</u></font> ." + getString(R.string.We_will_review_your_suggested_category)));
        text2.setLinkTextColor(Color.parseColor("#F48481"));
        Linkify.addLinks(text2 , Linkify.EMAIL_ADDRESSES);

        text3 = (TextView) view.findViewById(R.id.text3);
        if (isAdded())
            text3.setText(Html.fromHtml(getString(R.string.projects_falling_under_categories) + "<br/><br/>" + getString(R.string.a_project_or_idea_could_be_anything_from_starting) + "<br/><br/>" + getString(R.string.jaribha_will_continue_to_add_or_delete_categories)));

        termsCheckBox = (CheckBox) view.findViewById(R.id.termsCheckBox);
        if (isAdded()) {
            String styledText = getString(R.string.i_understand_that_in_order_to_launch) + " <font color='red'>" + getString(R.string.terms) + "</font> & <font color='red'>" + getString(R.string.privacy) + ".</font>";
           // String styledText = getString(R.string.i_understand_that_in_order_to_launch) + getString(R.string.terms) + getString(R.string.privacy) + ".</font>";
            termsCheckBox.setText(Html.fromHtml(styledText));
            termsCheckBox.setLinkTextColor(Color.RED);
            Linkify.addLinks(termsCheckBox, pattern1, "terms-activity://");

            Pattern pattern3 = Pattern.compile("Privacy Policy");
            Linkify.addLinks(termsCheckBox, pattern3, "privacy-activity://");
        }
        termsCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkboxChecked = isChecked;
            }
        });

        btn_start_new_project.setText(getString(R.string.edit_project));

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_more_info1:
                if (text1.getVisibility() == View.VISIBLE) {
                    if (isAdded())
                        tv_more_info1.setText(getString(R.string.more_info_plus));
                    text1.setVisibility(View.GONE);
                } else {
                    if (isAdded())
                        tv_more_info1.setText(getString(R.string.more_info_minus));
                    text1.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.tv_more_info2:
                if (text3.getVisibility() == View.VISIBLE) {
                    if (isAdded())
                        tv_more_info2.setText(getString(R.string.more_info_plus));
                    text3.setVisibility(View.GONE);
                } else {
                    if (isAdded())
                        tv_more_info2.setText(getString(R.string.more_info_minus));
                    text3.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.tv_point1:
                if (tv_point1_desc.getVisibility() == View.VISIBLE) {
                    tv_point1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);
                    tv_point2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);
                    tv_point3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);

                    tv_point1_desc.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);
                    tv_point3_desc.setVisibility(View.GONE);
                } else {
                    tv_point1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_remove_circle, 0, 0, 0);
                    tv_point2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);
                    tv_point3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);

                    tv_point1_desc.setVisibility(View.VISIBLE);
                    text2.setVisibility(View.GONE);
                    tv_point3_desc.setVisibility(View.GONE);
                }

                break;

            case R.id.tv_point2:
                if (text2.getVisibility() == View.VISIBLE) {
                    tv_point1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);
                    tv_point2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);
                    tv_point3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);

                    tv_point1_desc.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);
                    tv_point3_desc.setVisibility(View.GONE);
                } else {
                    tv_point2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_remove_circle, 0, 0, 0);
                    tv_point1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);
                    tv_point3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);

                    tv_point1_desc.setVisibility(View.GONE);
                    text2.setVisibility(View.VISIBLE);
                    tv_point3_desc.setVisibility(View.GONE);
                }
                break;

            case R.id.tv_point3:
                if (tv_point3_desc.getVisibility() == View.VISIBLE) {
                    tv_point1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);
                    tv_point2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);
                    tv_point3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);

                    tv_point1_desc.setVisibility(View.GONE);
                    text2.setVisibility(View.GONE);
                    tv_point3_desc.setVisibility(View.GONE);
                } else {
                    tv_point3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_remove_circle, 0, 0, 0);
                    tv_point2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);
                    tv_point1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_content_add_circle_outline, 0, 0, 0);

                    tv_point3_desc.setVisibility(View.VISIBLE);
                    text2.setVisibility(View.GONE);
                    tv_point1_desc.setVisibility(View.GONE);
                }
                break;

            default:
                break;
        }
    }
}
