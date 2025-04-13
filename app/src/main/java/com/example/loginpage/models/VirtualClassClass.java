package com.example.loginpage.models;

import java.sql.Timestamp;

public class VirtualClassClass {
    private int virtualClassAPICodeID;
    private int userId;
    private String selfReferralCode;
    private int classId; // Changed from classID to classId for consistency
    private int timeTableId; // Changed from timeTableID to timeTableId
    private String liveId;
    private Timestamp classStartTime; // Changed from String to Timestamp
    private Timestamp classEndTime; // Changed from String to Timestamp
    private String firstName; // Changed from 'name' to 'firstName' for clarity
    private String lastName;

    // No-argument constructor (Required for Firebase, Room, and some ORM libraries)
    public VirtualClassClass() {}

    // Full constructor
    public VirtualClassClass(int virtualClassAPICodeID, int userId, String selfReferralCode, int classId, int timeTableId,
                             String liveId, Timestamp classStartTime, Timestamp classEndTime, String firstName, String lastName) {
        this.virtualClassAPICodeID = virtualClassAPICodeID;
        this.userId = userId;
        this.selfReferralCode = selfReferralCode;
        this.classId = classId;
        this.timeTableId = timeTableId;
        this.liveId = liveId;
        this.classStartTime = classStartTime;
        this.classEndTime = classEndTime;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters and Setters
    public int getVirtualClassAPICodeID() { return virtualClassAPICodeID; }
    public void setVirtualClassAPICodeID(int virtualClassAPICodeID) { this.virtualClassAPICodeID = virtualClassAPICodeID; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSelfReferralCode() { return selfReferralCode; }
    public void setSelfReferralCode(String selfReferralCode) { this.selfReferralCode = selfReferralCode; }

    public int getClassId() { return classId; } // Renamed from getClassID() to match naming convention
    public void setClassId(int classId) { this.classId = classId; }

    public int getTimeTableId() { return timeTableId; } // Renamed from getTimeTableID()
    public void setTimeTableId(int timeTableId) { this.timeTableId = timeTableId; }

    public String getLiveId() { return liveId; }
    public void setLiveId(String liveId) { this.liveId = liveId; }

    public Timestamp getClassStartTime() { return classStartTime; } // Now returns Timestamp
    public void setClassStartTime(Timestamp classStartTime) { this.classStartTime = classStartTime; }

    public Timestamp getClassEndTime() { return classEndTime; } // Now returns Timestamp
    public void setClassEndTime(Timestamp classEndTime) { this.classEndTime = classEndTime; }

    public String getFirstName() { return firstName; } // Renamed from getName()
    public void setFirstName(String firstName) { this.firstName = firstName; } // Renamed from setName()

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
