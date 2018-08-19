package com.example.navada.feedingindia.pojos;

import java.util.HashMap;

public class DonationDetails {

    private String foodDescription,foodPreparedOn,additionalContactNumber,status,userContactNumber;
    private boolean hasContainer,canDonate;
    private HashMap<String,String> address;

    public DonationDetails() {

    }

    public DonationDetails(String foodDescription, String foodPreparedOn, String additionalContactNumber, String status, String userContactNumber, boolean hasContainer, boolean canDonate, HashMap<String, String> address) {
        this.foodDescription = foodDescription;
        this.foodPreparedOn = foodPreparedOn;
        this.additionalContactNumber = additionalContactNumber;
        this.status = status;
        this.userContactNumber = userContactNumber;
        this.hasContainer = hasContainer;
        this.canDonate = canDonate;
        this.address = address;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public String getFoodPreparedOn() {
        return foodPreparedOn;
    }

    public String getAdditionalContactNumber() {
        return additionalContactNumber;
    }

    public String getStatus() {
        return status;
    }

    public String getUserContactNumber() {
        return userContactNumber;
    }

    public boolean isHasContainer() {
        return hasContainer;
    }

    public boolean isCanDonate() {
        return canDonate;
    }

    public HashMap<String, String> getAddress() {
        return address;
    }
}
