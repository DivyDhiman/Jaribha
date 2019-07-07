package com.jaribha.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseFragment;
import com.jaribha.interfaces.GetCountry;
import com.jaribha.models.CountryBean;
import com.jaribha.server_communication.GetCountryByProjectTask;
import com.jaribha.shared_preferences.JaribhaPrefrence;
import com.jaribha.utility.Constants;
import com.jaribha.utility.Urls;
import com.jaribha.utility.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

public class FilterCountryFragment extends BaseFragment implements View.OnClickListener {

    private GridView grid_filter_country;

    private CountryCategoryAdapter adapter;

    private TextView tv_all_countries, tv_country_reset;

    private String idStr;

    private ProgressDialog progressDialog;

    private Activity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (isInternetConnected())
                getCountries();
            else
                showNetworkDialog();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_country_filter, container, false);

        progressDialog = new ProgressDialog(activity, R.style.MyTheme);

        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(activity, R.drawable.progress_style));
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);

        tv_all_countries = (TextView) view.findViewById(R.id.tv_all_countries);
        tv_all_countries.setOnClickListener(this);

        tv_country_reset = (TextView) view.findViewById(R.id.tv_country_reset);
        tv_country_reset.setOnClickListener(this);
        tv_country_reset.setText(getActivity().getResources().getString(R.string.reset));
        tv_all_countries.setText(getActivity().getResources().getString(R.string.all_countries));

        grid_filter_country = (GridView) view.findViewById(R.id.grid_filter_country);

        adapter = new CountryCategoryAdapter(getActivity(), countryList);
        grid_filter_country.setAdapter(adapter);

        grid_filter_country.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CountryBean bean = countryList.get(position);
                for (int i = 0; i < countryList.size(); i++) {
                    countryList.get(i).selected = false;
                }

                if (bean.selected) {
                    bean.selected = false;
                } else {
                    idStr = bean.id;
                    JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_ID, idStr);
                    JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_NAME, bean.name_eng);
                    bean.selected = true;
                }
                adapter.notifyDataSetChanged();
            }
        });

        for (int i = 0; i < countryList.size(); i++) {
            CountryBean bean = countryList.get(i);
            bean.selected = true;
        }


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_country_reset:
                for (int i = 0; i < countryList.size(); i++) {
                    CountryBean bean = countryList.get(i);
                    bean.selected = false;
                }
                JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_ID, "");
                JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_NAME, getString(R.string.all_country));
                adapter.notifyDataSetChanged();
                break;

            case R.id.tv_all_countries:
                for (int i = 0; i < countryList.size(); i++) {
                    CountryBean bean = countryList.get(i);
                    bean.selected = true;
                }
                JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_NAME, getString(R.string.all_country));
                JaribhaPrefrence.setPref(getActivity(), Constants.COUNTRY_ID, "");
                adapter.notifyDataSetChanged();
                break;

            default:
                break;
        }
    }

    public void getCountries() {
        //showLoading();
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();

        try {
            countryList.clear();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("apikey", Urls.API_KEY);
            if (getUser() != null) {
                jsonObject.put("user_id", getUser().id);
                jsonObject.put("user_token", getUser().user_token);
            } else {
                jsonObject.put("user_id", "");
                jsonObject.put("user_token", "");
            }
            jsonObject.put("country_id", "");
            GetCountryByProjectTask countryTask = new GetCountryByProjectTask(jsonObject, new GetCountry() {
                @Override
                public void OnSuccess(ArrayList<CountryBean> list) {
                    //hideLoading();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();

                    for (int i = 0; i < list.size(); i++) {
                        if (!JaribhaPrefrence.getPref(getActivity(), Constants.COUNTRY_NAME, "").equals(activity.getResources().getString(R.string.all_country))) {
                            if (list.get(i).name_eng.equals(JaribhaPrefrence.getPref(getActivity(), Constants.COUNTRY_NAME, ""))) {
                                list.get(i).selected = true;
                            }
                        } else {
                            list.get(i).selected = true;
                        }
                    }
                    countryList.addAll(list);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void OnFail(String s) {
                    //hideLoading();
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    if (s.equalsIgnoreCase("User Not Found")) {
                        showSessionDialog();
                    } else if (s.equalsIgnoreCase("Data Not Found")) {
                        showDataNotFoundDialog();
                    } else {
                        showServerErrorDialog();
                    }
                }
            });
            countryTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
            showServerErrorDialog();
        }


    }

    public class CountryCategoryAdapter extends ArrayAdapter<CountryBean> {

        public CountryCategoryAdapter(Context context, ArrayList<CountryBean> beans) {
            super(context, R.layout.item_category_filter, beans);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                holder = new ViewHolder();

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_category_filter, parent, false);

                holder.img_filter = (ImageView) convertView.findViewById(R.id.img_filter);

                holder.img_checked = (ImageView) convertView.findViewById(R.id.img_checked);
                holder.img_checked.setVisibility(View.INVISIBLE);

                holder.tv_filter = (TextView) convertView.findViewById(R.id.tv_filter);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CountryBean mBean = getItem(position);

            holder.tv_filter.setText(mBean.name_eng);

            holder.img_filter.setImageResource(Utils.getCountryImage(mBean.id));

            if (mBean.selected) {
                holder.img_checked.setVisibility(View.VISIBLE);
            } else {
                holder.img_checked.setVisibility(View.INVISIBLE);
            }

            return convertView;
        }

        class ViewHolder {
            ImageView img_filter, img_checked;
            TextView tv_filter;
        }
    }

    ArrayList<CountryBean> countryList = new ArrayList<>();

}
