package com.jaribha.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
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

public class OtherUserSponseredAdapter extends ArrayAdapter<GetProjects> {

    List<GetProjects> dataList;
    ItemFilter itemFilter = new ItemFilter();

    public OtherUserSponseredAdapter(Context context, List<GetProjects> objects) {
        super(context, R.layout.portfolio_projectby_items, objects);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.portfolio_projectby_items, parent, false);

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
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        GetProjects sopportedData = getItem(position);

        holder.projectStatus.setText(sopportedData.status);
        switch (sopportedData.status) {
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
//                //holder.projectStatus.setText(getContext().getString(R.string.successfully_submitted));
//                break;
//            case "incomplete":
//                //holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
//                //holder.projectStatus.setText(getContext().getString(R.string.incomplete));
//                break;
            default:
                holder.projectStatus.setVisibility(View.INVISIBLE);
                break;
        }
        holder.projectHeader.setText(sopportedData.title);
        holder.tv_goal.setText("$" + sopportedData.goal);
        holder.tv_days_left.setText(sopportedData.days_left);
        holder.tv_userN.setText(String.format("%s%s", getContext().getString(R.string.by), sopportedData.project_by));
        holder.tv_location.setText(sopportedData.city_name + ", " + sopportedData.country_name);

        Picasso.with(getContext())
                .load(Utils.getCategoryImage(sopportedData.category_id, false))
                .transform(new ColorTransformation(ContextCompat.getColor(getContext(), R.color.transform_color)))
                .into(holder.img_category);

        if (TextUtils.isNullOrEmpty(sopportedData.image_url)) {
            holder.projectImage.setImageResource(R.drawable.server_error_placeholder);
        } else {
            ((BaseAppCompatActivity) getContext()).displayImage(getContext(), holder.projectImage, sopportedData.image_url,
                    ContextCompat.getDrawable(getContext(), R.drawable.server_error_placeholder));
        }

        holder.tv_Category.setText(sopportedData.category_name);

        //holder.progressBar.setProgress();
        if (!TextUtils.isNullOrEmpty(sopportedData.total_support_amount) && !TextUtils.isNullOrEmpty(sopportedData.goal)) {
//            double supportAmt = Double.parseDouble(sopportedData.total_support_amount);
//            double goalAmt = Double.parseDouble(sopportedData.goal);
//
//            if (Utils.isProjectExpired(sopportedData.enddate)) {
//                if ((supportAmt < goalAmt) || (supportAmt >= goalAmt)) {
//                    holder.projectStatus.setText(getContext().getString(R.string.campaign_ended));
//                    holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
//                }
//            }
            holder.progressBar.setProgress(((BaseAppCompatActivity) getContext()).getProgressPercent(Double.parseDouble(sopportedData.total_support_amount), Double.parseDouble(sopportedData.goal)));
        }
//        double supportedAmount = 0;
//        try {
//            if (!TextUtils.isNullOrEmpty(sopportedData.total_support_amount))
//                supportedAmount = Double.parseDouble(sopportedData.total_support_amount);
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//
//        int goal = 0;
//
//        if (!TextUtils.isNullOrEmpty(sopportedData.goal))
//            goal = Integer.parseInt(sopportedData.goal);
//
//        double Supported = (supportedAmount * 100) / goal;

        holder.tv_supported.setText(Utils.getSupportedAmount(sopportedData.total_support_amount, sopportedData.goal) + "%");

        return convertView;
    }


    @Override
    public Filter getFilter() {

        return itemFilter;
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
        ProgressBar progressBar;
    }
}
