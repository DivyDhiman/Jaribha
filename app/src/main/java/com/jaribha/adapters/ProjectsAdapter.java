package com.jaribha.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.activity.LoginScreenActivity;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.interfaces.AddFavourite;
import com.jaribha.models.GetProjects;
import com.jaribha.server_communication.AddFavouriteTask;
import com.jaribha.utility.ColorTransformation;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProjectsAdapter extends ArrayAdapter<GetProjects> {

    public ProjectsAdapter(Context context, List<GetProjects> objects) {
        super(context, R.layout.project_list_item_card_view, objects);
    }

    private class ViewHolder {
        ImageView projectImage;
        TextView projectHeader;
        TextView projectType;
        TextView projectCategory;
        TextView projectLocation;
        TextView projectPeriod;
        TextView goalAmount;
        TextView supportPercent;
        TextView status;
        ProgressBar progressBar;
        ImageView iv_add_favourite, img_category;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.project_list_item_card_view, parent, false);
            holder = new ViewHolder();
            holder.projectHeader = (TextView) convertView.findViewById(R.id.projectHeader);
            holder.projectType = (TextView) convertView.findViewById(R.id.projectType);
            holder.projectCategory = (TextView) convertView.findViewById(R.id.projectCategory);
            holder.projectLocation = (TextView) convertView.findViewById(R.id.projectLocation);
            holder.projectImage = (ImageView) convertView.findViewById(R.id.projectImage);
            holder.projectPeriod = (TextView) convertView.findViewById(R.id.projectPeriod);
            holder.goalAmount = (TextView) convertView.findViewById(R.id.goalAmount);
            holder.supportPercent = (TextView) convertView.findViewById(R.id.supportPercent);
            holder.status = (TextView) convertView.findViewById(R.id.projectStatus);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.projectProgress);
            holder.iv_add_favourite = (ImageView) convertView.findViewById(R.id.iv_add_favourite);
            holder.img_category = (ImageView) convertView.findViewById(R.id.img_category);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GetProjects rowItem = getItem(position);

        String proHeader = rowItem.title;
        String proType = rowItem.project_by;
        String proCategory = rowItem.category_name;
        String proLocation = rowItem.city_name + "," + rowItem.country_name;
        String proImage = rowItem.image_url;

        holder.projectHeader.setText(proHeader);

        holder.projectType.setText(String.format("%s%s", getContext().getString(R.string.by), proType));

        holder.projectCategory.setText(proCategory);

        Picasso.with(getContext())
                .load(Utils.getCategoryImage(rowItem.category_id, false))
                .transform(new ColorTransformation(ContextCompat.getColor(getContext(), R.color.transform_color)))
                .into(holder.img_category);

        holder.projectLocation.setText(proLocation);

        holder.projectPeriod.setText(rowItem.days_left);

        holder.goalAmount.setText(String.format("$%s", rowItem.goal));

        switch (rowItem.status) {
            case "supported":
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setText(getContext().getString(R.string.successfully_supported));
                holder.status.setBackgroundResource(R.drawable.green_strip_transparent);
                break;
            case "complete":
                holder.status.setVisibility(View.VISIBLE);
                holder.status.setBackgroundResource(R.drawable.red_strip);
                holder.status.setText(getContext().getString(R.string.campaign_ended));
                break;

            default:
                holder.status.setVisibility(View.INVISIBLE);
                break;
        }


        if (!TextUtils.isNullOrEmpty(rowItem.total_support_amount) && !TextUtils.isNullOrEmpty(rowItem.goal)) {

            holder.progressBar.setProgress(((BaseAppCompatActivity) getContext()).getProgressPercent(Double.parseDouble(rowItem.total_support_amount), Double.parseDouble(rowItem.goal)));

            holder.supportPercent.setText(String.format("%s%%", ((BaseAppCompatActivity) getContext()).getSupportPercentage(Double.parseDouble(rowItem.total_support_amount), Double.parseDouble(rowItem.goal))));
        }

        if (TextUtils.isNullOrEmpty(proImage)) {
            holder.projectImage.setImageResource(R.drawable.server_error_placeholder);
        } else {
            ((BaseAppCompatActivity) getContext()).displayImage(getContext(), holder.projectImage, proImage,
                    ContextCompat.getDrawable(getContext(), R.drawable.server_error_placeholder));
        }

        if (rowItem.is_favourite) {
            holder.iv_add_favourite.setImageResource(R.drawable.ic_fav_selected);
        } else {
            holder.iv_add_favourite.setImageResource(R.drawable.fav_button);
        }

        holder.iv_add_favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseAppCompatActivity) getContext()).getUser() != null) {
                    if (((BaseAppCompatActivity) getContext()).isInternetConnected()) {
                        manageFavourite(rowItem);
                    } else {
                        ((BaseAppCompatActivity) getContext()).showNetworkDialog();
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

    private void manageFavourite(final GetProjects object) {
        ((BaseAppCompatActivity) getContext()).showLoading();
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("apikey", Urls.API_KEY);
            jsonObject.put("user_id", ((BaseAppCompatActivity) getContext()).getUser().id);
            jsonObject.put("user_token", ((BaseAppCompatActivity) getContext()).getUser().user_token);
            jsonObject.put("project_id", object.id);
            AddFavouriteTask addFavouriteTask = new AddFavouriteTask(jsonObject, new AddFavourite() {
                @Override
                public void OnSuccess(JSONObject response) {
                    if (response != null) {
                        ((BaseAppCompatActivity) getContext()).hideLoading();
                        try {
                            JSONObject jsonObject = response.getJSONObject("result");
                            Boolean status = jsonObject.getBoolean("status");
                            String msg = jsonObject.optString("msg");
                            if (status) {
                                object.is_favourite = msg.equalsIgnoreCase("Favourite Added");
                                if (object.is_favourite) {
                                    Utils.updateFavoriteCount(getContext(), 1);
                                } else {
                                    Utils.updateFavoriteCount(getContext(), -1);
                                }
                                notifyDataSetChanged();
                            } else {
                                switch (msg) {
                                    case "Data Not Found":
                                        ((BaseAppCompatActivity) getContext()).showDataNotFoundDialog();
                                        break;
                                    case "User Not Found":
                                        ((BaseAppCompatActivity) getContext()).showSessionDialog();
                                        break;
                                    default:
                                        ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                                        break;
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
                public void OnFail() {
                    ((BaseAppCompatActivity) getContext()).hideLoading();
                    ((BaseAppCompatActivity) getContext()).showServerDialogDialog();
                }
            });

            addFavouriteTask.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
