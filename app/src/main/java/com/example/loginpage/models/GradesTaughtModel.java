package com.example.loginpage.models;

public class GradesTaughtModel {
    private String subject;
    private String topic;
    private String gradeLevel;
    private String medium;

    public GradesTaughtModel(String subject, String topic, String gradeLevel, String medium) {
        this.subject = subject;
        this.topic = topic;
        this.gradeLevel = gradeLevel;
        this.medium = medium;
    }

    public String getSubject() { return subject; }
    public String getTopic() { return topic; }
    public String getGradeLevel() { return gradeLevel; }
    public String getMedium() { return medium; }
}
