package com.example.loginpage.models;

public class ExtracurricularModel {
    private String activityName;
    private String hobby;

    // Constructor
    public ExtracurricularModel(String activityName, String hobby) {
        this.activityName = activityName;
        this.hobby = hobby;
    }

    // Getters
    public String getActivityName() {
        return activityName;
    }

    public String getHobby() {
        return hobby;
    }

    // Setters
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }
}
