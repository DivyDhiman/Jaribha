package com.jaribha.adapters;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djhs16.net.JSONParser;
import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.GetProjects;
import com.jaribha.utility.ColorTransformation;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OtherCreatedProjectsAdapter extends ArrayAdapter<GetProjects> {

    public ArrayList<Integer> listPosition = new ArrayList<>();
    Context ctx;
    List<GetProjects> dataList;

    ItemFilter itemFilter = new ItemFilter();

    public OtherCreatedProjectsAdapter(Context context, List<GetProjects> objects) {
        super(context, R.layout.portfolio_projectby_created_items, objects);
        this.dataList = objects;
        this.ctx = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public GetProjects getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.portfolio_projectby_created_items, parent, false);
            holder.projectImage = (ImageView) convertView.findViewById(R.id.projectImage);
            holder.projectStatus = (TextView) convertView.findViewById(R.id.projectStatus);
            holder.projectHeader = (TextView) convertView.findViewById(R.id.projectHeader);
            holder.tv_userN = (TextView) convertView.findViewById(R.id.tv_userN);
            holder.tv_Category = (TextView) convertView.findViewById(R.id.tv_Category);
            holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
            holder.tv_supported = (TextView) convertView.findViewById(R.id.tv_supported);
            holder.tv_goal = (TextView) convertView.findViewById(R.id.tv_goal);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);
            holder.tv_days_left = (TextView) convertView.findViewById(R.id.tv_days_left);
            holder.projectImage = (ImageView) convertView.findViewById(R.id.projectImage);
            holder.deleteLayout = (LinearLayout) convertView.findViewById(R.id.deleteLayout);
            holder.editLayout = (LinearLayout) convertView.findViewById(R.id.editLayout);
            holder.img_category = (ImageView) convertView.findViewById(R.id.img_category);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.editLayout.setVisibility(View.GONE);
        holder.deleteLayout.setVisibility(View.GONE);

        final GetProjects creatorsData = getItem(position);
        switch (creatorsData.status) {
            case "supported":
                holder.projectStatus.setText(getContext().getString(R.string.successfully_supported));
                holder.projectStatus.setBackgroundResource(R.drawable.green_strip_transparent);
                break;
            case "complete":
                holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
                holder.projectStatus.setText(getContext().getString(R.string.campaign_ended));
                break;
            default:
                holder.projectStatus.setVisibility(View.INVISIBLE);
                break;
        }

        Picasso.with(getContext())
                .load(Utils.getCategoryImage(creatorsData.category_id, false))
                .transform(new ColorTransformation(ContextCompat.getColor(getContext(), R.color.transform_color)))
                .into(holder.img_category);

        holder.projectHeader.setText(creatorsData.title);
        holder.tv_goal.setText("$" + creatorsData.goal);
        holder.tv_days_left.setText(creatorsData.days_left);
        holder.tv_userN.setText(getContext().getString(R.string.by) + creatorsData.project_by);
        holder.tv_location.setText(creatorsData.city_name + ", " + creatorsData.country_name);

        if (TextUtils.isNullOrEmpty(creatorsData.image_url)) {
            holder.projectImage.setImageResource(R.drawable.server_error_placeholder);
        } else {
            ((BaseAppCompatActivity) getContext()).displayImage(getContext(), holder.projectImage, creatorsData.image_url,
                    ContextCompat.getDrawable(getContext(), R.drawable.server_error_placeholder));
        }

        holder.tv_Category.setText(creatorsData.category_name);

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

//        if (holder.projectStatus.getText().toString().equals("publish")) {
//            holder.projectStatus.setBackgroundResource(R.drawable.green_strip_transparent);
//        }
//
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
                    dialogWithTwoButton(creatorsData, position);
                } else {
                    ((BaseAppCompatActivity) getContext()).showNetworkDialog();
                }
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }

    public void dialogWithTwoButton(final GetProjects creatorsData, final int pos) {
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
                if (((BaseAppCompatActivity) getContext()).isInternetConnected())
                    projectDeleted(creatorsData, pos);
                else
                    ((BaseAppCompatActivity) getContext()).showNetworkDialog();
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


    private void projectDeleted(final GetProjects creatorsData, final int pos) {
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
                            listPosition.add(pos);
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
        LinearLayout editLayout;
        ProgressBar progressBar;
    }

    public class ItemFilter extends Filter {

        @Override
        public FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<GetProjects> list = dataList;

            int count = list.size();
            final ArrayList<GetProjects> nlist = new ArrayList<>(count);

            String filterableString;

            if (filterString.length() != 0) {
                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).toString();
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }
                results.values = nlist;
                results.count = nlist.size();

            } else {

                results.values = dataList;
                results.count = dataList.size();
            }
            Log.e("filterString", filterString.length() + "");

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void publishResults(CharSequence constraint, FilterResults results) {
            dataList = (ArrayList<GetProjects>) results.values;
            notifyDataSetChanged();
        }

    }
}
