package com.here.adly.db;

import com.google.firebase.database.DatabaseReference;

public class DatabaseFB {
    public DatabaseReference mDatabase;


    public DatabaseFB(){
        mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();
    }
}
