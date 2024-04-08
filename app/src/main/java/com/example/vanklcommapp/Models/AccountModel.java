package com.example.vanklcommapp.Models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.vanklcommapp.KDC.NetworkAcess.AuthenticationNetworkTask;
import com.example.vanklcommapp.KDC.NetworkAcess.BasicNetworkTask;
import com.example.vanklcommapp.Models.DataTypes.User;
import com.example.vanklcommapp.KDC.NetworkAcess.NetworkTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ExecutionException;

/** @noinspection ALL*/
public class AccountModel extends Observable {

    // Firebase authentication and database references
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseFirestore db;
    public User currentUser;

    // Constructor initializing Firebase components
    public AccountModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        System.out.println("Account Model: " + user);
    }

    // Method to retrieve user information from Firestore
    public void getUser(){
        // Query Firestore for user data
        db.collection("users").whereEqualTo("email", user.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User userDoc = null;
                        // Parse query results into User object
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userDoc = document.toObject(User.class);
                        }
                        // Set current user and notify observers of role update
                        this.currentUser = userDoc;
                        setChanged();
                        notifyObservers("RoleUpdated");
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    // Method for user login
    public void login(String email, String password){
        // Attempt to sign in using Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            // Handle completion of login attempt
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                // If login is successful, notify relevant controllers
                                if (task.isSuccessful()) {
                                    System.out.println("LoginSuccess");
                                    // Update user information and notify observers
                                    user = mAuth.getCurrentUser();
                                    setChanged();
                                    notifyObservers("LoginSuccess");
                                }
                                else {
                                    // Notify observers of login failure
                                    System.out.println("LoginFail");
                                    setChanged();
                                    notifyObservers("LoginFail");
                                }
                            }
                        });
    }

    // Method for creating a new user account
    public void createAccount(String email, String password, String eid, String username){
        // Create a new user account using Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {
                            // Prepare user data to be added to Firestore
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);
                            user.put("password", password);
                            user.put("email", email);
                            user.put("employeeID", eid);
                            user.put("role", "employee");

                            // Add user data to Firestore
                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        // Notify observers upon successful user creation
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                            // Update server to generate keys for user and notify observers of success
                                            String url = "https://vanklwebserver.onrender.com/updatedb?";
                                            url += "email=" + email;
                                            NetworkTask nt = new BasicNetworkTask();
                                            nt.execute(url);
                                            NetworkTask nt2 = new BasicNetworkTask();
                                            nt2.execute("https://vanklwebserver.onrender.com/getkeys");
                                            setChanged();
                                            notifyObservers("CreateSuccess");
                                            // Automatically log in newly created user
                                            login(email, password);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Notify observers upon failure to add user data to Firestore
                                            Log.w(TAG, "Error adding document", e);
                                            setChanged();
                                            notifyObservers("CreateFailure");
                                        }
                                    });
                        }
                        else {
                            // Notify observers upon failure to create user account
                            setChanged();
                            notifyObservers("CreateFailure");
                        }
                    }
                });
    }

    // Method for user logout
    public void logout(){
        // Sign out current user
        FirebaseAuth.getInstance().signOut();
        user = mAuth.getCurrentUser();
        System.out.println("Logout: " + user);
        // Notify observers of logout
        setChanged();
        notifyObservers("Logout");
    }
}

