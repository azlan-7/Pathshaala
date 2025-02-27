package com.example.loginpage.models;

public class WorkExperienceModel {
    private String profession, institution, designation, experience,experienceType;

    public WorkExperienceModel(String profession, String institution, String designation, String experience, String experienceType) {
        this.profession = profession;
        this.institution = institution;
        this.designation = designation;
        this.experience = experience;
        this.experienceType = experienceType;
    }

    public String getProfession() {
        return profession;
    }

    public String getInstitution() {
        return institution;
    }

    public String getDesignation() {
        return designation;
    }

    public String getExperience() {
        return experience;
    }


    public String getExperienceType() {
        return experienceType;
    }

    public void setExperienceType(String experienceType) {
        this.experienceType = experienceType;
    }


}
