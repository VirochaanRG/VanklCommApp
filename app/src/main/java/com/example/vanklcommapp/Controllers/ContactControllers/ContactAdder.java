package com.example.vanklcommapp.Controllers.ContactControllers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.example.vanklcommapp.Application.SystemManagement;
import com.example.vanklcommapp.Controllers.Adapters.ContactAdapter;
import com.example.vanklcommapp.Controllers.MainActivity;
import com.example.vanklcommapp.Models.AccountModel;
import com.example.vanklcommapp.Models.ContactModel;
import com.example.vanklcommapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Observable;
import java.util.Observer;

/*
 * Activity responsible for searching and adding contacts
 */

public class ContactAdder extends AppCompatActivity implements Observer {
    //Initialize views and components
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    Button buttonReturn;
    //Init Models
    AccountModel accountModel;
    ContactModel contactModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //INIT activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        //Retrieve Models
        accountModel = ((SystemManagement) getApplication()).getModelAccount();
        contactModel = ((SystemManagement) getApplication()).getModelContact();

        //Set as contact model observer
        contactModel.addObserver(this);



        //Set Up Recycler View for viewing in search
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ContactAdapter(contactModel.userList, contactModel, accountModel.currentUser.getRole());
        recyclerView.setAdapter(adapter);
        buttonReturn = findViewById(R.id.returnMain);
        SearchView searchView = findViewById(R.id.searchView);

        //Set text listener on the textview to find updates based on the query
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                contactModel.searchUsers(newText);
                return true;
            }
        });
        //Return to the home screen
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    //Observer updates the adapter
    @Override
    public void update(Observable o, Object arg) {
        if(arg.equals("ContactSearch")){
            System.out.println("Adapter Change");
            adapter.notifyDataSetChanged();
        }
    }
}