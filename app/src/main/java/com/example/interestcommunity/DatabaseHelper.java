package com.example.interestcommunity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelper {
    private static DatabaseReference databaseReference;

    public static DatabaseReference getDatabaseReference() {
        if (databaseReference == null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://interestcommunity-f6a3f-default-rtdb.europe-west1.firebasedatabase.app");

            databaseReference = database.getReference();
        }
        return databaseReference;
    }
}