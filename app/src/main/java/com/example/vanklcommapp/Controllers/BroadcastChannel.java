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
import android.widget.ImageButton;

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

public class BroadcastChannel extends AppCompatActivity implements Observer {

    BroadcastModel broadcastModel;
    ImageButton makeBroadcast;
    EditText getText;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_channel);
        broadcastModel = ((SystemManagement) getApplication()).getModelBroadcast();
        broadcastModel.addObserver(this);
        makeBroadcast = findViewById(R.id.make_broadcast);
        broadcastModel.getRole();
        broadcastModel.returnChannelMessages();
        getText = findViewById(R.id.bc_input);
        recyclerView = findViewById(R.id.chat_recycler_send);
        Button buttonReturn = findViewById(R.id.home);
        makeBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bcast = getText.getText().toString();
                getText.getText().clear();
                broadcastModel.send_broadcast(bcast);
            }
        });
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent to Main Activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg.equals("RoleUpdated")){
            if (broadcastModel.userDoc.getRole().equals("admin")) {
                // If the user is an admin, show the text box and the make broadcast button
                getText.setVisibility(View.VISIBLE);
                makeBroadcast.setVisibility(View.VISIBLE);
            } else {
                // If the user is not an admin, hide the text box and the make broadcast button
                getText.setVisibility(View.GONE);
                makeBroadcast.setVisibility(View.GONE);
            }
            
        } else if (arg.equals("BroadcastUpdate")) {
            updateRecycler();
        }
    }
    public void updateRecycler(){
        //Update the recycler to include the new message by creating the adapter to bind data to view
        BroadcastChannelAdapter adapter = new BroadcastChannelAdapter(BroadcastChannel.this,broadcastModel.currentBroadcast);
        //Set recycler view settings
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(BroadcastChannel.this));
        recyclerView.scrollToPosition(broadcastModel.currentBroadcast.size() - 1);
    }

}
