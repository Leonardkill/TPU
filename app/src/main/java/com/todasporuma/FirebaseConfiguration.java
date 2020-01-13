package com.todasporuma;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseConfiguration {

    private static FirebaseAuth auth;
    private static DatabaseReference database;
    private static StorageReference storage;

    public static DatabaseReference getFirebaseDatabase(){
        if (database == null){
            database = FirebaseDatabase .getInstance().getReference();
        }
        return database;
    }

    public static FirebaseAuth getFirebaseAutenticacao(){
        if (auth == null){
            auth = FirebaseAuth.getInstance();
        }
        return auth;
    }

    public static StorageReference getFiresabseStorage(){
        if (storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }
}