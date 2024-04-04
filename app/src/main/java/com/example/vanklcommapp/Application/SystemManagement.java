package com.example.vanklcommapp.Application;

import android.app.Application;

import com.example.vanklcommapp.Models.AccountModel;
import com.example.vanklcommapp.Models.ContactModel;
import com.google.firebase.FirebaseApp;

public class SystemManagement extends Application {

    public AccountModel getModelAccount() {
        return modelAccount;
    }

    public void setModelAccount(AccountModel modelAccount) {
        this.modelAccount = modelAccount;
    }

    private AccountModel modelAccount;

    public ContactModel getModelContact() {
        return modelContact;
    }

    public void setModelContact(ContactModel modelContact) {
        this.modelContact = modelContact;
    }

    private ContactModel modelContact;

}
