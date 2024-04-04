package com.example.vanklcommapp.Application;

import android.app.Application;

import com.example.vanklcommapp.Models.AccountModel;
import com.example.vanklcommapp.Models.ContactModel;
import com.example.vanklcommapp.Models.MessageModel;


public class SystemManagement extends Application {

    public AccountModel getModelAccount() {
        return modelAccount;
    }

    public void setModelAccount(AccountModel modelAccount) {
        this.modelAccount = modelAccount;
        System.out.println("Account Model being Set");
    }

    private AccountModel modelAccount;

    public ContactModel getModelContact() {
        return modelContact;
    }

    public void setModelContact(ContactModel modelContact) {
        this.modelContact = modelContact;
        System.out.println("Contact Model being Set");
    }

    private ContactModel modelContact;

    public MessageModel getModelMessage() {
        return modelMessage;
    }

    public void setModelMessage(MessageModel modelMessage) {
        this.modelMessage = modelMessage;
    }

    private MessageModel modelMessage;

}
