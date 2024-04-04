package com.example.vanklcommapp.Models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vanklcommapp.Models.DataTypes.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Observable;

public class MessageModel extends Observable {
        public FirebaseAuth mAuth;
        public FirebaseUser user;
        public FirebaseFirestore db;
        public List<Message> currentChat;

        public MessageModel(){
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            user = mAuth.getCurrentUser();
            currentChat = new ArrayList<>();
        }
        public void sendMessage(Message m){
            db.collection("messages")
                    .add(m)
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
        public void returnChannelMessages(String contactEmail){
            db.collection("messages")
                    .addSnapshotListener(new EventListener<QuerySnapshot>()
                    {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                // Handle error
                                //...
                                return;
                            }
                            currentChat.clear();
                            List<Message> chats = snapshot.toObjects(Message.class);
                            for(Message s: chats){
                                if((s.getAccountRecieve().equals(user.getEmail()) && s.getAccountSend().equals(contactEmail)) || (s.getAccountSend().equals(user.getEmail()) && s.getAccountRecieve().equals(contactEmail))){
                                    currentChat.add(s);
                                }
                            }
                            System.out.println("Final: " + currentChat);
                            Collections.sort(currentChat, new Comparator<Message>() {
                                @Override
                                public int compare(Message m1, Message m2) {
                                    return m1.getTimestamp().compareTo(m2.getTimestamp());
                                }
                            });
                            setChanged();
                            notifyObservers("MessageUpdate");

                        }
                    });
        }

}
