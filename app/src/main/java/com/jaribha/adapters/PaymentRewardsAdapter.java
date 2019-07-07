package com.jaribha.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.models.Rewards;

import java.util.List;

public class PaymentRewardsAdapter extends ArrayAdapter<Rewards> {

    public PaymentRewardsAdapter(Context context, List<Rewards> objects) {
        super(context, R.layout.item_payment_rewards, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_payment_rewards, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.tv_rewards = (TextView) convertView.findViewById(R.id.tv_rewards);

            viewHolder.tv_amount= (TextView) convertView.findViewById(R.id.tv_amount);

            viewHolder.iv_check = (ImageView) convertView.findViewById(R.id.iv_check);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Rewards rewards = getItem(position);

        viewHolder.tv_amount.setText(String.format("$ %s +", rewards.amount));

        if (rewards.selected) {
            viewHolder.iv_check.setImageResource(R.drawable.radio_red_sel);
        } else {
            viewHolder.iv_check.setImageResource(R.drawable.radio_red_unselected);
        }

        viewHolder.tv_rewards.setText(rewards.description);

        return convertView;
    }

    class ViewHolder {
        TextView tv_rewards,tv_amount;
        ImageView iv_check;
    }
}
