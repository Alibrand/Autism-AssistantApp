package com.ksacp2022t3.fanarapp.models;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;

public class ExerciseFile {
    String name;
    StorageReference reference;
    Uri path;

    public ExerciseFile(String name, StorageReference reference, Uri path) {
        this.name = name;
        this.reference = reference;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public StorageReference getReference() {
        return reference;
    }

    public Uri getPath() {
        return path;
    }
}
