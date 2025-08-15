package com.urban_wash.Model;

public class User {
    private String uid;
    private String name;
    private String firstName; // ADDED
    private String lastName;  // ADDED
    private String email;
    private String phone;
    private String address; // This is used for the "location" or "address" field in Firestore
    private String city;
    private String zipCode;
    private String state;
    private String country;
    private String dateJoined; // ADDED
    private String subscriptionStatus; // ADDED

    public User() {}

    // --- Getters ---
    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getFirstName() { return firstName; } // ADDED
    public String getLastName() { return lastName; }   // ADDED
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getZipCode() { return zipCode; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public String getDateJoined() { return dateJoined; } // ADDED
    public String getSubscriptionStatus() { return subscriptionStatus; } // ADDED

    // --- Setters ---
    public void setUid(String uid) { this.uid = uid; }
    public void setName(String name) { this.name = name; }
    public void setFirstName(String firstName) { this.firstName = firstName; } // ADDED
    public void setLastName(String lastName) { this.lastName = lastName; }   // ADDED
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public void setState(String state) { this.state = state; }
    public void setCountry(String country) { this.country = country; }
    public void setDateJoined(String dateJoined) { this.dateJoined = dateJoined; } // ADDED
    public void setSubscriptionStatus(String subscriptionStatus) { this.subscriptionStatus = subscriptionStatus; } // ADDED
}
