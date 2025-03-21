package com.example.loginpage.models;

public class MessageModel {
    private String message;
    private boolean isUser; // true -> user message, false -> bot message

    public MessageModel(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
    }

    public String getMessage() {
        return message;
    }

    public boolean isUser() {
        return isUser;
    }
}
