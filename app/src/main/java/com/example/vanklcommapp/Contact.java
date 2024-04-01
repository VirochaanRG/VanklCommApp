package com.example.vanklcommapp;

public class Contact {
    public String getAccountSend() {
        return accountSend;
    }

    public void setAccountSend(String accountSend) {
        this.accountSend = accountSend;
    }

    public String getAccountRecieve() {
        return accountRecieve;
    }

    public void setAccountRecieve(String accountRecieve) {
        this.accountRecieve = accountRecieve;
    }

    public Contact(){

    }

    public Contact(String accountSend, String accountRecieve) {
        this.accountSend = accountSend;
        this.accountRecieve = accountRecieve;
    }

    private String accountSend;
    private String accountRecieve;
}
