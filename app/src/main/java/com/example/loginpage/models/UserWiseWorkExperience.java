package com.example.loginpage.models;

public class UserWiseWorkExperience {
    private String userId;
    private String institutionName;
    private String designationName;
    private String workExperience;
    private String curPreExperience;

    // ✅ Corrected Constructor
    public UserWiseWorkExperience(String userId, String institutionName, String designationName, String workExperience, String curPreExperience) {
        this.userId = userId;
        this.institutionName = institutionName;
        this.designationName = designationName;
        this.workExperience = workExperience;
        this.curPreExperience = curPreExperience;
    }

    // ✅ Add Getters
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
        return workExperience;
    }

    public String getCurPreExperience() {
        return curPreExperience;
    }

    // ✅ Add Setters (if needed)
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
}
