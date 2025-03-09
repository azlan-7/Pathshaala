package com.example.loginpage.models;

public class UserWiseEducation {
    private String userwiseEducationId;
    private String educationLevelId;
    private String userId;
    private String selfReferralCode;
    private String mobileNo;
    private String institutionName;
    private String passingYear;
    private String cityName;
    private String educationLevelName;
    private String username;

    // Constructor
    public UserWiseEducation(String userwiseEducationId, String educationLevelId, String userId,
                             String selfReferralCode, String mobileNo, String institutionName,
                             String passingYear, String cityName, String educationLevelName, String username) {
        this.userwiseEducationId = userwiseEducationId;
        this.educationLevelId = educationLevelId;
        this.userId = userId;
        this.selfReferralCode = selfReferralCode;
        this.mobileNo = mobileNo;
        this.institutionName = institutionName;
        this.passingYear = passingYear;
        this.cityName = cityName;
        this.educationLevelName = educationLevelName;
        this.username = username;
    }

    // Default constructor
    public UserWiseEducation() {}

    // Getters
    public String getUserwiseEducationId() { return userwiseEducationId; }
    public String getEducationLevelId() { return educationLevelId; }
    public String getUserId() { return userId; }
    public String getSelfReferralCode() { return selfReferralCode; }
    public String getMobileNo() { return mobileNo; }
    public String getInstitutionName() { return institutionName; }
    public String getPassingYear() { return passingYear; }
    public String getCityName() { return cityName; }
    public String getEducationLevelName() { return educationLevelName; }
    public String getUsername() { return username; }

    // Setters
    public void setUserwiseEducationId(String userwiseEducationId) { this.userwiseEducationId = userwiseEducationId; }
    public void setEducationLevelId(String educationLevelId) { this.educationLevelId = educationLevelId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setSelfReferralCode(String selfReferralCode) { this.selfReferralCode = selfReferralCode; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
    public void setPassingYear(String passingYear) { this.passingYear = passingYear; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public void setEducationLevelName(String educationLevelName) { this.educationLevelName = educationLevelName; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String toString() {
        return "UserWiseEducation{" +
                "userwiseEducationId='" + userwiseEducationId + '\'' +
                ", educationLevelId='" + educationLevelId + '\'' +
                ", userId='" + userId + '\'' +
                ", selfReferralCode='" + selfReferralCode + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", institutionName='" + institutionName + '\'' +
                ", passingYear='" + passingYear + '\'' +
                ", cityName='" + cityName + '\'' +
                ", educationLevelName='" + educationLevelName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
