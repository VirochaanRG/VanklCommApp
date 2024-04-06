package com.example.vanklcommapp.Controllers.MessageControllers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vanklcommapp.Application.SystemManagement;
import com.example.vanklcommapp.Controllers.MainActivity;
import com.example.vanklcommapp.Models.ContactModel;
import com.example.vanklcommapp.Models.MessageModel;
import com.example.vanklcommapp.R;

import java.util.Observable;
import java.util.Observer;

public class MessageContactChooser extends AppCompatActivity implements Observer {
    ContactModel contactModel;
    ListView listView;
    MessageModel messageModel;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_contact_chooser);
        listView = (ListView) findViewById(R.id.mobile_list);

        contactModel = ((SystemManagement) getApplication()).getModelContact();
        contactModel.addObserver(this);

        Button buttonHome;
        buttonHome = findViewById(R.id.btnList);
        contactModel.showContactList();
        messageModel = ((SystemManagement) getApplication()).getModelMessage();
        messageModel.addObserver(this);
        progressBar = findViewById(R.id.progressBar);
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView c = view.findViewById(R.id.label);
                String messageVal = c.getText().toString();

                progressBar.setVisibility(View.VISIBLE);
                messageModel.authenticate(messageVal);
            }
        });
    }
    @Override
    public void update(Observable o, Object arg) {
        if (arg.equals("ContactSuccess")){
            setContactListValues();
        } else if (arg.equals("AuthenticateSuccess")) {
            progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(getApplicationContext(), MessageChannel.class);
            intent.putExtra("contact", messageModel.targetEmail);
            startActivity(intent);
            finish();
        }
    }
    public void setContactListValues(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.activity_item, contactModel.contacts);
        listView.setAdapter(adapter);
    }
}