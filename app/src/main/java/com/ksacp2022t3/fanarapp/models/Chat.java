package com.ksacp2022t3.fanarapp.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat {
    String id;
    List<String> users_ids=new ArrayList<>();
    List<String> users_names=new ArrayList<>();

    @ServerTimestamp
    Date last_update=null;

    public Chat() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getUsers_ids() {
        return users_ids;
    }

    public void setUsers_ids(List<String> users_ids) {
        this.users_ids = users_ids;
    }

    public List<String> getUsers_names() {
        return users_names;
    }

    public void setUsers_names(List<String> users_names) {
        this.users_names = users_names;
    }

    public Date getLast_update() {
        return last_update;
    }

    public void setLast_update(Date last_update) {
        this.last_update = last_update;
    }
}