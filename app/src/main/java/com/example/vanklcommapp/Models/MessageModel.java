package com.example.vanklcommapp.Models;

import static android.content.ContentValues.TAG;



import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vanklcommapp.KDC.NetworkAcess.AuthenticationNetworkTask;
import com.example.vanklcommapp.KDC.Decrypter;
import com.example.vanklcommapp.KDC.Encrypter;
import com.example.vanklcommapp.Models.DataTypes.EncryptedMessage;
import com.example.vanklcommapp.Models.DataTypes.Message;
import com.example.vanklcommapp.Models.DataTypes.User;
import com.example.vanklcommapp.KDC.NetworkAcess.NetworkTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutionException;


public class MessageModel extends Observable {
        public FirebaseAuth mAuth;
        public FirebaseUser user;
        public FirebaseFirestore db;
        public List<EncryptedMessage> currentChat;

        public String currentNonce;

        public String sessionKey;
        public String targetEmail;
        public MessageModel(){
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            user = mAuth.getCurrentUser();
            currentChat = new ArrayList<>();
        }
    // Method to send a message and store both plaintext and encrypted versions
    public void sendMessage(Message m){
        // Add plaintext message to the "messages" collection
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

        // Encrypt the message and add it to the "encryptedMessages" collection
        EncryptedMessage eMsg = new EncryptedMessage();
        eMsg.setAccountSend(m.getAccountSend());
        eMsg.setAccountRecieve(m.getAccountRecieve());
        String ecContent = Encrypter.encrypt(sessionKey, m.getContent());
        eMsg.setContent(ecContent);
        eMsg.setTimestamp(m.getTimestamp());
        eMsg.setSessionKey(sessionKey);

        db.collection("encryptedMessages").add(eMsg)
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

    // Method to authenticate the current user with a target user
    public void authenticate(String target){
        db.collection("users").whereEqualTo("email", user.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User userDoc = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userDoc = document.toObject(User.class);
                        }
                        // Obtain the user's key and convert it to hex string
                        System.out.println(userDoc.getKey());
                        byte[] bA = userDoc.getKey().toBytes();
                        System.out.println(Encrypter.bytesToHex(bA));
                        String key = Encrypter.bytesToHex(bA);

                        //Generate Random Nonce
                        Random rand = new Random();
                        int nonce = rand.nextInt(65555);
                        String nonceVal = Integer.toString(nonce);
                        this.currentNonce = nonceVal;
                        this.targetEmail = target;

                        // URL for authentication request
                        String url = "https://vanklwebserver.onrender.com/authenticate?email=" + user.getEmail() + "&target="+target + "&nonce=" + nonceVal;
                        System.out.println(url);
                        NetworkTask nt = new AuthenticationNetworkTask();
                        nt.execute(url);
                        try {
                            //Get response
                            String result = nt.get();
                            System.out.println(key);
                            try{
                                // Parse JSON response
                                JSONObject jsonObject = new JSONObject(result);
                                String header = jsonObject.getString("header");
                                String sessionKeyValue = jsonObject.getString("session_key");
                                if(header.equals("New")){
                                    // Extract details from the response
                                    String nonceValue = jsonObject.getString("nonce");
                                    String targetValue = jsonObject.getString("target");
                                    String senderValue = jsonObject.getString("sender");

                                    // Print extracted values
                                    System.out.println("Nonce: " + nonceValue);
                                    System.out.println("Session Key: " + sessionKeyValue);
                                    System.out.println("Target: " + targetValue);
                                    System.out.println("Sender: " + senderValue);

                                    this.sessionKey = sessionKeyValue;

                                    // Decrypt nonce and target from response
                                    String dcNonce = Decrypter.decrypt(key, nonceValue);
                                    String dcTarget= Decrypter.decrypt(key, targetValue);

                                    System.out.println("Decrypted Nonce: " + Decrypter.decrypt(key, nonceValue));
                                    System.out.println("Decrypted Target: " + Decrypter.decrypt(key, targetValue));

                                    // Check if decrypted nonce and target match and go to stage 2
                                    if(Objects.equals(dcNonce, this.currentNonce) && Objects.equals(dcTarget, this.targetEmail)){
                                        System.out.println("Authentication Stage 1 is successful");
                                        authenticateStage2(senderValue, dcTarget);
                                    } else {
                                        System.out.println("Not successful");
                                    }
                                } else {
                                    //If session is already open then authentication not necessary
                                    System.out.println("Session key exists. Session is Open");
                                    this.sessionKey = sessionKeyValue;
                                    setChanged();
                                    notifyObservers("AuthenticateSuccess");
                                }
                            }
                            catch(JSONException e){
                                System.out.println("JSON ERROR");
                            }
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    // Method to perform stage 2 of authentication
    public void authenticateStage2(String sender, String target){
        //Check if the sender is authenticated
        db.collection("users").whereEqualTo("email", target).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User userDoc = null;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userDoc = document.toObject(User.class);
                        }
                        // Obtain the key of the target user and convert it to hex string
                        byte[] bA = userDoc.getKey().toBytes();
                        String key = Encrypter.bytesToHex(bA);
                        // Decrypt the sender's email using the key
                        String dcSender = Decrypter.decrypt(key, sender);
                        // Check if the decrypted sender's email matches the current user's email
                        if(dcSender.equals(user.getEmail())){
                            System.out.println("Other User is also Authenticated");
                            // Notify observers of successful authentication
                            setChanged();
                            notifyObservers("AuthenticateSuccess");
                        } else {
                            System.out.println("Authentication Error");
                        }
                        //Notify Observers that a user exists
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    // Method to retrieve encrypted messages between the current user and a specific contact
    public void returnChannelMessages(String contactEmail){
        // Listen for changes in encrypted messages
        db.collection("encryptedMessages")
                .addSnapshotListener(new EventListener<QuerySnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        currentChat.clear();
                        // Retrieve encrypted messages and filter for the current chat
                        List<EncryptedMessage> chats = snapshot.toObjects(EncryptedMessage.class);
                        for(EncryptedMessage s: chats){
                            if((s.getAccountRecieve().equals(user.getEmail()) && s.getAccountSend().equals(contactEmail)) || (s.getAccountSend().equals(user.getEmail()) && s.getAccountRecieve().equals(contactEmail))){
                                currentChat.add(s);
                            }
                        }
                        // Sort messages by timestamp
                        Collections.sort(currentChat, new Comparator<EncryptedMessage>() {
                            @Override
                            public int compare(EncryptedMessage m1, EncryptedMessage m2) {
                                return m1.getTimestamp().compareTo(m2.getTimestamp());
                            }
                        });
                        // Notify observers of message update
                        setChanged();
                        notifyObservers("MessageUpdate");
                    }
                });
    }

    // Method to deauthenticate the current user
    public void deauthenticate() {
        // Execute network task to deauthenticate
        NetworkTask nt = new AuthenticationNetworkTask();
        String url = "https://vanklwebserver.onrender.com/deauthenticate?email=" + user.getEmail();
        nt.execute(url);
    }
}
