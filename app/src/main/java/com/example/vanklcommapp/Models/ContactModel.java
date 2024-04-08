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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicReference;

public class ContactModel extends Observable {

    // Firebase authentication and database references
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseFirestore db;

    // Lists to store accounts, contacts, and user information
    public List<String> accounts;
    public List<String> contacts;
    public ArrayList<User> userList;

    // Constructor initializing Firebase components and data structures
    public ContactModel(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        userList = new ArrayList<>();
        accounts = new ArrayList<>();
        contacts = new ArrayList<>();
        System.out.println("Contact User: " + user);
    }

    // Utility method to remove duplicates from a string ArrayList
    public static ArrayList<String> removeDuplicates(ArrayList<String> list) {
        // Create a new ArrayList to store unique elements
        ArrayList<String> newList = new ArrayList<String>();

        // Traverse through the original list
        for (String element : list) {
            // If the element is not already in the newList, add it
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }

        // Return the new list without duplicates
        return newList;
    }

    // Method to retrieve and display contact list
    public void showContactList(){
        // Clear lists to avoid duplicates
        accounts.clear();
        contacts.clear();

        // Query to retrieve user ID based on email
        db.collection("users").whereEqualTo("email", user.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Extract user ID from query results
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            accounts.add(user.getUid());
                        }
                        // Retrieve the user ID
                        String uid = accounts.get(0);

                        // Query to retrieve contacts based on user ID
                        db.collection("contacts").whereEqualTo("accountRecieve", uid).get()
                                .addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()) {
                                        // Loop through contacts and retrieve user emails
                                        for (QueryDocumentSnapshot document : task2.getResult()) {
                                            Contact contact = document.toObject(Contact.class);
                                            // Query to retrieve user details based on contact's user ID
                                            db.collection("users").whereEqualTo(FieldPath.documentId(), contact.getAccountSend()).get()
                                                    .addOnCompleteListener(task3 -> {
                                                        if (task3.isSuccessful()) {
                                                            // Extract the user's email
                                                            for (QueryDocumentSnapshot document2 : task3.getResult()) {
                                                                User userContact = document2.toObject(User.class);
                                                                contacts.add(userContact.getEmail());
                                                            }

                                                            // Remove duplicates from contacts list
                                                            contacts = removeDuplicates((ArrayList<String>) contacts);

                                                            // Notify observers of successful contact retrieval
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

    // Method to search for users based on query
    public void searchUsers(String query) {
        userList.clear();
        // Query Firestore for users matching the search query
        db.collection("users").whereEqualTo("email", query).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        // Loop through query results and add to user list
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User userDoc = document.toObject(User.class);
                            if (user != null && !Objects.equals(userDoc.getEmail(), user.getEmail())) {
                                userList.add(userDoc);
                            }
                        }

                        // Notify observers of search results
                        setChanged();
                        notifyObservers("ContactSearch");

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    // Method to add a user to contacts
    public void addContact(User contactUser){
        // Query Firestore to retrieve user reference
        db.collection("users").whereEqualTo("email", user.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User userRef = new User();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userRef = (document.toObject(User.class));
                        }
                        // Contact must be added both ways
                        Contact contact = new Contact();
                        contact.setAccountRecieve(userRef.getUid());
                        contact.setAccountSend(contactUser.getUid());

                        Contact contactReverse = new Contact();
                        contactReverse.setAccountSend(userRef.getUid());
                        contactReverse.setAccountRecieve(contactUser.getUid());

                        // Add contacts to Firestore
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

                                        // Retrieve contact user's email
                                        db.collection("users").whereEqualTo(FieldPath.documentId(), contactUser.getUid()).get()
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        User userRef = new User();
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            userRef = (document.toObject(User.class));
                                                        }
                                                        // Add contact email to contacts list
                                                        contacts.add(userRef.getEmail());

                                                        // Notify observers of contact update
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

    public void makeAdmin(User contactUser) {
        //Find user based on userID and update the Role
        db.collection("users")
                .whereEqualTo(FieldPath.documentId(), contactUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User userRef = document.toObject(User.class);

                            // Update the admin field
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("role", "admin");

                            // Perform the update based on the exctracted document id
                            db.collection("users")
                                    .document(document.getId())
                                    .update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        System.out.println("Success");
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle errors
                                        Log.e(TAG, "Error updating user field", e);
                                    });
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}
