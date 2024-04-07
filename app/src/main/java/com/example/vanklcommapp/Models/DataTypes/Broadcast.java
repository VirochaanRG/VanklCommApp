package com.example.vanklcommapp.Models.DataTypes;

import java.util.Date;

public class Broadcast {
    public String getAccountSend() {
        return accountSend;
    }

    public void setAccountSend(String accountSend) {
        this.accountSend = accountSend;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    private String accountSend;
    private String content;
    private Date timestamp;

    public Broadcast() {
    }

    public Broadcast(String accountSend, String content, Date timestamp) {
        this.accountSend = accountSend;
        this.content = content;
        this.timestamp = timestamp;
    }
}

