package com.example.vanklcommapp.Models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.vanklcommapp.KDC.NetworkAcess.AuthenticationNetworkTask;
import com.example.vanklcommapp.KDC.NetworkAcess.BasicNetworkTask;
import com.example.vanklcommapp.Models.DataTypes.User;
import com.example.vanklcommapp.KDC.NetworkAcess.NetworkTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.ExecutionException;

/** @noinspection ALL*/
public class AccountModel extends Observable {


    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseFirestore db;
    public AccountModel() {
        //Init Firebase Utils
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        System.out.println("Account Model: " + user);
    }
    public void testServer(){
        db.collection("users").whereEqualTo("email", user.getEmail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User userDoc = document.toObject(User.class);
                            System.out.println(userDoc.getKey());
                            byte[] bA = userDoc.getKey().toBytes();
                            System.out.println(bytesToHex(bA));
                            String val = bytesToHex(bA);
                            // URL to which the request will be sent
                            String url = "https://vanklwebserver.onrender.com/authenticate?email=v@v.com&target=t@t.com&nonce=12345";
                            // Create a URL object from the specified URL
                            NetworkTask nt = new AuthenticationNetworkTask();
                            nt.execute(url);
                            try {
                                String result = nt.get();

                            } catch (ExecutionException e) {
                                throw new RuntimeException(e);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            // Print response
                        }

                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
    public static String bytesToHex(byte[] bytes) {
        BigInteger bigInt = new BigInteger(1, bytes);
        String hexString = bigInt.toString(16);
        // Adjust length if necessary (prepend zeros)
        int paddingLength = (bytes.length * 2) - hexString.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hexString;
        } else {
            return hexString;
        }
    }
    public void login(String email, String password){
        //Login with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            //On completion
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                //If succeeded notify required controller with Login Success message
                                if (task.isSuccessful()) {
                                    System.out.println("LoginSuccess");
                                    //Ensure we set User after logging in and Notify Relevant observers
                                    user = mAuth.getCurrentUser();
                                    setChanged();
                                    notifyObservers("LoginSuccess");
                                }
                                else {
                                    // sign-in failed notify relevant observers
                                    System.out.println("LoginFail");
                                    setChanged();
                                    notifyObservers("LoginFail");
                                }
                            }
                        });
    }
    public void createAccount(String email, String password, String eid, String username){
        // create new user and add to users database
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful()) {

                            //Create user Hashmap
                            Map<String, Object> user = new HashMap<>();
                            user.put("username", username);
                            user.put("password", password);
                            user.put("email", email);
                            user.put("employeeID", eid);
                            user.put("role", "employee");

                            // Add a new user with a generated ID
                            db.collection("users")
                                    .add(user)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        //On success notify relevant observer
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                            String url = "https://vanklwebserver.onrender.com/updatedb?";
                                            url += "email=" + email;
                                            NetworkTask nt = new BasicNetworkTask();
                                            nt.execute(url);
                                            NetworkTask nt2 = new BasicNetworkTask();
                                            nt2.execute("https://vanklwebserver.onrender.com/getkeys");
                                            setChanged();
                                            notifyObservers("CreateSuccess");
                                            login(email, password);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                            setChanged();
                                            notifyObservers("CreateFailure");
                                        }
                                    });
                        }
                        else {
                            setChanged();
                            notifyObservers("CreateFailure");
                        }
                    }
                });
    }
    public void logout(){
        FirebaseAuth.getInstance().signOut();
        user = mAuth.getCurrentUser();
        System.out.println("Logout: " + user);
        setChanged();
        notifyObservers("Logout");
    }
}

