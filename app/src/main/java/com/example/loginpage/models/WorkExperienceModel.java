package com.example.loginpage.models;

public class WorkExperienceModel {
    private String userId;
    private String institutionName;
    private String designationName;
    private String workExperience;
    private String curPreExperience;
    private String professionId;  // ✅ Added
    private String professionName; // ✅ Added

    // ✅ Default Constructor (Fixes object creation issue)
    public WorkExperienceModel() {
    }

    // ✅ Parameterized Constructor (Updated)
// ✅ Corrected Constructor to Include Profession Fields
    public WorkExperienceModel(String professionName, String institutionName, String designationName, String workExperience, String curPreExperience, String professionId, String userId) {
        this.professionName = professionName;
        this.institutionName = institutionName;
        this.designationName = designationName;
        this.workExperience = workExperience;
        this.curPreExperience = curPreExperience;
        this.professionId = professionId;
        this.userId = userId;
    }




    // ✅ Setter Methods
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

    public void setProfessionId(String professionId) {
        this.professionId = professionId;
    }

    public void setProfessionName(String professionName) {
        this.professionName = professionName;
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

    public String getProfessionId() {
        return professionId;
    }

    public String getProfessionName() {
        return professionName;
    }
}
