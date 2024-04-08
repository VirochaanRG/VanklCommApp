package com.example.vanklcommapp.Controllers.AccountControllers;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vanklcommapp.Application.SystemManagement;
import com.example.vanklcommapp.Models.AccountModel;
import com.example.vanklcommapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Observable;
import java.util.Observer;

/*
 * Activity responsible for creating a new user account.
 */

public class CreateAccount extends AppCompatActivity implements Observer {
    // Views and components within Activity
    private EditText emailTextView, passwordTextView, usernameTextView, eIDTextView;
    private Button Btn;
    private ProgressBar progressbar;

    // Account Model to be accessed within this class
    private AccountModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Activity Creation
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Initialize views and components through id
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        eIDTextView = findViewById(R.id.employeeID);
        usernameTextView = findViewById(R.id.username);
        Btn = findViewById(R.id.btnregister);
        progressbar = findViewById(R.id.progressbar);

        // Initialize Model and subscribe to observe changes
        model = ((SystemManagement) getApplication()).getModelAccount();
        model.addObserver(this);

        // Set on Click Listener on Registration button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });
    }

    // Method to register a new user
    private void registerNewUser() {
        progressbar.setVisibility(View.VISIBLE); // Show progress bar

        // Get input values
        String email, password, username , eid;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();
        username = usernameTextView.getText().toString();
        eid = eIDTextView.getText().toString();

        // Validation to ensure all fields are set
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(eid) || TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "Please enter all fields!!", Toast.LENGTH_LONG).show();
            return;
        }

        // Call model to create account
        model.createAccount(email, password, eid, username);
    }

    // Observer pattern update method
    @Override
    public void update(Observable o, Object arg) {
        Log.d(TAG, "In Create Update");

        // If Create Account succeeds
        if(((String)arg).equals("CreateSuccess")){
            // Show success message
            Log.d(TAG, "Create Success");
            System.out.println(model.user);
            progressbar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
        } else if (((String)arg).equals("CreateFailure")) {
            // Show failure message
            Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later", Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
        }
    }
}