package com.example.loginpage.models;

public class CertificationModel {
    private String name, organisation, year, credentialUrl, imageUri;

    public CertificationModel(String name, String organisation, String year, String credentialUrl, String imageUri) {
        this.name = name;
        this.organisation = organisation;
        this.year = year;
        this.credentialUrl = credentialUrl;
        this.imageUri = imageUri;
    }

    public String getName() { return name; }
    public String getOrganisation() { return organisation; }
    public String getYear() { return year; }
    public String getCredentialUrl() { return credentialUrl; }
    public String getImageUri() { return imageUri; }
}











//package com.example.loginpage.models;
//
//public class CertificationModel {
//    private String title;
//    private String organisation;
//    private String year;
//    private String credentialUrl;
//
//    public CertificationModel(String title, String organisation, String year, String credentialUrl) {
//        this.title = title;
//        this.organisation = organisation;
//        this.year = year;
//        this.credentialUrl = credentialUrl;
//    }
//
//    public String getTitle() { return title; }
//    public String getOrganisation() { return organisation; }
//    public String getYear() { return year; }
//    public String getCredentialUrl() { return credentialUrl; }
//}
