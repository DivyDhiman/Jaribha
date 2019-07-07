package com.jaribha.adapters;

import android.content.Context;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OtherCreatedProjectsAdapterNew extends BaseAdapter implements Filterable {

    public ArrayList<Integer> listPosition = new ArrayList<>();
    private List<GetProjects> dataList;
    ItemFilter itemFilter = new ItemFilter();
    private Context context;

    public OtherCreatedProjectsAdapterNew(Context context, List<GetProjects> objects) {
        //  super(context, R.layout.portfolio_projectby_created_items, objects);
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
            case "publish":
                holder.projectStatus.setText(context.getString(R.string.successfully_published));
                holder.projectStatus.setBackgroundResource(R.drawable.green_strip_transparent);
                break;
            case "complete":
                holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
                holder.projectStatus.setText(context.getString(R.string.campaign_ended));
                break;
            case "submit":
                holder.projectStatus.setBackgroundResource(R.drawable.green_strip_transparent);
                holder.projectStatus.setText(context.getString(R.string.successfully_submitted));
                break;
            case "Incomplete":
                holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
                holder.projectStatus.setText(context.getString(R.string.incomplete));
                break;
        }

        Picasso.with(context)
                .load(Utils.getCategoryImage(creatorsData.category_id, false))
                .transform(new ColorTransformation(context.getResources().getColor(R.color.transform_color)))
                .into(holder.img_category);

        holder.projectHeader.setText(creatorsData.title);
        holder.tv_goal.setText("$" + creatorsData.goal);
        holder.tv_days_left.setText(creatorsData.period);
        holder.tv_userN.setText(context.getString(R.string.by) + creatorsData.project_by);
        holder.tv_location.setText(creatorsData.city_name + ", " + creatorsData.country_name);

        if (TextUtils.isNullOrEmpty(creatorsData.image_url)) {
            holder.projectImage.setImageResource(R.drawable.server_error_placeholder);
        } else {
            ((BaseAppCompatActivity) context).displayImage(context, holder.projectImage, creatorsData.image_url, context.getResources().getDrawable(R.drawable.server_error_placeholder));
        }

        //  ((BaseAppCompatActivity) getContext()).displayImage(context, holder.projectImage, creatorsData.image_url, context.getResources().getDrawable(R.drawable.image_upload));

        holder.tv_Category.setText(creatorsData.category_name);

        if (!TextUtils.isNullOrEmpty(creatorsData.total_support_amount) && !TextUtils.isNullOrEmpty(creatorsData.goal)) {
            double supportAmt = Double.parseDouble(creatorsData.total_support_amount);
            double goalAmt = Double.parseDouble(creatorsData.goal);

            if (Utils.isProjectExpired(creatorsData.enddate)) {
                if ((supportAmt < goalAmt) || (supportAmt >= goalAmt)) {
                    holder.projectStatus.setText(context.getString(R.string.campaign_ended));
                    holder.projectStatus.setBackgroundResource(R.drawable.red_strip);
                }
            }
            holder.progressBar.setProgress(((BaseAppCompatActivity) context).getProgressPercent(Double.parseDouble(creatorsData.total_support_amount), Double.parseDouble(creatorsData.goal)));
        }

        if (holder.projectStatus.getText().toString().equals("publish")) {
            holder.projectStatus.setBackgroundResource(R.drawable.green_strip_transparent);
        }

        double supportedAmount = 0;
        try {
            if (!TextUtils.isNullOrEmpty(creatorsData.total_support_amount))
                supportedAmount = Double.parseDouble(creatorsData.total_support_amount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        int goal = 0;

        if (!TextUtils.isNullOrEmpty(creatorsData.goal))
            goal = Integer.parseInt(creatorsData.goal);

        double Supported = (supportedAmount * 100) / goal;

        holder.tv_supported.setText(new DecimalFormat("##.##").format(Supported) + "%");


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
            final ArrayList<GetProjects> nlist = new ArrayList<GetProjects>(count);

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
