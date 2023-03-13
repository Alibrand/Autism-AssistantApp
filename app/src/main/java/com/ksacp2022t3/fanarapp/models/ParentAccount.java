package com.ksacp2022t3.fanarapp.models;

public class ParentAccount extends Account {
    String state_description;

    public ParentAccount() {
    }

    public String getState_description() {
        return state_description;
    }

    public void setState_description(String state_description) {
        this.state_description = state_description;
    }
}
