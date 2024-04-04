package com.example.vanklcommapp.Controllers.AccountControllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vanklcommapp.Application.SystemManagement;
import com.example.vanklcommapp.Controllers.AccountControllers.CreateAccount;
import com.example.vanklcommapp.Controllers.MainActivity;
import com.example.vanklcommapp.Models.AccountModel;
import com.example.vanklcommapp.Models.ContactModel;
import com.example.vanklcommapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Observable;
import java.util.Observer;

public class Login extends AppCompatActivity implements Observer {
    private EditText emailTextView, passwordTextView;
    private Button Btn, BtnCreate;
    private ProgressBar progressBar;
    private AccountModel accountModel;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    ContactModel model;
    @Override
    protected void onStart() {

        super.onStart();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Initialization of the View
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //App Launcher so setting Account Model in System
        accountModel = ((SystemManagement) getApplication()).getModelAccount();
        System.out.println("Account Model Init: " + accountModel);
        if(accountModel == null){
            accountModel = new AccountModel();
            ((SystemManagement) getApplication()).setModelAccount(accountModel);
            accountModel = ((SystemManagement) getApplication()).getModelAccount();
        }
        //Subscribing to model
        accountModel.addObserver(this);


        //Getting the user from the model
        FirebaseUser user = accountModel.user;

        if(user != null){
            goMain();
        }

        // Initializing Text Views for Email and Password
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        //Login Button and Create Button
        Btn = findViewById(R.id.login);
        BtnCreate = findViewById(R.id.create);

        //Progress Bar on Login
        progressBar = findViewById(R.id.progressBar);

        // Set on Click Listener on Sign-in button
        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginUserAccount();
            }
        });
        //Set on Click Listener on Create Account Button
        BtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goCreate();
            }
        });
    }
    //Function to intent to Main
    private void goMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
    //Function to intent to Create Account
    private void goCreate(){
        //Use Intent to go to Create Account View
        Intent intent = new Intent(getApplicationContext(), CreateAccount.class);
        startActivity(intent);
        finish();
    }

    private void loginUserAccount()
    {
        // show the visibility of progress bar to show loading into app
        progressBar.setVisibility(View.VISIBLE);

        // Take the value of two edit texts in Strings
        String email, password;
        email = emailTextView.getText().toString();
        password = passwordTextView.getText().toString();

        // validations for input email and password
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        //Call the Model for the login functionality
        accountModel.login(email, password);


    }

    @Override
    public void update(Observable o, Object arg) {
        //Recieve notification from AccountModel
        user = accountModel.user;
        System.out.println("LoginEntry");
        //IF user not null and LoginSuccess message then Login
        if(user != null && ((String)arg).equals("LoginSuccess")) {
            Toast.makeText(getApplicationContext(), "Login successful!!", Toast.LENGTH_LONG).show();

            // hide the progress bar
            progressBar.setVisibility(View.GONE);
            // if sign-in is successful
            // intent to home activity
            goMain();
        } else if(((String)arg).equals("LoginFail")){
            //Login Failure stay in Login.
            Toast.makeText(getApplicationContext(), "Login Failure!!", Toast.LENGTH_LONG).show();
        }
    }
}