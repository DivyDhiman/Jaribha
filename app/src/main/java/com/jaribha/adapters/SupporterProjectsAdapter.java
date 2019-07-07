package com.jaribha.adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.activity.MyPortfolioActivity;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.GetProjects;
import com.jaribha.utility.ColorTransformation;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SupporterProjectsAdapter extends ArrayAdapter<GetProjects> {

    MyPortfolioActivity myPortfolioActivity;

    public SupporterProjectsAdapter(Context context, List<GetProjects> objects) {
        super(context, R.layout.portfolio_projectby_created_items, objects);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.portfolio_projectby_created_items, parent, false);

            myPortfolioActivity = new MyPortfolioActivity();

            holder.projectImage = (ImageView) convertView.findViewById(R.id.projectImage);
            holder.projectStatus = (TextView) convertView.findViewById(R.id.projectStatus);
            holder.projectHeader = (TextView) convertView.findViewById(R.id.projectHeader);
            holder.tv_userN = (TextView) convertView.findViewById(R.id.tv_userN);
            holder.tv_Category = (TextView) convertView.findViewById(R.id.tv_Category);
            holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
            holder.tv_supported = (TextView) convertView.findViewById(R.id.tv_supported);
            holder.tv_goal = (TextView) convertView.findViewById(R.id.tv_goal);
            holder.edit = (LinearLayout) convertView.findViewById(R.id.editLayout);
            holder.delete = (LinearLayout) convertView.findViewById(R.id.deleteLayout);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
            holder.tv_days_left = (TextView) convertView.findViewById(R.id.tv_days_left);

            holder.deleteLayout = (LinearLayout) convertView.findViewById(R.id.deleteLayout);
            holder.img_category = (ImageView) convertView.findViewById(R.id.img_category);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.edit.setVisibility(View.GONE);
        holder.delete.setVisibility(View.GONE);
        final GetProjects creatorsData = getItem(position);
        switch (creatorsData.status) {
            case "supported":
                holder.projectStatus.setVisibility(View.VISIBLE);
                holder.projectStatus.setText(getContext().getString(R.string.successfully_supported));
                holder.projectStatus.setBackgroundResource(R.drawable.green_strip_transparent);
                break;
            case "complete":
                holder.projectStatus.setVisibility(View.VISIBLE);
                holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
                holder.projectStatus.setText(getContext().getString(R.string.campaign_ended));
                break;
//            case "submit":
//                //holder.projectStatus.setBackgroundResource(R.drawable.green_strip_transparent);
//                // holder.projectStatus.setText(getContext().getString(R.string.successfully_submitted));
//                break;
//            case "incomplete":
//                //holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
//                //holder.projectStatus.setText(getContext().getString(R.string.incomplete));
//                break;
            default:
                holder.projectStatus.setVisibility(View.INVISIBLE);
                break;
        }

        Picasso.with(getContext())
                .load(Utils.getCategoryImage(creatorsData.category_id, false))
                .transform(new ColorTransformation(ContextCompat.getColor(getContext(), R.color.transform_color)))
                .into(holder.img_category);

        holder.projectHeader.setText(creatorsData.title);

        holder.tv_goal.setText(String.format("$%s", creatorsData.goal));

        holder.tv_days_left.setText(creatorsData.period);

        // holder.tv_userN.setText("By " + ((BaseAppCompatActivity) getContext()).getUser().name);
        //holder.tv_location.setText(((BaseAppCompatActivity) getContext()).getUser().cityname + ", " + ((BaseAppCompatActivity) getContext()).getUser().countryname);

        ((BaseAppCompatActivity) getContext()).displayImage(getContext(), holder.projectImage, creatorsData.image,
                ContextCompat.getDrawable(getContext(), R.drawable.server_error_placeholder));


        if (!TextUtils.isNullOrEmpty(creatorsData.total_support_amount) && !TextUtils.isNullOrEmpty(creatorsData.goal)) {
//            double supportAmt = Double.parseDouble(creatorsData.total_support_amount);
//            double goalAmt = Double.parseDouble(creatorsData.goal);
//
//            if (Utils.isProjectExpired(creatorsData.enddate)) {
//                if ((supportAmt < goalAmt) || (supportAmt >= goalAmt)) {
//                    holder.projectStatus.setText(getContext().getString(R.string.campaign_ended));
//                    holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
//                }
//            }
            holder.progressBar.setProgress(((BaseAppCompatActivity) getContext()).getProgressPercent(Double.parseDouble(creatorsData.total_support_amount), Double.parseDouble(creatorsData.goal)));
        }

        if (holder.projectStatus.getText().toString().equals("publish")) {
            holder.projectStatus.setBackgroundResource(R.drawable.green_strip_transparent);
        }

//        double supportedAmount = 0;
//        try {
//            if (!TextUtils.isNullOrEmpty(creatorsData.total_support_amount))
//                supportedAmount = Double.parseDouble(creatorsData.total_support_amount);
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//
//        int goal = 0;
//
//        if (!TextUtils.isNullOrEmpty(creatorsData.goal))
//            goal = Integer.parseInt(creatorsData.goal);
//
//        double Supported = (supportedAmount * 100) / goal;

        holder.tv_supported.setText(Utils.getSupportedAmount(creatorsData.total_support_amount, creatorsData.goal) + "%");

        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((BaseAppCompatActivity) getContext()).isInternetConnected()) {
//                    DeleteArraylistPos callBack = (DeleteArraylistPos) getContext();
//                    callBack.setItemPosition(position);
                    dialogWithTwoButton(creatorsData);
                } else {
                    ((BaseAppCompatActivity) getContext()).showNetworkDialog();
                }
            }
        });

        return convertView;
    }

    public void dialogWithTwoButton(final GetProjects creatorsData) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_with_two_btn);
        dialog.setCanceledOnTouchOutside(true);

        Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        Button btnNO = (Button) dialog.findViewById(R.id.btn_no);
        LinearLayout dialog_main_container = (LinearLayout) dialog.findViewById(R.id.dialog_main_container);

        if (((BaseAppCompatActivity) getContext()).isTablet()) {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(getContext()) * 40) / 100;
        } else {
            dialog_main_container.getLayoutParams().width = (Utils.getDeviceWidth(getContext()) * 80) / 100;
        }

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                projecDeleted(creatorsData);
                dialog.dismiss();
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

    private void projecDeleted(final GetProjects creatorsData) {
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
                    jsonObject.put("project_id", creatorsData.id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return new JSONParser().getJsonObjectFromUrl1(Urls.DELETE_PROJECT, jsonObject);
            }

            @Override
            protected void onPostExecute(final JSONObject responce) {
                ((BaseAppCompatActivity) getContext()).hideLoading();
                if (responce != null) {
                    try {
                        JSONObject jsonObject = responce.getJSONObject("result");
                        Boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("msg");
                        if (status) {

                            remove(creatorsData);
                            //myPortfolioActivity.creatorsDataArrayList.remove(pos);
                            notifyDataSetChanged();
                        } else {
                            switch (message) {
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
            protected void onCancelled() {
                ((BaseAppCompatActivity) getContext()).hideLoading();
            }
        }.execute();
    }

    class ViewHolder {
        ImageView projectImage, img_category;
        TextView projectStatus;
        TextView projectHeader;
        TextView tv_userN;
        TextView tv_Category;
        TextView tv_location;
        TextView tv_supported;
        TextView tv_goal;
        TextView tv_days_left;
        LinearLayout deleteLayout;
        ProgressBar progressBar;
        LinearLayout edit;
        LinearLayout delete;
    }
}
