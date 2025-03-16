package com.example.loginpage.models;

public class WorkExperienceModel {
    private String userId;
    private String institutionName;
    private String designationName;
    private String workExperience;
    private String curPreExperience;

    // ✅ Default Constructor (Fixes object creation issue)
    public WorkExperienceModel() {
    }

    // ✅ Parameterized Constructor
    public WorkExperienceModel(String institutionName, String designationName, String workExperience, String curPreExperience, String userId) {
        this.institutionName = institutionName;
        this.designationName = designationName;
        this.workExperience = workExperience;
        this.curPreExperience = curPreExperience;
        this.userId = userId;
    }

    // ✅ Setter Methods (Fixes missing method issue)
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    public void setWorkExperience(String workExperience) {
        this.workExperience = workExperience;
    }

    public void setCurPreExperience(String curPreExperience) {
        this.curPreExperience = curPreExperience;
    }

    // ✅ Getter Methods
    public String getUserId() {
        return userId;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public String getDesignationName() {
        return designationName;
    }

    public String getWorkExperience() {
        return workExperience != null && !workExperience.isEmpty() ? workExperience : "Unknown";
    }

    public String getCurPreExperience() {
        return curPreExperience.equals("1") ? "Current" : "Previous";
    }
}
