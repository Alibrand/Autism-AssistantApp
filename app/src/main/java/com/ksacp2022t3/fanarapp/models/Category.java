package com.ksacp2022t3.fanarapp.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Category {
    String id;
    String name;
    String image_name;
    boolean default_category=false;
    @ServerTimestamp
    Date created_at;


    public Category() {
    }

    public Category(String name, String image_name, boolean default_category) {
        this.name = name;
        this.image_name = image_name;
        this.default_category = default_category;
    }

    public boolean isDefault_category() {
        return default_category;
    }

    public void setDefault_category(boolean default_category) {
        this.default_category = default_category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
