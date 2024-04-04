package com.example.vanklcommapp.Models;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.vanklcommapp.MessageChannel;
import com.example.vanklcommapp.Models.DataTypes.Contact;
import com.example.vanklcommapp.Models.DataTypes.User;
import com.example.vanklcommapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ContactModel extends Observable {
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseFirestore db;
    public List<String> accounts;
    public List<String> contacts;
    public ContactModel(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        System.out.println("Contact User: " + user);
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
    public void showContactList(){
        //Init Account lists and Contact Lists
        accounts = new ArrayList<>();
        contacts = new ArrayList<>();
        //Query Database to get the User with given Email
        db.collection("users").whereEqualTo("email", user.getEmail()).get()
                .addOnCompleteListener(task -> {
                    //If task has been successful in getting user
                    if (task.isSuccessful()) {
                        //Extract the users ID by looping through results
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            accounts.add(user.getUid());
                        }
                        //This code ensures we get only the Current User ID
                        String uid = accounts.get(0);

                        //Loop through Contacts db and find all contacts with accountRecieve with given userID
                        db.collection("contacts").whereEqualTo("accountRecieve", uid).get()
                                .addOnCompleteListener(task2 -> {
                                    //IF task has been successful in getting contacts
                                    if (task2.isSuccessful()) {

                                        //Loops through every Single contact for this user
                                        for (QueryDocumentSnapshot document : task2.getResult()) {
                                            Contact contact = document.toObject(Contact.class);

                                            //This query is for finding the emails based on the userID of the contact
                                            db.collection("users").whereEqualTo(FieldPath.documentId(), contact.getAccountSend()).get()
                                                    .addOnCompleteListener(task3 -> {
                                                        //IF task has been successful in getting user
                                                        if (task3.isSuccessful()) {
                                                            //Extract Each Users Email
                                                            for (QueryDocumentSnapshot document2 : task3.getResult()) {
                                                                User userContact = document2.toObject(User.class);
                                                                contacts.add(userContact.getEmail());
                                                            }

                                                            //Remove Duplicate Contacts (Testing ENV)
                                                            contacts = removeDuplicates((ArrayList<String>) contacts);

                                                            //Notify Observers that a contact email has been added.
                                                            setChanged();
                                                            notifyObservers("ContactSuccess");
                                                        } else {
                                                            Log.d(TAG, "Error getting user documents: ", task3.getException());
                                                        }
                                                    });
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting contact documents: ", task2.getException());
                                    }
                                });
                    } else {
                        Log.d(TAG, "Error getting user documents: ", task.getException());
                    }
                });
    }
}
