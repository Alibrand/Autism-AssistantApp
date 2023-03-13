package com.ksacp2022t3.fanarapp.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Exercise implements Serializable {
    String id;
    String question;
    String audio_name;
    String option1_image;
    String option1_text;
    String option2_image;
    String option2_text;
    String option3_image;
    String option3_text;
    String correct_option;
    boolean default_exercise=false;
    @ServerTimestamp
    Date created_at;

    public Exercise() {

    }

    public Exercise(String question, String audio_name, String option1_image, String option1_text, String option2_image, String option2_text, String option3_image, String option3_text, String correct_option, boolean default_exercise) {
        this.question = question;
        this.audio_name = audio_name;
        this.option1_image = option1_image;
        this.option1_text = option1_text;
        this.option2_image = option2_image;
        this.option2_text = option2_text;
        this.option3_image = option3_image;
        this.option3_text = option3_text;
        this.correct_option = correct_option;
        this.default_exercise = default_exercise;
    }

    public boolean isDefault_exercise() {
        return default_exercise;
    }

    public void setDefault_exercise(boolean default_exercise) {
        this.default_exercise = default_exercise;
    }

    public String getOption1_text() {
        return option1_text;
    }

    public void setOption1_text(String option1_text) {
        this.option1_text = option1_text;
    }

    public String getOption2_text() {
        return option2_text;
    }

    public void setOption2_text(String option2_text) {
        this.option2_text = option2_text;
    }

    public String getOption3_text() {
        return option3_text;
    }

    public void setOption3_text(String option3_text) {
        this.option3_text = option3_text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAudio_name() {
        return audio_name;
    }

    public void setAudio_name(String audio_name) {
        this.audio_name = audio_name;
    }

    public String getOption1_image() {
        return option1_image;
    }

    public void setOption1_image(String option1_image) {
        this.option1_image = option1_image;
    }

    public String getOption2_image() {
        return option2_image;
    }

    public void setOption2_image(String option2_image) {
        this.option2_image = option2_image;
    }

    public String getOption3_image() {
        return option3_image;
    }

    public void setOption3_image(String option3_image) {
        this.option3_image = option3_image;
    }

    public String getCorrect_option() {
        return correct_option;
    }

    public void setCorrect_option(String correct_option) {
        this.correct_option = correct_option;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
