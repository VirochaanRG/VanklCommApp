package com.example.vanklcommapp.Controllers.MessageControllers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vanklcommapp.Application.SystemManagement;
import com.example.vanklcommapp.Controllers.MainActivity;
import com.example.vanklcommapp.Controllers.Adapters.MessageChannelAdapter;
import com.example.vanklcommapp.Models.AccountModel;
import com.example.vanklcommapp.Models.DataTypes.Message;
import com.example.vanklcommapp.Models.MessageModel;
import com.example.vanklcommapp.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public class MessageChannel extends AppCompatActivity implements Observer {

    FirebaseUser user;
    MessageModel messageModel;
    RecyclerView recyclerView;
    AccountModel accountModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Init Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_channel);

        //Extract contact email from bundle (From pass through in MessageContactChooser)
        Bundle bundle = getIntent().getExtras();
        String contactEmail = bundle.getString("contact");

        //Get Model and Add Class as observer
        accountModel = ((SystemManagement) getApplication()).getModelAccount();
        messageModel = ((SystemManagement) getApplication()).getModelMessage();
        messageModel.addObserver(this);

        //Set User from Account Model
        user = accountModel.user;

        //Extract all components
        TextView tv = findViewById(R.id.contact_details);
        EditText msg = findViewById(R.id.chat_message_input);
        recyclerView = findViewById(R.id.chat_recycler_send);
        Button buttonReturn = findViewById(R.id.home);
        Button buttonSend = findViewById(R.id.message_send_btn);

        //The contact we are contacting
        tv.setText(contactEmail);

        //Call Model to return/get Messages from db
        messageModel.returnChannelMessages(contactEmail);

        //On button Return click head to MainActivity
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent to Main Activity
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Button to send Message
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating Message Object
                Message message = new Message();

                //Extracting string from the edit text and clearing
                String content = msg.getText().toString();
                msg.getText().clear();

                //Set Message values
                message.setAccountSend(user.getEmail());
                message.setAccountRecieve(contactEmail);
                message.setContent(content);
                message.setTimestamp(new Date());

                //Call Message Model to send the message
                messageModel.sendMessage(message);
            }
        });
    }

    public void updateRecycler(){
        //Update the recycler to include the new message by creating the adapter to bind data to view
        MessageChannelAdapter adapter = new MessageChannelAdapter(MessageChannel.this, messageModel.currentChat);
        //Set recycler view settings
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageChannel.this));
        recyclerView.scrollToPosition(messageModel.currentChat.size() - 1);
    }

    //When MessageModel Notifies we call updateRecycler
    @Override
    public void update(Observable o, Object arg) {
        if(arg.equals("MessageUpdate")){
            updateRecycler();
        }
    }
}