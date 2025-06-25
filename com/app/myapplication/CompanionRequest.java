package com.app.myapplication;

public class CompanionRequest {
    private String date;
    private String time;
    private String location;
    private String modeOfTravel;
    private String userId;
    private String userName;  // New field for user name

    public CompanionRequest() {
        // Default constructor required for Firebase
    }

    public CompanionRequest(String date, String time, String location, String modeOfTravel, String userId) {
        this.date = date;
        this.time = time;
        this.location = location;
        this.modeOfTravel = modeOfTravel;
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getModeOfTravel() {
        return modeOfTravel;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
