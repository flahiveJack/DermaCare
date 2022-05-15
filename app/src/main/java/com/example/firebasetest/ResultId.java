package com.example.firebasetest;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ResultId {
    @Exclude
    public String ResultId;

    public <J extends ResultId>  J withId (@NonNull final String id){
        this.ResultId = id;
        return (J) this;
    }
}
