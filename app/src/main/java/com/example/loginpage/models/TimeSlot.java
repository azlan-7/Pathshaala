package com.example.loginpage.models;

public class TimeSlot {
    private String subject;
    private String grade;
    private String day;
    private String time;
    private String courseFee;
    private String batchCapacity;
    private String duration;

    public TimeSlot(String subject, String grade, String day, String time) {
        this.subject = subject;
        this.grade = grade;
        this.day = day;
        this.time = time;
        this.courseFee = "";
        this.batchCapacity = "";
        this.duration = "";
    }

    public TimeSlot(String subject, String grade, String day, String time, String courseFee, String batchCapacity, String duration) {
        this.subject = subject;
        this.grade = grade;
        this.day = day;
        this.time = time;
        this.courseFee = courseFee;
        this.batchCapacity = batchCapacity;
        this.duration = duration;
    }

    // Getters and Setters for all the fields
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCourseFee() {
        return courseFee;
    }

    public void setCourseFee(String courseFee) {
        this.courseFee = courseFee;
    }

    public String getBatchCapacity() {
        return batchCapacity;
    }

    public void setBatchCapacity(String batchCapacity) {
        this.batchCapacity = batchCapacity;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}