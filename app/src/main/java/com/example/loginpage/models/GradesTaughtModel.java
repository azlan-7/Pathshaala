package com.example.loginpage.models;

public class GradesTaughtModel {
    private String subject;
    private String topic;
    private String gradeLevel;
    private String userId;  // ✅ Add User ID

    public GradesTaughtModel(String subject, String topic, String gradeLevel, String userId) { // ✅ Updated Constructor
        this.subject = subject;
        this.topic = topic;
        this.gradeLevel = gradeLevel;
        this.userId = userId;
    }

    public String getSubject() { return subject; }
    public String getTopic() { return topic; }
    public String getGradeLevel() { return gradeLevel; }
    public String getUserId() { return userId; } // ✅ Getter for User ID
}

