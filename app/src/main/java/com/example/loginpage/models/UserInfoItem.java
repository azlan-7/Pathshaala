package com.example.loginpage.models;

import com.google.gson.annotations.SerializedName;

public class UserInfoItem {
    @SerializedName("slno")
    private String slno;

    @SerializedName("heading")
    private String heading;

    @SerializedName("description")
    private String description;

    public String getSlno() { return slno; }
    public void setSlno(String slno) { this.slno = slno; }

    public String getHeading() { return heading; }
    public void setHeading(String heading) { this.heading = heading; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
