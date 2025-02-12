package com.example.loginpage.models;

public class Education {
    private String institution;
    private String degree;
    private String year;

    // Constructor
    public Education(String institution, String degree, String year) {
        this.institution = institution;
        this.degree = degree;
        this.year = year;
    }

    // Getters
    public String getInstitution() {
        return institution;
    }

    public String getDegree() {
        return degree;
    }

    public String getYear() {
        return year;
    }

    // Setters (optional)
    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
