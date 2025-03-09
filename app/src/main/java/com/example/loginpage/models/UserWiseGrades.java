//package com.example.loginpage.models;
//
//public class UserWiseGrades {
//    private String userwiseGradesId;
//    private String userId;
//    private String selfReferralCode;
//    private String mobileNo;
//    private String currentProfessionRem;
//    private String subjectName;
//    private String subjectId;
//    private String gradeName;
//    private String username;
//    private String isActive;
//
//    // Constructor
//    public UserWiseGrades(String userwiseGradesId, String userId, String selfReferralCode, String mobileNo,
//                          String currentProfessionRem, String subjectName, String subjectId, String gradeName,
//                          String username, String isActive) {
//        this.userwiseGradesId = userwiseGradesId;
//        this.userId = userId;
//        this.selfReferralCode = selfReferralCode;
//        this.mobileNo = mobileNo;
//        this.currentProfessionRem = currentProfessionRem;
//        this.subjectName = subjectName;
//        this.subjectId = subjectId;
//        this.gradeName = gradeName;
//        this.username = username;
//        this.isActive = isActive;
//    }
//
//    // Getters
//    public String getUserwiseGradesId() {
//        return userwiseGradesId;
//    }
//
//    public String getUserId() {
//        return userId;
//    }
//
//    public String getSelfReferralCode() {
//        return selfReferralCode;
//    }
//
//    public String getMobileNo() {
//        return mobileNo;
//    }
//
//    public String getCurrentProfessionRem() {
//        return currentProfessionRem;
//    }
//
//    public String getSubjectName() {
//        return subjectName;
//    }
//
//    public String getSubjectId() {
//        return subjectId;
//    }
//
//    public String getGradename() {
//        return gradeName;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    public String getIsActive() {
//        return isActive;
//    }
//}




package com.example.loginpage.models;

public class UserWiseGrades {
    private String userwiseGradesId;
    private String userId;
    private String selfReferralCode;
    private String mobileNo;
    private String subjectName;
    private String subjectId;
    private String gradeName;
    private String username;
    private String isActive;

    // ✅ Updated Constructor (Removed currentProfessionRem)
    public UserWiseGrades(String userwiseGradesId, String userId, String selfReferralCode, String mobileNo,
                          String subjectName, String subjectId, String gradeName,
                          String username, String isActive) {
        this.userwiseGradesId = userwiseGradesId;
        this.userId = userId;
        this.selfReferralCode = selfReferralCode;
        this.mobileNo = mobileNo;
        this.subjectName = subjectName;
        this.subjectId = subjectId;
        this.gradeName = gradeName;
        this.username = username;
        this.isActive = isActive;
    }

    // Getters
    public String getUserwiseGradesId() {
        return userwiseGradesId;
    }

    public String getUserId() {
        return userId;
    }

    public String getSelfReferralCode() {
        return selfReferralCode;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public String getGradename() {
        return gradeName;
    }

    public String getUsername() {
        return username;
    }

    public String getIsActive() {
        return isActive;
    }
}
