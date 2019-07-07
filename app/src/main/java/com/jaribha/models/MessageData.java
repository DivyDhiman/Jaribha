package com.jaribha.models;

import com.jaribha.utility.Utils;

import java.io.Serializable;


public class MessageData implements Serializable, Item {

    public String id;
    public String to_user_id;
    public String from_user_id;
    public String subject;
    public String message;
    public String read;
    public String parent_id;
    public String created;
    public String name;
    public String imgurl;
    public String seperatorDate;
    public String separatorLabel;
    public boolean isSection;
    public String unread_count;
    // public String parent_id;

    public String getCreated() {
        return Utils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", created);
    }

    @Override
    public boolean isSection() {
        return false;
    }
}
