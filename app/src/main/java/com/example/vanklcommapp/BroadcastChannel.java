package com.example.vanklcommapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BroadcastChannel extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_channel);

        // Initialize UI components and set up listeners
        TextView textView = findViewById(R.id.textView);

        // Register broadcast receiver
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Handle broadcast message received
                String message = intent.getStringExtra("message");
                textView.setText(message);
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("com.example.vanklcommapp.MESSAGE_BROADCAST"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast receiver to avoid memory leaks
        unregisterReceiver(broadcastReceiver);
    }
}
