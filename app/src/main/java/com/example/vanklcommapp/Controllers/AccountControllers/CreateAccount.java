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

public class CreateAccount extends AppCompatActivity implements Observer {
    private EditText emailTextView, passwordTextView, usernameTextView, eIDTextView;
    private Button Btn;
    private ProgressBar progressbar;

    private AccountModel model;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Initialization of view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // initialising all views through id
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        eIDTextView = findViewById(R.id.employeeID);
        usernameTextView = findViewById(R.id.username);
        //Initializing Button to register
        Btn = findViewById(R.id.btnregister);
        //Progress bar for creating account
        progressbar = findViewById(R.id.progressbar);

        //Initialize Model and subscribe
        model = ((SystemManagement) getApplication()).getModelAccount();
        model.addObserver(this);

        // Set on Click Listener on Registration button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                registerNewUser();
            }
        });
    }

    private void registerNewUser()
    {

        // show the visibility of progress bar to show loading
        progressbar.setVisibility(View.VISIBLE);

        // Take the value of edit texts in Strings
        String email, password, username , eid;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();
        username = usernameTextView.getText().toString();
        eid = eIDTextView.getText().toString();

        // Validations for inputs
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(eid) || TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter all fields!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        //Call model to create account
        model.createAccount(email, password, eid, username);
    }

    @Override
    public void update(Observable o, Object arg) {
        Log.d(TAG, "In Create Update");
        //If Create Account succeeds
        if(((String)arg).equals("CreateSuccess")){
            //if Toast that account was created
            Log.d(TAG, "Create Success");
            System.out.println(model.user);
            progressbar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();

        } else if (((String)arg).equals("CreateFailure")) {
            // Registration failed
            Toast.makeText(getApplicationContext(), "Registration failed!!" + " Please try again later",
                            Toast.LENGTH_LONG).show();
            progressbar.setVisibility(View.GONE);
        }
    }
}