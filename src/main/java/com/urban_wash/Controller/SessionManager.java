package com.urban_wash.Controller;

import com.urban_wash.Model.Business;

public class SessionManager {
    private static SessionManager instance;
    private String currentUserUid;
    private Business selectedBusiness;
    
    // --- NEW FIELD ---
    // This will store the final total calculated on the userp1 page.
    private double finalOrderTotal;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setCurrentUserUid(String uid) {
        this.currentUserUid = uid;
    }

    public String getCurrentUserUid() {
        return this.currentUserUid;
    }

    public void setSelectedBusiness(Business business) {
        this.selectedBusiness = business;
    }

    public Business getSelectedBusiness() {
        return this.selectedBusiness;
    }

    // --- NEW GETTER AND SETTER for the final total ---
    public void setFinalOrderTotal(double total) {
        this.finalOrderTotal = total;
    }

    public double getFinalOrderTotal() {
        return this.finalOrderTotal;
    }

    public void clearSession() {
        this.currentUserUid = null;
        this.selectedBusiness = null;
        this.finalOrderTotal = 0.0; // Reset total on logout
    }
}
