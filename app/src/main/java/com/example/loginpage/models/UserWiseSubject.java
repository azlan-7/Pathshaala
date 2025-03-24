package com.example.loginpage.models;

public class UserWiseSubject {
    private String mobileNo;
    private String userwiseSubjectId;
    private String subjectId;
    private String userId;
    private String selfReferralCode;
    private String subjectName;
    private String username;

    // Constructor
    public UserWiseSubject(String mobileNo, String userwiseSubjectId, String subjectId, String userId,
                           String selfReferralCode, String subjectName, String username) {
        this.mobileNo = mobileNo;
        this.userwiseSubjectId = userwiseSubjectId;
        this.subjectId = subjectId;
        this.userId = userId;
        this.selfReferralCode = selfReferralCode;
        this.subjectName = subjectName;
        this.username = username;
    }

    // Default constructor (for Firebase or database deserialization)
    public UserWiseSubject() {}

    // Getters
    public String getMobileNo() {
        return mobileNo;
    }

    public String getUserwiseSubjectId() {
        return userwiseSubjectId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSelfReferralCode() {
        return selfReferralCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getUsername() {
        return username;
    }

    // Setters
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setUserwiseSubjectId(String userwiseSubjectId) {
        this.userwiseSubjectId = userwiseSubjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSelfReferralCode(String selfReferralCode) {
        this.selfReferralCode = selfReferralCode;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Optional: Override toString() for debugging
    @Override
    public String toString() {
        return "UserWiseSubject{" +
                "mobileNo='" + mobileNo + '\'' +
                ", userwiseSubjectId='" + userwiseSubjectId + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", userId='" + userId + '\'' +
                ", selfReferralCode='" + selfReferralCode + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
