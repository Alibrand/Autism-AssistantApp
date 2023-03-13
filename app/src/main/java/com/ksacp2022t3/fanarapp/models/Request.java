package com.ksacp2022t3.fanarapp.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Request {
    String id;
    String specialist_id;
    String specialist_name;
    String sender_id;
    String sender_name;
    String sender_state;
    String status="Pending";
    Date session_date;
    @ServerTimestamp
    Date created_at;
    String to_notify;

    public Request() {
    }

    public String getTo_notify() {
        return to_notify;
    }

    public void setTo_notify(String to_notify) {
        this.to_notify = to_notify;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpecialist_id() {
        return specialist_id;
    }

    public void setSpecialist_id(String specialist_id) {
        this.specialist_id = specialist_id;
    }

    public String getSpecialist_name() {
        return specialist_name;
    }

    public void setSpecialist_name(String specialist_name) {
        this.specialist_name = specialist_name;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public String getSender_state() {
        return sender_state;
    }

    public void setSender_state(String sender_state) {
        this.sender_state = sender_state;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSession_date() {
        return session_date;
    }

    public void setSession_date(Date session_date) {
        this.session_date = session_date;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
