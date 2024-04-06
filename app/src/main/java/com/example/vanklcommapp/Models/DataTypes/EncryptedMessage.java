package com.example.vanklcommapp.Models.DataTypes;

import java.util.Date;

public class EncryptedMessage {
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

    public EncryptedMessage(String accountSend, String accountRecieve, String content, Date timestamp) {
        this.accountSend = accountSend;
        this.accountRecieve = accountRecieve;
        this.content = content;
        this.timestamp = timestamp;
    }
    public EncryptedMessage() {

    }
    private String accountSend;
    private String accountRecieve;
    private String content;
    private Date timestamp;

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    private String sessionKey;
}
