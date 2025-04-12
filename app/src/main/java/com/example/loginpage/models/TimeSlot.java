package com.example.loginpage.models;

import java.io.Serializable;

public class TimeSlot implements Serializable {
    private String subject;
    private String grade;
    private String day;
    private String time;

    public TimeSlot(String subject, String grade, String day, String time) {
        this.subject = subject;
        this.grade = grade;
        this.day = day;
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public String getGrade() {
        return grade;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }
}
