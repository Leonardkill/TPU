package com.todasporuma.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.todasporuma.FirebaseConfiguration;

public class User {
    private String userId;
    private String name;
    private String email;
    private String password;

    public void save(){
        DatabaseReference database = FirebaseConfiguration.getFirebaseDatabase();
        database.child("usuarios")
                .child(userId)
                .setValue(this);
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
