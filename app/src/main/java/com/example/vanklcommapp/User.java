package com.example.vanklcommapp;

import com.google.firebase.firestore.DocumentId;

public class User {


    public User(String username, String password, String role, String employeeID, String email, String uid) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeID = employeeID;
        this.email = email;
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getEmail() {
        return email;
    }

    private String username;
    private String password;
    private String role;
    private String employeeID;
    private String email;
    @DocumentId
    public String getUid() {
        return uid;
    }
    @DocumentId
    public void setUid(String uid) {
        this.uid = uid;
    }
    @DocumentId
    private String uid;
    public User() {
        // Required empty public constructor
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
