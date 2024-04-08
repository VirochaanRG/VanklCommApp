package com.example.vanklcommapp.Application;

import android.app.Application;

import com.example.vanklcommapp.Models.AccountModel;
import com.example.vanklcommapp.Models.BroadcastModel;
import com.example.vanklcommapp.Models.ContactModel;
import com.example.vanklcommapp.Models.MessageModel;

/*
*
*   System Management Class which coordinates the models to ensure same instances are used through out the application
*   Primarily consists of Getter and Setters for each model type.
*   Class doesn't require a constructor as it loads on application creation.
*
* */
public class SystemManagement extends Application {

    // The 4 Models used within the application
    private AccountModel modelAccount;
    private ContactModel modelContact;
    private BroadcastModel modelBroadcast;
    private MessageModel modelMessage;

    // Getter and setter methods for AccountModel
    public AccountModel getModelAccount() {
        return modelAccount;
    }

    public void setModelAccount(AccountModel modelAccount) {
        this.modelAccount = modelAccount;
    }

    // Getter and setter methods for ContactModel
    public ContactModel getModelContact() {
        return modelContact;
    }

    public void setModelContact(ContactModel modelContact) {
        this.modelContact = modelContact;
    }

    // Getter and setter methods for BroadcastModel
    public BroadcastModel getModelBroadcast() {
        return modelBroadcast;
    }

    public void setModelBroadcast(BroadcastModel modelBroadcast) {
        this.modelBroadcast = modelBroadcast;
    }

    // Getter and setter methods for MessageModel
    public MessageModel getModelMessage() {
        return modelMessage;
    }

    public void setModelMessage(MessageModel modelMessage) {
        this.modelMessage = modelMessage;
    }
}
