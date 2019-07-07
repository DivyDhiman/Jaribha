package com.jaribha.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.GetProjects;
import com.jaribha.utility.ColorTransformation;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class OtherFavoritesAdapter extends BaseAdapter implements Filterable {

    Context context;
    List<GetProjects> dataList;
    ItemFilter filter = new ItemFilter();

    public OtherFavoritesAdapter(Context context, List<GetProjects> objects) {
        this.context = context;
        this.dataList = objects;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.portfolio_projectby_created_items, parent, false);
            holder.projectImage = (ImageView) convertView.findViewById(R.id.projectImage);
            holder.projectStatus = (TextView) convertView.findViewById(R.id.projectStatus);
            holder.projectHeader = (TextView) convertView.findViewById(R.id.projectHeader);
            holder.tv_userN = (TextView) convertView.findViewById(R.id.tv_userN);
            holder.tv_Category = (TextView) convertView.findViewById(R.id.tv_Category);
            holder.tv_location = (TextView) convertView.findViewById(R.id.tv_location);
            holder.tv_supported = (TextView) convertView.findViewById(R.id.tv_supported);
            holder.tv_goal = (TextView) convertView.findViewById(R.id.tv_goal);
            holder.tv_days_left = (TextView) convertView.findViewById(R.id.tv_days_left);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progress);

            holder.img_category = (ImageView) convertView.findViewById(R.id.img_category);

            holder.deleteLayout = (LinearLayout) convertView.findViewById(R.id.deleteLayout);
            holder.deleteLayout.setVisibility(View.GONE);

            holder.editLayout = (LinearLayout) convertView.findViewById(R.id.editLayout);
            holder.editLayout.setVisibility(View.GONE);


            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final GetProjects getProjects = getItem(position);

        switch (getProjects.status) {
            case "supported":
                holder.projectStatus.setVisibility(View.VISIBLE);
                holder.projectStatus.setText(context.getString(R.string.successfully_supported));
                holder.projectStatus.setBackgroundResource(R.drawable.green_strip_transparent);
                break;
            case "complete":
                holder.projectStatus.setVisibility(View.VISIBLE);
                holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
                holder.projectStatus.setText(context.getString(R.string.campaign_ended));
                break;
            default:
                holder.projectStatus.setVisibility(View.INVISIBLE);
                break;
        }

        Picasso.with(context)
                .load(Utils.getCategoryImage(getProjects.category_id, false))
                .transform(new ColorTransformation(ContextCompat.getColor(context, R.color.transform_color)))
                .into(holder.img_category);

        holder.projectHeader.setText(getProjects.title);
        holder.tv_goal.setText("$" + getProjects.goal);
        holder.tv_days_left.setText(getProjects.days_left);
        holder.tv_userN.setText(context.getString(R.string.by) + getProjects.project_by);
        holder.tv_location.setText(getProjects.city_name + ", " + getProjects.country_name);

        if (TextUtils.isNullOrEmpty(getProjects.image_url)) {
            holder.projectImage.setImageResource(R.drawable.server_error_placeholder);
        } else {
            ((BaseAppCompatActivity) context).displayImage(context, holder.projectImage, getProjects.image_url,
                    ContextCompat.getDrawable(context, R.drawable.server_error_placeholder));
        }

        //((BaseAppCompatActivity) getContext()).displayImage(context, holder.projectImage, getProjects.image, context.getResources().getDrawable(R.drawable.image_upload));

        if (!TextUtils.isNullOrEmpty(getProjects.total_support_amount) && !TextUtils.isNullOrEmpty(getProjects.goal)) {
//            double supportAmt = Double.parseDouble(getProjects.total_support_amount);
//            double goalAmt = Double.parseDouble(getProjects.goal);
//
//            if (Utils.isProjectExpired(getProjects.enddate)) {
//                if ((supportAmt < goalAmt) || (supportAmt >= goalAmt)) {
//                    holder.projectStatus.setText(context.getString(R.string.campaign_ended));
//                    holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
//                }
//            }
            holder.progressBar.setProgress(((BaseAppCompatActivity) context).getProgressPercent(Double.parseDouble(getProjects.total_support_amount), Double.parseDouble(getProjects.goal)));
        }


//        if (holder.projectStatus.getText().toString().equals("publish")) {
//            holder.projectStatus.setBackgroundResource(R.drawable.green_strip_transparent);
//        }
//
//        float supportedAmount = 0;
//        try {
//            supportedAmount = Integer.parseInt(getProjects.total_support_amount);
//
//
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//        int goal = Integer.parseInt(getProjects.goal);
//        float Supported = (supportedAmount * 100) / goal;
        holder.tv_supported.setText(Utils.getSupportedAmount(getProjects.total_support_amount, getProjects.goal) + "%");


        return convertView;
    }

    @Override
    public Filter getFilter() {
        return filter;
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
}
