package com.example.vanklcommapp;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.example.vanklcommapp.Application.SystemManagement;
import com.example.vanklcommapp.Controllers.MainActivity;
import com.example.vanklcommapp.Models.AccountModel;
import com.example.vanklcommapp.Models.ContactModel;
import com.example.vanklcommapp.Models.DataTypes.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class ContactAdder extends AppCompatActivity implements Observer {
    private FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser userCur;
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private ArrayList<User> userList;
    Button buttonReturn;

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

        contactModel.addObserver(this);


        mAuth = FirebaseAuth.getInstance();
        userCur = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        //

        userList = new ArrayList<>();

        System.out.println(userCur);

        //Set Up Recycler View for viewing in search
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter(userList);
        recyclerView.setAdapter(adapter);
        buttonReturn = findViewById(R.id.returnMain);
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return true;
            }
        });
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void searchUsers(String query) {
        db.collection("users")
                .whereEqualTo("email", query)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            if (userCur != null && !Objects.equals(user.getEmail(), userCur.getEmail())) {
                                userList.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}