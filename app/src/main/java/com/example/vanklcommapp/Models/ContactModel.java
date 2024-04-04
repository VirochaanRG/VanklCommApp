package com.example.vanklcommapp.Models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.vanklcommapp.Models.DataTypes.Contact;
import com.example.vanklcommapp.Models.DataTypes.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicReference;

public class ContactModel extends Observable {
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseFirestore db;
    public List<String> accounts;
    public List<String> contacts;
    public ArrayList<User> userList;
    public ContactModel(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        userList = new ArrayList<>();
        accounts = new ArrayList<>();
        contacts = new ArrayList<>();
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
        //Clear Lists for no duplicates
        accounts.clear();
        contacts.clear();
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
    public void searchUsers(String query) {
        userList.clear();
        db.collection("users").whereEqualTo("email", query).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User userDoc = document.toObject(User.class);
                            if (user != null && !Objects.equals(userDoc.getEmail(), user.getEmail())) {
                                userList.add(userDoc);
                            }
                        }

                        //Notify Observers that a user exists
                        setChanged();
                        notifyObservers("ContactSearch");

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    public void addContact(User contactUser){
        db.collection("users").whereEqualTo("email", user.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User userRef = new User();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userRef = (document.toObject(User.class));
                        }
                        //Contact Must be added both ways
                        Contact contact = new Contact();
                        contact.setAccountRecieve(userRef.getUid());
                        contact.setAccountSend(contactUser.getUid());

                        Contact contactReverse = new Contact();
                        contactReverse.setAccountSend(userRef.getUid());
                        contactReverse.setAccountRecieve(contactUser.getUid());

                        Task<DocumentReference> addTask1 = db.collection("contacts").add(contactReverse);
                        Task<DocumentReference> addTask2 = db.collection("contacts").add(contact);

                        Tasks.whenAllSuccess(addTask1, addTask2)
                                .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                                    @Override
                                    public void onSuccess(List<Object> list) {
                                        // Both tasks succeeded
                                        DocumentReference docRef1 = (DocumentReference) list.get(0);
                                        DocumentReference docRef2 = (DocumentReference) list.get(1);

                                        // Log success or perform further actions
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + docRef1.getId());
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + docRef2.getId());

                                        db.collection("users").whereEqualTo(FieldPath.documentId(), contactUser.getUid()).get()
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        User userRef = new User();
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            userRef = (document.toObject(User.class));
                                                        }
                                                        contacts.add(userRef.getEmail());

                                                        setChanged();
                                                        notifyObservers("ContactAdapter");
                                                    } else {
                                                        Log.d(TAG, "Error getting documents: ", task.getException());
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle failure
                                        Log.w(TAG, "Error adding documents", e);
                                    }
                                });
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

}
