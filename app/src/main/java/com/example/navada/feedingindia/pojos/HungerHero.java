package com.example.navada.feedingindia.pojos;

import java.util.ArrayList;

public class HungerHero {

    private String name,dateOfBirth,email,password,mobileNumber,userType,educationalBackground,state,city,locality,pinCode,
            reasonForJoining,affordableTime,responsibility,currentlyPartOf;
    private ArrayList<String> introducedToFIThrough,aboutMe;
    private boolean requestedToBeAdmin;

    public HungerHero(){

    }

    public HungerHero(String name, String dateOfBirth, String email, String password, String mobileNumber,String userType,
                      boolean requestedToBeAdmin,String educationalBackground, String state, String city, String locality, String pinCode,
                      String reasonForJoining, String affordableTime, String responsibility, String currentlyPartOf,
                      ArrayList<String> introducedToFIThrough, ArrayList<String> aboutMe) {

        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.password = password;
        this.mobileNumber = mobileNumber;
        this.userType = userType;
        this.requestedToBeAdmin = requestedToBeAdmin;
        this.educationalBackground = educationalBackground;
        this.state = state;
        this.city = city;
        this.locality = locality;
        this.pinCode = pinCode;
        this.reasonForJoining = reasonForJoining;
        this.affordableTime = affordableTime;
        this.responsibility = responsibility;
        this.currentlyPartOf = currentlyPartOf;
        this.introducedToFIThrough = introducedToFIThrough;
        this.aboutMe = aboutMe;
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isRequestedToBeAdmin() {
        return requestedToBeAdmin;
    }

    public String getCurrentlyPartOf() {
        return currentlyPartOf;
    }

    public String getEducationalBackground() { return educationalBackground; }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getLocality() {
        return locality;
    }

    public String getPinCode() {
        return pinCode;
    }

    public String getReasonForJoining() {
        return reasonForJoining;
    }

    public String getAffordableTime() {
        return affordableTime;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public ArrayList<String> getAboutMe() {
        return aboutMe;
    }

    public ArrayList<String> getIntroducedToFIThrough() {
        return introducedToFIThrough;
    }
}
