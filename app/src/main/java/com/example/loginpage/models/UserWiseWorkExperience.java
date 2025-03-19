package com.example.loginpage.models;

public class UserWiseWorkExperience {
    private String userId;
    private String institutionName;
    private String designationName;
    private String workExperience;
    private String curPreExperience;
    private String professionId;  // ✅ Added
    private String professionName;  // ✅ Added

    // ✅ Updated Constructor to Include ProfessionId & ProfessionName
    public UserWiseWorkExperience(String userId, String institutionName, String designationName, String workExperience, String curPreExperience, String professionId, String professionName) {
        this.userId = userId;
        this.institutionName = institutionName;
        this.designationName = designationName;
        this.workExperience = workExperience;
        this.curPreExperience = curPreExperience;
        this.professionId = professionId;
        this.professionName = professionName;
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

    public String getProfessionId() {  // ✅ Added
        return professionId;
    }

    public String getProfessionName() {  // ✅ Added
        return professionName;
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

    public void setProfessionId(String professionId) {  // ✅ Added
        this.professionId = professionId;
    }

    public void setProfessionName(String professionName) {  // ✅ Added
        this.professionName = professionName;
    }
}
