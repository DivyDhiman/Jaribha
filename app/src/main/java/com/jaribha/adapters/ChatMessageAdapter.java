package com.jaribha.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.Item;
import com.jaribha.models.MessageData;
import com.jaribha.models.SectionItem;
import com.jaribha.utility.TextUtils;
import com.jaribha.utility.Utils;

import java.util.ArrayList;

public class ChatMessageAdapter extends ArrayAdapter<Item> {

    private LayoutInflater inflater;

    public ChatMessageAdapter(Context context, ArrayList<Item> data) {
        super(context, 0, data);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final Item item = getItem(position);
        if (item != null) {
            if (item.isSection()) {
                SectionItem sectionItem = (SectionItem) item;
                view = inflater.inflate(R.layout.list_item_section, parent, false);

                view.setOnClickListener(null);
                view.setOnLongClickListener(null);
                view.setLongClickable(false);

                final TextView sectionView = (TextView) view.findViewById(R.id.tv_section);
                sectionView.setText(Utils.formatToYesterdayOrToday(getContext(), sectionItem.title));

            } else {
                MessageData message = (MessageData) item;

                boolean isMine = message.from_user_id.equals(((BaseAppCompatActivity) getContext()).getUser().id);

                if (isMine) {
                    view = inflater.inflate(R.layout.item_mine_message, parent, false);

                    TextView textView = (TextView) view.findViewById(R.id.text);
                    if (!TextUtils.isNullOrEmpty(message.message))
                        textView.setText(message.message);

                    TextView tv_date_time = (TextView) view.findViewById(R.id.tv_date_time);
                    tv_date_time.setText(String.format("%s", Utils.formateDateFromstring(getContext(), "yyyy-MM-dd HH:mm:ss", "h:mma", message.created)));

                } else {
                    view = inflater.inflate(R.layout.item_other_message, parent, false);
                    TextView textView = (TextView) view.findViewById(R.id.text);
                    if (!TextUtils.isNullOrEmpty(message.message))
                        textView.setText(message.message);

                    TextView tv_date_time = (TextView) view.findViewById(R.id.tv_date_time);
                    tv_date_time.setText(String.format("%s", Utils.formateDateFromstring(getContext(), "yyyy-MM-dd HH:mm:ss", "h:mma", message.created)));
                }
            }
        }
        return view;
    }
}
