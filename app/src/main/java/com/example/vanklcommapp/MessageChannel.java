package com.example.vanklcommapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vanklcommapp.Controllers.MainActivity;
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
import java.util.Date;
import java.util.List;

public class MessageChannel extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_channel);
        Bundle bundle = getIntent().getExtras();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        String contactName = bundle.getString("contact");
        TextView tv = findViewById(R.id.contact_details);
        tv.setText(contactName);
        Button buttonReturn = findViewById(R.id.home);
        Button send = findViewById(R.id.message_send_btn);

        EditText msg = findViewById(R.id.chat_message_input);

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
                    List<Message> chats = snapshot.toObjects(Message.class);
                    List<Message> currentChat = new ArrayList<>();
                    for(Message s: chats){
                        if((s.getAccountRecieve().equals(user.getEmail()) && s.getAccountSend().equals(contactName)) || (s.getAccountSend().equals(user.getEmail()) && s.getAccountRecieve().equals(contactName))){
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
                    RecyclerView recyclerView = findViewById(R.id.chat_recycler_send);
                    MessageChannelAdapter adapter = new MessageChannelAdapter(MessageChannel.this, currentChat);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MessageChannel.this));
                    recyclerView.scrollToPosition(currentChat.size() - 1);
                }
            });


        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message = new Message();
                String content = msg.getText().toString();
                msg.getText().clear();
                message.setAccountSend(user.getEmail());
                message.setAccountRecieve(contactName);
                message.setContent(content);
                message.setTimestamp(new Date());
                db.collection("messages")
                        .add(message)
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
        });
    }
}