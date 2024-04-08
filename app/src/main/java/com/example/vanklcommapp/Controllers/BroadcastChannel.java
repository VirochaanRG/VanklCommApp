package com.example.vanklcommapp.Controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanklcommapp.Application.SystemManagement;
import com.example.vanklcommapp.Controllers.Adapters.BroadcastChannelAdapter;
import com.example.vanklcommapp.Controllers.Adapters.MessageChannelAdapter;
import com.example.vanklcommapp.Controllers.MessageControllers.MessageChannel;
import com.example.vanklcommapp.Models.BroadcastModel;
import com.example.vanklcommapp.R;

import java.util.Observable;
import java.util.Observer;
/*
 * Activity responsible for Showing The broadcast channel and broadcasts sent out by the user
 * Also allows elgible users to send broadcasts.
 */

public class BroadcastChannel extends AppCompatActivity implements Observer {

    BroadcastModel broadcastModel; // Instance of BroadcastModel
    Button makeBroadcast; // Button for making a broadcast
    EditText getText; // EditText for entering broadcast message
    RecyclerView recyclerView; // RecyclerView for displaying broadcast messages

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_channel);

        // Get instance of BroadcastModel from the application
        broadcastModel = ((SystemManagement) getApplication()).getModelBroadcast();
        broadcastModel.addObserver(this); // Add this class as an observer to the BroadcastModel

        // Find views by their IDs
        makeBroadcast = findViewById(R.id.make_broadcast);
        getText = findViewById(R.id.bc_input);
        recyclerView = findViewById(R.id.chat_recycler_send);
        Button buttonReturn = findViewById(R.id.home);

        broadcastModel.getRole();
        broadcastModel.returnChannelMessages();

        // Set click listener for the make broadcast button
        makeBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the broadcast message from the EditText and send it
                String bcast = getText.getText().toString();
                getText.getText().clear();
                broadcastModel.send_broadcast(bcast);
            }
        });

        // Set click listener for the return button
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to the Main Activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Update method from the Observer interface to observe changes in the BroadcastModel
    @Override
    public void update(Observable o, Object arg) {
        // Update UI based on the notified change
        if(arg.equals("RoleUpdated")){
            // If user's role is updated, check if they are admin
            if(broadcastModel.userDoc.getRole().equals("admin")){

            } else {
                // Disable broadcast button for non-admin users
                makeBroadcast.setEnabled(false);
                makeBroadcast.setText("Not permitted to make Broadcast");
            }
        } else if (arg.equals("BroadcastUpdate")) {
            // If there's a broadcast update, update the RecyclerView
            updateRecycler();
        }
    }

    // Method to update the RecyclerView with new broadcast messages
    public void updateRecycler(){
        // Create an adapter to bind data to the view
        BroadcastChannelAdapter adapter = new BroadcastChannelAdapter(BroadcastChannel.this,broadcastModel.currentBroadcast);
        // Set RecyclerView settings
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(BroadcastChannel.this));
        recyclerView.scrollToPosition(broadcastModel.currentBroadcast.size() - 1);
    }
}
