package com.example.loginpage.api;

public class UserRequest {
    private String username;
    private String password;
    private String UserType;
    private String Name;
    private String DateOfBirth;
    private String signuptype;
    private String CountryCode;
    private String mobineno;
    private String emailId;
    private String SecurityKey;
    private String selfreferralcode;
    private String custreferralcode;
    private String latitude;
    private String longitude;
    private String UserImageName;

    public UserRequest(String username, String password, String UserType, String Name, String DateOfBirth,
                       String signuptype, String CountryCode, String mobineno, String emailId,
                       String SecurityKey, String selfreferralcode, String custreferralcode,
                       String latitude, String longitude, String UserImageName) {
        this.username = username;
        this.password = password;
        this.UserType = UserType;
        this.Name = Name;
        this.DateOfBirth = DateOfBirth;
        this.signuptype = signuptype;
        this.CountryCode = CountryCode;
        this.mobineno = mobineno;
        this.emailId = emailId;
        this.SecurityKey = SecurityKey;
        this.selfreferralcode = selfreferralcode;
        this.custreferralcode = custreferralcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.UserImageName = UserImageName;
    }
}
