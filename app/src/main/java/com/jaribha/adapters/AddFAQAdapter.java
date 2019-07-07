package com.jaribha.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.models.AddFAQ;

import java.util.List;

public class AddFAQAdapter extends ArrayAdapter<AddFAQ> {

    public AddFAQAdapter(Context context, List<AddFAQ> objects) {
        super(context, R.layout.item_faq, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_faq, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.tv_ans = (TextView) convertView.findViewById(R.id.tv_ans);

            viewHolder.tv_que = (TextView) convertView.findViewById(R.id.tv_que);

            viewHolder.iv_delete_que = (ImageView) convertView.findViewById(R.id.iv_delete_que);

            viewHolder.iv_edit_que = (ImageView) convertView.findViewById(R.id.iv_edit_que);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AddFAQ addFAQ = getItem(position);

        viewHolder.tv_que.setText(addFAQ.question);

        viewHolder.tv_ans.setText(addFAQ.answer);

        viewHolder.iv_edit_que.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onEditListener != null) {
                    onEditListener.onEditClick(addFAQ, position, true);
                }
            }
        });

        viewHolder.iv_delete_que.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(addFAQ);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView tv_que, tv_ans;
        ImageView iv_edit_que, iv_delete_que;
    }

    public interface OnEditListener {
        void onEditClick(AddFAQ addFAQ, int position, boolean isEdit);
    }

    OnEditListener onEditListener;

    public void setOnEditListener(OnEditListener editListener) {
        onEditListener = editListener;
    }
}
