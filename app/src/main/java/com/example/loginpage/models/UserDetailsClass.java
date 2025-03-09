package com.example.loginpage.models;

public class UserDetailsClass {
    private String UserId;
    private String username;

    private String password;
    private String userType;
    private String name;
    private String lastName;
    private String dateOfBirth;
    private String signUpType;
    private String countryCode;
    private String mobileNo;
    private String emailId;
    private String securityKey;
    private String selfReferralCode;
    private String custReferralCode;
    private String latitude;
    private String longitude;
    private String userImageName;

    // Default Constructor
    public UserDetailsClass() {
    }

    // Parameterized Constructor
    public UserDetailsClass(String username, String password, String userType, String name, String lastName,
                            String dateOfBirth, String signUpType, String countryCode, String mobileNo,
                            String emailId, String securityKey, String selfReferralCode, String custReferralCode,
                            String latitude, String longitude, String userImageName) {
        this.UserId = UserId;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.name = name;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.signUpType = signUpType;
        this.countryCode = countryCode;
        this.mobileNo = mobileNo;
        this.emailId = emailId;
        this.securityKey = securityKey;
        this.selfReferralCode = selfReferralCode;
        this.custReferralCode = custReferralCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userImageName = userImageName;
    }

    // Getters and Setters


    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getSignUpType() { return signUpType; }
    public void setSignUpType(String signUpType) { this.signUpType = signUpType; }

    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }

    public String getSecurityKey() { return securityKey; }
    public void setSecurityKey(String securityKey) { this.securityKey = securityKey; }

    public String getSelfReferralCode() { return selfReferralCode; }
    public void setSelfReferralCode(String selfReferralCode) { this.selfReferralCode = selfReferralCode; }

    public String getCustReferralCode() { return custReferralCode; }
    public void setCustReferralCode(String custReferralCode) { this.custReferralCode = custReferralCode; }

    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }

    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }

    public String getUserImageName() { return userImageName; }
    public void setUserImageName(String userImageName) { this.userImageName = userImageName; }
}
