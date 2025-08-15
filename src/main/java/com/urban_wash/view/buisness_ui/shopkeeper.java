package com.urban_wash.view.buisness_ui;

public class shopkeeper {
    private String fullName;
    private String shopName;
    private String shopAddress;
    private String mobileNumber;
    private String email;

    public shopkeeper(String fullName, String shopName, String shopAddress, String mobileNumber, String email) {
        this.fullName = fullName;
        this.shopName = shopName;
        this.shopAddress = shopAddress;
        this.mobileNumber = mobileNumber;
        this.email = email;
    }

    public String getFullName() { return fullName; }
    public String getShopName() { return shopName; }
    public String getShopAddress() { return shopAddress; }
    public String getMobileNumber() { return mobileNumber; }
    public String getEmail() { return email; }
}
