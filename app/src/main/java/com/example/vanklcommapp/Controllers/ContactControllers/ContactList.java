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

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ContactList extends AppCompatActivity implements Observer {
    Button buttonSearch;
    AccountModel accountModel;
    ContactModel contactModel;
    ListView listView;
    @Override
    protected void onStart() {
        super.onStart();

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Init contact views
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);


        accountModel = ((SystemManagement) getApplication()).getModelAccount();
        contactModel = ((SystemManagement) getApplication()).getModelContact();

        contactModel.addObserver(this);


        buttonSearch = findViewById(R.id.btnList);
        contactModel.showContactList();
        listView = (ListView) findViewById(R.id.mobile_list);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), ContactAdder.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals("ContactSuccess")){
            setContactListValues();
        }
    }
    public void setContactListValues(){
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_item, contactModel.contacts);
        listView.setAdapter(adapter);
    }
}