package com.todasporuma.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.todasporuma.FirebaseConfiguration;

public class User {
    private String userId;
    private String name;
    private String email;
    private String password;

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    private String endereco;
    private String celular;

    public void save(){
        DatabaseReference database = FirebaseConfiguration.getFirebaseDatabase();
        database.child("usuarios")
                .child(userId)
                .setValue(this);
    }

    public User(String nome,String email,String endereco,String celular){
        this.name = nome;
        this.email = email;
        this.endereco = endereco;
        this.celular = celular;
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
