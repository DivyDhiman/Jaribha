package com.jaribha.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.activity.PaymentActivity;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.Rewards;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Utils;

import java.util.ArrayList;

public class RewardsListAdapterNew extends BaseAdapter {

    private Context ctx;

    private ArrayList<Rewards> dataList;

    public RewardsListAdapterNew(Context context, ArrayList<Rewards> data) {
        this.ctx = context;
        this.dataList = data;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Rewards getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.rewards_screen_item, null);
        TextView rewardShip = (TextView) view.findViewById(R.id.rewardShipping);
        TextView title = (TextView) view.findViewById(R.id.rewardTitle);
        TextView rewardType = (TextView) view.findViewById(R.id.rewardType);
        TextView rewardDate = (TextView) view.findViewById(R.id.rewardDate);
        TextView rewardDesc = (TextView) view.findViewById(R.id.rewardDesc);

        final Button btn = (Button) view.findViewById(R.id.supportBtn);

        Rewards rewards = getItem(position);
        rewardShip.setText(rewards.shipping);
        rewardType.setText("(" + rewards.limit + ")");
        rewardDesc.setText(rewards.description);
        String time = Utils.formateDateFromstring(ctx, "MM yyyy", "MMM yyyy", rewards.month + " " + rewards.year);
        rewardDate.setText(time);
        title.setText(ctx.getResources().getString(R.string.support_doller) + rewards.amount + " " + ctx.getResources().getString(R.string.or_more));
        btn.setText(ctx.getResources().getString(R.string.support_doller) + rewards.amount);

        if (!rewards.is_active.equals("true")) {
            btn.setText(ctx.getResources().getString(R.string.sold_out));
            btn.setTextColor(Color.parseColor("#72747A"));
            btn.setBackgroundResource(R.drawable.btn_soldout);
        }

        btn.setTag(position);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) btn.getTag();
                Rewards reward = getItem(pos);

                if (((BaseAppCompatActivity) ctx).getUser() != null) {
                    if (reward.is_active.equals("true")) {
                        if (reward.is_active.equals("true")) {
                            if (JaribhaPrefrence.getPref(ctx, Constants.PROJECT_STATUS, "").equals("complete")) {
                                ((BaseAppCompatActivity) ctx).showCustomeDialog(R.drawable.icon_error, ctx.getString(R.string.campaign_ended),
                                        ctx.getString(R.string.you_cant_support_project_that_ended), ctx.getResources().getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                            } else if (JaribhaPrefrence.getPref(ctx, Constants.PROJECT_STATUS, "").equals("supported") && Utils.isProjectExpired(JaribhaPrefrence.getPref(ctx, Constants.END_DATE, ""))) {
                                ((BaseAppCompatActivity) ctx).showCustomeDialog(R.drawable.icon_error, ctx.getString(R.string.campaign_ended),
                                        ctx.getString(R.string.you_cant_support_project_that_ended), ctx.getResources().getString(R.string.dgts__okay), R.drawable.btn_bg_green);
                            } else {
                                ctx.startActivity(new Intent(ctx, PaymentActivity.class).putExtra(Constants.DATA, reward));
                            }
                        }
                    }
                } else {
                    Intent loginIntent = new Intent(ctx, LoginScreenActivity.class);
                    //loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    ctx.startActivity(loginIntent);
                    //((Activity) ctx).finish();
                }
            }
        });

        return view;
    }
}

