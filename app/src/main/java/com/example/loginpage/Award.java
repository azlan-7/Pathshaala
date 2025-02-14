package com.example.loginpage;

public class Award {
    private String awardName;
    private String organization;
    private String year;
    private String description;

    public Award(String awardName, String organization, String year, String description) {
        this.awardName = awardName;
        this.organization = organization;
        this.year = year;
        this.description = description;
    }

    public String getAwardName() { return awardName; }
    public String getOrganization() { return organization; }
    public String getYear() { return year; }
    public String getDescription() { return description; }
}
