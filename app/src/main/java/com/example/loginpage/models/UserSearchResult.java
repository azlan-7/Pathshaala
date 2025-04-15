package com.example.loginpage.models;

public class UserSearchResult {
    private int userId;
    private String userType;
    private int gradeId;
    private String gradeName;
    private int subjectId;
    private String subjectName;
    private String designationName;
    private String institutionName;
    private String username;
    private String mobileNo;
    private String selfReferralCode;
    private int currentProfession;
    private String currentProfessionRem;
    private String userImageName;
    private String email;


    // 👉 Generate Getters and Setters for all fields here

    public int getUserId() {
        return userId;
    }

    public String getUserType() {
        return userType;
    }

    public String getEmail(){
        return email;
    }

    public int getGradeId() {
        return gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getDesignationName() {
        return designationName;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public String getUsername() {
        return username;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getSelfReferralCode() {
        return selfReferralCode;
    }

    public int getCurrentProfession() {
        return currentProfession;
    }

    public String getCurrentProfessionRem() {
        return currentProfessionRem;
    }

    // Setters
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public void setSelfReferralCode(String selfReferralCode) {
        this.selfReferralCode = selfReferralCode;
    }

    public void setCurrentProfession(int currentProfession) {
        this.currentProfession = currentProfession;
    }

    public void setCurrentProfessionRem(String currentProfessionRem) {
        this.currentProfessionRem = currentProfessionRem;
    }

    public String getUserImageName() {
        return userImageName;
    }

    public void setUserImageName(String userImageName) {
        this.userImageName = userImageName;
    }

}
