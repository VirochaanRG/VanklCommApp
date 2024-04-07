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
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    public User userDoc;
    public List<Broadcast> currentBroadcast;
    public BroadcastModel() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        userDoc = new User();
        currentBroadcast = new ArrayList<>();
    }

    public void getRole(){
        db.collection("users").whereEqualTo("email", user.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User userDoc = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userDoc = document.toObject(User.class);
                        }
                        this.userDoc = userDoc;
                        setChanged();
                        notifyObservers("RoleUpdated");
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void send_broadcast(String bcast) {
        Broadcast bc = new Broadcast();
        bc.setAccountSend(user.getEmail());
        bc.setContent(bcast);
        bc.setTimestamp(new Date());
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
    public void returnChannelMessages(){
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
                        currentBroadcast.clear();
                        List<Broadcast> chats = snapshot.toObjects(Broadcast.class);
                        for(Broadcast s: chats){
                            currentBroadcast.add(s);
                        }
                        System.out.println("Final: " + currentBroadcast);
                        Collections.sort(currentBroadcast, new Comparator<Broadcast>() {
                            @Override
                            public int compare(Broadcast m1, Broadcast m2) {
                                return m1.getTimestamp().compareTo(m2.getTimestamp());
                            }
                        });
                        setChanged();
                        notifyObservers("BroadcastUpdate");

                    }
                });
    }
}
