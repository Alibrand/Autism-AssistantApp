package com.ksacp2022t3.fanarapp.models;

import java.util.ArrayList;
import java.util.List;

public class SpecialistAccount extends Account {
    String brief;
    String degree;
    String experience_years;
    int comments_count=0;
    int likes_count=0;
    List<String> liked_users=new ArrayList<>();

    public SpecialistAccount() {
    }

    public int getComments_count() {
        return comments_count;
    }

    public List<String> getLiked_users() {
        return liked_users;
    }

    public void setLiked_users(List<String> liked_users) {
        this.liked_users = liked_users;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getExperience_years() {
        return experience_years;
    }

    public void setExperience_years(String experience_years) {
        this.experience_years = experience_years;
    }
}
