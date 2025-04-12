package com.example.loginpage.models;

public class ParentGuardianInfo {
    private String fatherName;
    private String fatherContact;
    private String motherName;
    private String motherContact;
    private String guardianName;
    private String guardianContact;

    // Constructor
    public ParentGuardianInfo(String fatherName, String fatherContact, String motherName, String motherContact, String guardianName, String guardianContact) {
        this.fatherName = fatherName;
        this.fatherContact = fatherContact;
        this.motherName = motherName;
        this.motherContact = motherContact;
        this.guardianName = guardianName;
        this.guardianContact = guardianContact;
    }

    // Getter methods
    public String getFatherName() {
        return fatherName;
    }

    public String getFatherContact() {
        return fatherContact;
    }

    public String getMotherName() {
        return motherName;
    }

    public String getMotherContact() {
        return motherContact;
    }

    public String getGuardianName() {
        return guardianName;
    }

    public String getGuardianContact() {
        return guardianContact;
    }
}
