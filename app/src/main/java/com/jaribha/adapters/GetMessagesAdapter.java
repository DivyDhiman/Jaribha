package com.jaribha.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaribha.R;
import com.jaribha.base.BaseAppCompatActivity;
import com.jaribha.models.MessageData;
import com.jaribha.utility.Utils;

import java.util.List;

public class GetMessagesAdapter extends ArrayAdapter<MessageData> {

    public GetMessagesAdapter(Context context, List<MessageData> objects) {
        super(context, R.layout.message_list_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_list_item, parent, false);

            holder.profileImage = (ImageView) convertView.findViewById(R.id.profileImage);
            holder.tv_username = (TextView) convertView.findViewById(R.id.tv_username);
            holder.tv_message = (TextView) convertView.findViewById(R.id.tv_message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.messageCount = (TextView) convertView.findViewById(R.id.messageCount);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MessageData messageData = getItem(position);

        holder.tv_message.setText(messageData.message);

        holder.time.setText(Utils.formateDateFromstring(getContext(), "yyyy-MM-dd HH:mm:ss", "h:mm a", messageData.created));

        holder.tv_username.setText(messageData.name);

        holder.tv_message.setText(messageData.message);

        if ((messageData.unread_count).equalsIgnoreCase("0")) {
            holder.messageCount.setVisibility(View.GONE);
        } else {
            holder.messageCount.setVisibility(View.VISIBLE);
            holder.messageCount.setText(messageData.unread_count);
        }

        if (messageData.imgurl.isEmpty()) {
            holder.profileImage.setImageResource(R.drawable.user_icon);
        } else {
            ((BaseAppCompatActivity) getContext()).displayImage(getContext(), holder.profileImage, messageData.imgurl, ContextCompat.getDrawable(getContext(), R.drawable.user_icon));
        }

        return convertView;
    }

    class ViewHolder {
        ImageView profileImage;
        TextView tv_username;
        TextView tv_message;
        TextView time;
        TextView messageCount;
    }
}
