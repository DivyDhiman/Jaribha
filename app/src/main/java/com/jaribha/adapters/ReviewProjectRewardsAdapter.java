package com.jaribha.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.activity.PaymentActivity;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.GetProjects;
import com.jaribha.models.Rewards;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Utils;

import java.util.List;

public class ReviewProjectRewardsAdapter extends ArrayAdapter<Rewards> {

    GetProjects projectData;

    public ReviewProjectRewardsAdapter(Context context, List<Rewards> objects, GetProjects project) {
        super(context, R.layout.item_review_rewards, objects);
        this.projectData = project;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_review_rewards, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.rewardHeaderTab = (Button) convertView.findViewById(R.id.rewardHeaderTab);

            viewHolder.supportBtnTab = (Button) convertView.findViewById(R.id.supportBtnTab);

            viewHolder.rewardDescTab = (TextView) convertView.findViewById(R.id.rewardDescTab);

            viewHolder.rewardTitleTab = (TextView) convertView.findViewById(R.id.rewardTitleTab);

            viewHolder.rewardTypeTab = (TextView) convertView.findViewById(R.id.rewardTypeTab);

            viewHolder.tv_estimate_delivery = (TextView) convertView.findViewById(R.id.tv_estimate_delivery);

            viewHolder.tv_shipping = (TextView) convertView.findViewById(R.id.tv_shipping);

            viewHolder.supportDetailLayout1 = (LinearLayout) convertView.findViewById(R.id.supportDetailLayout1);
            viewHolder.supportDetailLayout1.setVisibility(View.GONE);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Rewards rewards = getItem(position);

        viewHolder.rewardHeaderTab.setText(getContext().getString(R.string.support_doller) + rewards.amount + getContext().getString(R.string.or_more));

        viewHolder.supportBtnTab.setText(getContext().getString(R.string.support_doller) + rewards.amount + getContext().getString(R.string.or_more));

        viewHolder.rewardDescTab.setText(rewards.description);

        if (rewards.limit.equalsIgnoreCase("limit")) {
            viewHolder.rewardTypeTab.setText(String.format("%s ", rewards.limit_number + getContext().getString(R.string.sponsor_limited)));
        } else {
            viewHolder.rewardTypeTab.setText(getContext().getString(R.string.supporter_unlimited));
        }

        viewHolder.tv_estimate_delivery.setText(String.format("%s %s", rewards.month, rewards.year));

        //International
        if (rewards.shipping.equalsIgnoreCase(getContext().getString(R.string.shipping_worldwide))) {
            viewHolder.tv_shipping.setText(getContext().getString(R.string.international));
        } else if (rewards.shipping.equalsIgnoreCase(getContext().getString(R.string.no_shipping_involved))) {
            viewHolder.tv_shipping.setError(getContext().getString(R.string.none));
        } else {
            viewHolder.tv_shipping.setText(getContext().getString(R.string.domestic));
        }

        if (rewards.selected) {
            viewHolder.supportDetailLayout1.setVisibility(View.VISIBLE);
            viewHolder.rewardHeaderTab.setVisibility(View.GONE);
        } else {
            viewHolder.supportDetailLayout1.setVisibility(View.GONE);
            viewHolder.rewardHeaderTab.setVisibility(View.VISIBLE);
        }

        viewHolder.rewardHeaderTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewards.selected = true;
                notifyDataSetChanged();
            }
        });

        viewHolder.rewardTitleTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rewards.selected = false;
                notifyDataSetChanged();
            }
        });

        viewHolder.supportBtnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseAppCompatActivity) getContext()).getUser() != null) {
                    if (projectData != null)
                        if (projectData.status.equals("complete")) {
                            ((BaseAppCompatActivity) getContext()).showCustomeDialog(R.drawable.icon_error,
                                    getContext().getString(R.string.campaign_ended),
                                    getContext().getString(R.string.you_cant_support),
                                    getContext().getString(R.string.dgts__okay),
                                    R.drawable.btn_bg_green);
                        } else if (projectData.status.equals("supported") && Utils.isProjectExpired(projectData.enddate)) {
                            ((BaseAppCompatActivity) getContext()).showCustomeDialog(R.drawable.icon_error,
                                    getContext().getString(R.string.support_project),
                                    getContext().getString(R.string.you_cant_support),
                                    getContext().getString(R.string.dgts__okay),
                                    R.drawable.btn_bg_green);
                        } else {
                            if (rewards.is_active.equals("true") && !rewards.user_id.equals(((BaseAppCompatActivity) getContext()).getUser().id)) {
                                if (rewards.is_active.equals("true"))
                                    getContext().startActivity(new Intent(getContext(), PaymentActivity.class).putExtra(Constants.DATA, rewards));
                            }
                        }
                } else {
                    Intent loginIntent = new Intent(getContext(), LoginScreenActivity.class);
                    //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(loginIntent);
                    //((Activity) getContext()).overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);
                    //((Activity) getContext()).finish();
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        LinearLayout supportDetailLayout1;
        Button rewardHeaderTab, supportBtnTab;
        TextView rewardTitleTab, rewardTypeTab, rewardDescTab, tv_estimate_delivery, tv_shipping;
    }
}
