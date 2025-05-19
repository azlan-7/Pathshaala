package com.example.loginpage.models;

import java.util.Date;

public class NotificationWithSender {
    private int id;
    private int senderId;
    private String title;
    private String message;
    private String type;
    private Date createdAt;
    private Date readAt;
    private String senderName;
    private String senderSelfReferralCode;

    public NotificationWithSender(int id, int senderId, String title, String message, String type, Date createdAt, Date readAt, String senderName, String senderSelfReferralCode) {
        this.id = id;
        this.senderId = senderId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.senderName = senderName;
        this.senderSelfReferralCode = senderSelfReferralCode;
    }

    // Default constructor (no-argument)
    public NotificationWithSender() {
    }

    // Getters and setters for all fields
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getReadAt() {
        return readAt;
    }

    public void setReadAt(Date readAt) {
        this.readAt = readAt;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderSelfReferralCode() {
        return senderSelfReferralCode;
    }

    public void setSenderSelfReferralCode(String senderSelfReferralCode) {
        this.senderSelfReferralCode = senderSelfReferralCode;
    }
}