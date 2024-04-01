package com.example.vanklcommapp;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<User> userList;

    public MyAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new ViewHolder(view, this.userList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AtomicReference<String> userRef = new AtomicReference<>("");
        User contactUser = userList.get(position);
        holder.textViewName.setText(contactUser.getUsername() + ": " + contactUser.getEmail());
        db.collection("users")
                .whereEqualTo("email", user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User userCur = document.toObject(User.class);
                            userRef.set(userCur.getUid());
                        }
                        Contact contact = new Contact();
                        contact.setAccountRecieve(String.valueOf(userRef));
                        contact.setAccountSend(contactUser.getUid());
                        System.out.println(userRef);
                        System.out.println(contactUser.getUid());
                        AtomicInteger count = new AtomicInteger();
                        db.collection("contacts")
                                .whereEqualTo("accountRecieve", contact.getAccountRecieve())
                                .whereEqualTo("accountSend", contact.getAccountSend())
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        System.out.println("here");
                                        count.set(0);
                                        for (QueryDocumentSnapshot document : task2.getResult()) {
                                            count.getAndIncrement();
                                            System.out.println("contact exists");
                                        }
                                        System.out.println("here2");
                                        holder.buttonAdd.setEnabled(true);
                                        holder.buttonAdd.setText("Add contact");
                                        System.out.println(count);
                                        if (count.get() > 0){
                                            holder.buttonAdd.setEnabled(false);
                                            holder.buttonAdd.setText("Added");
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task2.getException());
                                    }
                                });

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });






    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewName;
        public Button buttonAdd;
        public FirebaseFirestore db;
        FirebaseUser user;
        User contactUser;
        String userRef;
        public ViewHolder(@NonNull View itemView, List<User> userList) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            buttonAdd = itemView.findViewById(R.id.buttonAdd);
            db = FirebaseFirestore.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();

            db.collection("users")
                    .whereEqualTo("email", user.getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User userCur = document.toObject(User.class);
                                userRef = userCur.getUid();
                            }


                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });

            if (userList.size() == 1){
                contactUser = userList.get(0);
            }
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Contact Must be added both ways
                    Contact contact = new Contact();
                    contact.setAccountRecieve(userRef);
                    contact.setAccountSend(contactUser.getUid());

                    Contact contactReverse = new Contact();
                    contactReverse.setAccountSend(userRef);
                    contactReverse.setAccountRecieve(contactUser.getUid());


                    db.collection("contacts")
                            .add(contactReverse)
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
                    db.collection("contacts")
                            .add(contact)
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
                    buttonAdd.setEnabled(false);
                    buttonAdd.setText("Added");
                }
            });
        }
    }
}
