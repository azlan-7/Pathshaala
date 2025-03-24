package com.example.loginpage.models;

public class WorkExperienceModel {
    private String professionName, institutionName, designationName, workExperience, curPreExperience, professionId, userId;

    public WorkExperienceModel(String professionName, String institutionName, String designationName, String workExperience, String curPreExperience, String professionId, String userId) {
        this.professionName = professionName;
        this.institutionName = institutionName;
        this.designationName = designationName;
        this.workExperience = workExperience;
        this.curPreExperience = curPreExperience;
        this.professionId = professionId;
        this.userId = userId;
    }

    public String getProfessionName() { return professionName; }
    public String getInstitutionName() { return institutionName; }
    public String getDesignationName() { return designationName; }
    public String getWorkExperience() { return workExperience; }
    public String getCurPreExperience() { return curPreExperience; }
    public String getProfessionId() { return professionId; }
    public String getUserId() { return userId; }
}
