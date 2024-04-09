package com.example.vanklcommapp.Controllers.ContactControllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.vanklcommapp.Application.SystemManagement;
import com.example.vanklcommapp.Models.AccountModel;
import com.example.vanklcommapp.Models.ContactModel;
import com.example.vanklcommapp.R;
import com.example.vanklcommapp.Controllers.MainActivity;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/*
 * Activity responsible for Showing all the users contacts.
 */

public class ContactList extends AppCompatActivity implements Observer {
    //Init Views and components
    Button buttonSearch;
    AccountModel accountModel;
    ContactModel contactModel;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize contact views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        // Get instances of AccountModel and ContactModel from the application
        accountModel = ((SystemManagement) getApplication()).getModelAccount();
        contactModel = ((SystemManagement) getApplication()).getModelContact();

        // Add this class as an observer to the ContactModel
        contactModel.addObserver(this);

        // Find views by their IDs
        buttonSearch = findViewById(R.id.btnList);
        listView = findViewById(R.id.mobile_list);

        // Set click listener for the search button
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent to contactadder when button clicked
                Intent intent = new Intent(getApplicationContext(), ContactAdder.class);
                startActivity(intent);
                finish();
            }
        });
        Button buttonReturn = findViewById(R.id.home);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent to Main Activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Show the contact list
        contactModel.showContactList();
    }

    // Update method from the Observer interface to observe changes in the ContactModel
    @Override
    public void update(Observable o, Object arg) {
        // Update contact list when a successful contact operation is notified
        if (arg.equals("ContactSuccess")) {
            setContactListValues();
        }
    }

    // Method to set values in the contact list view
    public void setContactListValues() {
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_item, contactModel.contacts);
        listView.setAdapter(adapter);
    }
}