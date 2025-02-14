package com.example.loginpage.models;

public class AwardModel {
    private String title, organisation, year, description;

    public AwardModel(String title, String organisation, String year, String description) {
        this.title = title;
        this.organisation = organisation;
        this.year = year;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getOrganisation() { return organisation; }
    public String getYear() { return year; }
    public String getDescription() { return description; }
}
