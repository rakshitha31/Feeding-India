package com.android.developer.feedingindia.pojos;

public class FeedingIndiaDonor {

    private String name,email,password,mobileNumber,dateOfBirth,userType;
    private boolean requestedToBeAdmin;

    public FeedingIndiaDonor(){

    }

    public FeedingIndiaDonor(String name, String email, String password, String mobileNumber, String dateOfBirth, String userType, boolean requestedToBeAdmin) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobileNumber = mobileNumber;
        this.dateOfBirth = dateOfBirth;
        this.userType = userType;
        this.requestedToBeAdmin = requestedToBeAdmin;
    }

    public String getName() {
        return name;
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

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getUserType() {
        return userType;
    }

    public boolean isRequestedToBeAdmin() {
        return requestedToBeAdmin;
    }
}


