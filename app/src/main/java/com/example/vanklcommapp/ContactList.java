package com.example.vanklcommapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ContactList extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseUser user;
    List<String> accounts;
    List <String> contacts = new ArrayList<>();
    @Override
    protected void onStart() {
        super.onStart();

    }
    public static ArrayList<String> removeDuplicates(ArrayList<String> list)
    {

        // Create a new ArrayList
        ArrayList<String> newList = new ArrayList<String>();

        // Traverse through the first list
        for (String element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {

                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


        Button buttonList;
        buttonList = findViewById(R.id.btnList);
        accounts = new ArrayList<>();
        contacts = new ArrayList<>();
        db.collection("users")
                .whereEqualTo("email", user.getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            accounts.add(user.getUid());
                        }
                        String uid = accounts.get(0);
                        db.collection("contacts")
                                .whereEqualTo("accountRecieve", uid)
                                .get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        System.out.println("entry");
                                        for (QueryDocumentSnapshot document : task2.getResult()) {
                                            Contact contact = document.toObject(Contact.class);

                                            db.collection("users")
                                                    .whereEqualTo(FieldPath.documentId(), contact.getAccountSend())
                                                    .get()
                                                    .addOnCompleteListener(task3 -> {
                                                        if (task3.isSuccessful()) {
                                                            System.out.println("entry2");
                                                            for (QueryDocumentSnapshot document2 : task3.getResult()) {
                                                                User userContact = document2.toObject(User.class);
                                                                contacts.add(userContact.getEmail());
                                                            }

                                                            contacts = removeDuplicates((ArrayList<String>) contacts);
                                                            ArrayAdapter adapter = new ArrayAdapter<String>(this,
                                                                    R.layout.activity_item, contacts);

                                                            ListView listView = (ListView) findViewById(R.id.mobile_list);
                                                            listView.setAdapter(adapter);

                                                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                                @Override
                                                                public void onItemClick(AdapterView<?> parent, View view,
                                                                                        int position, long id) {
                                                                    String value = (String)parent.getItemAtPosition(position);
                                                                    Intent intent = new Intent(getApplicationContext(), MessageChannel.class);
                                                                    intent.putExtra("contact", value);

                                                                }
                                                            });

                                                        } else {
                                                            Log.d(TAG, "Error getting documents: ", task3.getException());
                                                        }
                                                    });


                                        }



                                    } else {
                                        Log.d(TAG, "Error getting documents: ", task2.getException());
                                    }
                                });

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

        buttonList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), ContactController.class);
                startActivity(intent);
                finish();
            }
        });

    }
}