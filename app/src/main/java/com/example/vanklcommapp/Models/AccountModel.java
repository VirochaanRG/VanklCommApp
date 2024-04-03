package com.example.vanklcommapp.Models;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/** @noinspection ALL*/
public class AccountModel extends Observable {


    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseFirestore db;
    public AccountModel() {
        //Init Firebase Utils
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        System.out.println("Account Model: " + user);
    }
    public void login(String email, String password){
        //Login with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            //On completion
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                //If succeeded notify required controller with Login Success message
                                if (task.isSuccessful()) {
                                    System.out.println("LoginSuccess");
                                    //Ensure we set User after logging in and Notify Relevant observers
                                    user = mAuth.getCurrentUser();
                                    setChanged();
                                    notifyObservers("LoginSuccess");
                                }
                                else {
                                    // sign-in failed notify relevant observers
                                    System.out.println("LoginFail");
                                    setChanged();
                                    notifyObservers("LoginFail");
                                }
                            }
                        });
    }
    public void createAccount(String email, String password, String eid, String username){
        // create new user and add to users database
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {

                            //Create user Hashmap
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);
                            user.put("password", password);
                            user.put("email", email);
                            user.put("employeeID", eid);
                            user.put("role", "employee");

                            // Add a new user with a generated ID
                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        //On success notify relevant observer
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                            setChanged();
                                            notifyObservers("CreateSuccess");
                                            login(email, password);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                            setChanged();
                                            notifyObservers("CreateFailure");
                                        }
                                    });
                        }
                        else {
                            setChanged();
                            notifyObservers("CreateFailure");
                        }
                    }
                });
    }
}

