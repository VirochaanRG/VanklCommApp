package com.example.vanklcommapp.Controllers;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.vanklcommapp.Application.SystemManagement;
import com.example.vanklcommapp.Controllers.ContactControllers.ContactList;
import com.example.vanklcommapp.Controllers.AccountControllers.Login;
import com.example.vanklcommapp.Controllers.MessageControllers.MessageContactChooser;
import com.example.vanklcommapp.Models.AccountModel;
import com.example.vanklcommapp.Models.BroadcastModel;
import com.example.vanklcommapp.Models.ContactModel;
import com.example.vanklcommapp.Models.MessageModel;
import com.example.vanklcommapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer {


    TextView textView;
    Button buttonLogout;
    Button buttonContact;
    FirebaseAuth mAuth;
    FirebaseUser user;
    AccountModel accountModel;
    Button buttonMessage;
    ContactModel contactModel;
    MessageModel messageModel;
    BroadcastModel broadcastModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Init activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Firebase
        mAuth = FirebaseAuth.getInstance();

        //Get components by ID
        textView = findViewById(R.id.user_details);
        buttonLogout = findViewById(R.id.logout);
        buttonContact = findViewById(R.id.contact);
        buttonMessage = findViewById(R.id.message);
        Button buttonBroadcast = findViewById(R.id.btn_broadcast);
        //Get account model
        accountModel = ((SystemManagement) getApplication()).getModelAccount();
        user = accountModel.user;
        accountModel.addObserver(this);
        accountModel.getUser();
        //Initialize other models if they are not null
        contactModel = ((SystemManagement) getApplication()).getModelContact();
        messageModel = ((SystemManagement) getApplication()).getModelMessage();
        broadcastModel = ((SystemManagement) getApplication()).getModelBroadcast();
        if (contactModel == null){
            contactModel = new ContactModel();
            ((SystemManagement) getApplication()).setModelContact(contactModel);
            contactModel = ((SystemManagement) getApplication()).getModelContact();
        }
        if (messageModel == null){
            messageModel = new MessageModel();
            ((SystemManagement) getApplication()).setModelMessage(messageModel);
            messageModel = ((SystemManagement) getApplication()).getModelMessage();
        }
        if (broadcastModel == null){
            broadcastModel = new BroadcastModel();
            ((SystemManagement) getApplication()).setModelBroadcast(broadcastModel);
            broadcastModel = ((SystemManagement) getApplication()).getModelBroadcast();
        }
        contactModel.addObserver(this);

        //If user is null return to Login else show the users Email and UID
        if (user == null){
            returnLogin();
        } else {
            textView.setText(user.getEmail() + " " + user.getUid());
        }

        //Listeners for the different buttons and routing to different views
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountModel.logout();
            }
        });
        buttonBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BroadcastChannel.class);
                startActivity(intent);
                finish();
            }
        });
        buttonContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goContact();
            }
        });
        buttonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMessaging();
            }
        });

    }
    //Functions to intent to required view
    private void goMessaging() {
        Intent intent = new Intent(getApplicationContext(), MessageContactChooser.class);
        startActivity(intent);
        finish();
    }

    private void goContact() {
        Intent intent = new Intent(getApplicationContext(), ContactList.class);
        startActivity(intent);
        finish();
    }

    private void returnLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals("Logout")){
            returnLogin();
        }
    }
}