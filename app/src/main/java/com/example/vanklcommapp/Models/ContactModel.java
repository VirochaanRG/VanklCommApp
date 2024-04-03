package com.example.vanklcommapp.Models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;

public class ContactModel extends Observable {
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseFirestore db;
}
