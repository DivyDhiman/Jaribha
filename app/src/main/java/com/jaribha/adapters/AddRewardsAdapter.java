package com.jaribha.adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.Rewards;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AddRewardsAdapter extends ArrayAdapter<Rewards> {

    public AddRewardsAdapter(Context context, List<Rewards> objects) {
        super(context, R.layout.item_add_rewards, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_add_rewards, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.tv_limit = (TextView) convertView.findViewById(R.id.tv_limit);

            viewHolder.tv_amount = (TextView) convertView.findViewById(R.id.tv_amount);

            viewHolder.tv_description = (TextView) convertView.findViewById(R.id.tv_description);

            viewHolder.tv_estimate_delivery = (TextView) convertView.findViewById(R.id.tv_estimate_delivery);

            viewHolder.tv_shipping = (TextView) convertView.findViewById(R.id.tv_shipping);

            viewHolder.deleteReward = (ImageView) convertView.findViewById(R.id.deleteReward);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Rewards rewards = getItem(position);

        if (rewards.limit.equalsIgnoreCase("limit")) {
            viewHolder.tv_limit.setText(String.format("%s", rewards.limit_number + getContext().getString(R.string.sponsor_limited)));
        } else {
            viewHolder.tv_limit.setText(getContext().getString(R.string.supporter_unlimited));
        }

        viewHolder.tv_amount.setText(getContext().getString(R.string.Reward_dollar) + rewards.amount + getContext().getString(R.string.or_more));

        viewHolder.tv_description.setText(rewards.description);

        viewHolder.tv_estimate_delivery.setText(String.format("%s %s", rewards.month, rewards.year));

        //International
        if (rewards.shipping.equalsIgnoreCase(getContext().getString(R.string.shipping_worldwide))) {
            viewHolder.tv_shipping.setText(getContext().getString(R.string.international));
        } else if (rewards.shipping.equalsIgnoreCase(getContext().getString(R.string.no_shipping_involved))) {
            viewHolder.tv_shipping.setError(getContext().getString(R.string.none));
        } else {
            viewHolder.tv_shipping.setText(getContext().getString(R.string.domestic));
        }

        viewHolder.deleteReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWithTwoButton(rewards);
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView tv_amount, tv_limit, tv_description, tv_estimate_delivery, tv_shipping;
        ImageView deleteReward;
    }

    public void dialogWithTwoButton(final Rewards rewards) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_reward_dialog);
        dialog.setCanceledOnTouchOutside(true);

        Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        Button btnNO = (Button) dialog.findViewById(R.id.btn_no);
        TextView textView = (TextView) dialog.findViewById(R.id.tv_dialog_subtitle);
        textView.setText(getContext().getString(R.string.are_you_sure_to_delete_this_reward));
        LinearLayout dialog_main_container = (LinearLayout) dialog.findViewById(R.id.dialog_main_container);

        if (((BaseAppCompatActivity) getContext()).isTablet()) {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(getContext()) * 40) / 100;
        } /*else {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(getContext()) * 80) / 100;
        }*/

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteRewards(rewards);
            }
        });
        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void deleteRewards(final Rewards rewards) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ((BaseAppCompatActivity) getContext()).showLoading();
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("apikey", Urls.API_KEY);
                    jsonObject.put("user_id", ((BaseAppCompatActivity) getContext()).getUser().id);
                    jsonObject.put("user_token", ((BaseAppCompatActivity) getContext()).getUser().user_token);
                    jsonObject.put("reward_id", rewards.id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.DELETE_REWARD, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject response) {
                ((BaseAppCompatActivity) getContext()).hideLoading();
                if (response != null) {
                    try {
                        JSONObject jsonObject = response.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("msg");
                        if (status) {
                            remove(rewards);
                            notifyDataSetChanged();
                        } else {
                            if (message.equalsIgnoreCase("User Not Found")) {
                                ((BaseAppCompatActivity) getContext()).showSessionDialog();
                            } else if (message.equalsIgnoreCase("Data Not Found")) {
                                ((BaseAppCompatActivity) getContext()).showDataNotFoundDialog();
                            } else {
                                ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                    }
                } else {
                    ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                }
            }

            @Override
            protected void onCancelled() {
                ((BaseAppCompatActivity) getContext()).hideLoading();
            }
        }.execute();
    }
}
