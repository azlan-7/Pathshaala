package com.example.loginpage.models;

public class ParentInfoModel {
    public String fatherName;
    public String fatherContact;
    public String motherName;
    public String motherContact;
    public String guardianName;
    public String guardianRelation;
    public String guardianContact;

    public ParentInfoModel(String fatherName, String fatherContact, String motherName, String motherContact,
                           String guardianName, String guardianRelation, String guardianContact) {
        this.fatherName = fatherName;
        this.fatherContact = fatherContact;
        this.motherName = motherName;
        this.motherContact = motherContact;
        this.guardianName = guardianName;
        this.guardianRelation = guardianRelation;
        this.guardianContact = guardianContact;
    }
}
