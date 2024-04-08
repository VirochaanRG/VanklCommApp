package com.example.vanklcommapp.Models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vanklcommapp.Controllers.BroadcastChannel;
import com.example.vanklcommapp.Models.DataTypes.Broadcast;
import com.example.vanklcommapp.Models.DataTypes.EncryptedMessage;
import com.example.vanklcommapp.Models.DataTypes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Observable;

public class BroadcastModel extends Observable {
    // Firebase authentication and database references
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;

    // User information and current broadcast list
    public User userDoc;
    public List<Broadcast> currentBroadcast;

    // Constructor initializing Firebase components and data structures
    public BroadcastModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        userDoc = new User();
        currentBroadcast = new ArrayList<>();
    }

    // Method to retrieve user role from Firestore
    public void getRole(){
        // Query Firestore for user role
        db.collection("users").whereEqualTo("email", user.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User userDoc = null;
                        // Parse query results into User object
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userDoc = document.toObject(User.class);
                        }
                        // Update user role and notify observers
                        this.userDoc = userDoc;
                        setChanged();
                        notifyObservers("RoleUpdated");
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    // Method to send a broadcast message
    public void send_broadcast(String bcast) {
        // Create a new Broadcast object
        Broadcast bc = new Broadcast();
        bc.setAccountSend(user.getEmail());
        bc.setContent(bcast);
        bc.setTimestamp(new Date());
        // Add the broadcast message to Firestore db
        db.collection("broadcasts").add(bc)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    // Method to retrieve and update current broadcast messages
    public void returnChannelMessages(){
        // Listen for changes in the 'broadcasts' collection in Firestore
        db.collection("broadcasts")
                .addSnapshotListener(new EventListener<QuerySnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle error
                            //...
                            return;
                        }
                        // Clear current broadcast list
                        currentBroadcast.clear();
                        // Populate current broadcast list with updated messages
                        List<Broadcast> chats = snapshot.toObjects(Broadcast.class);
                        for(Broadcast s: chats){
                            currentBroadcast.add(s);
                        }
                        // Sort broadcast messages by timestamp
                        Collections.sort(currentBroadcast, new Comparator<Broadcast>() {
                            @Override
                            public int compare(Broadcast m1, Broadcast m2) {
                                return m1.getTimestamp().compareTo(m2.getTimestamp());
                            }
                        });
                        // Notify observers of broadcast update
                        setChanged();
                        notifyObservers("BroadcastUpdate");
                    }
                });
    }
}

