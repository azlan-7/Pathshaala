package com.example.loginpage;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private String title;
    private String message;
    private String type;
    private Timestamp createdAt;
    private Timestamp readAt;

    public Notification(int id, String title, String message, String type, Timestamp createdAt, Timestamp readAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    // Getters and toString() if needed
}
