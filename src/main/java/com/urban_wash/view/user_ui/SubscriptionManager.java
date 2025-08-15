package com.urban_wash.view.user_ui;

/**
 * A Singleton class to manage the user's subscription state across the application.
 * This ensures that when a plan is selected on the SubscriptionPage, the UserProfilePage
 * can access and display the updated information in real-time.
 */
public class SubscriptionManager {

    // The single instance of the SubscriptionManager
    private static SubscriptionManager instance;

    // The name of the currently active subscription plan
    private String activePlan;

    /**
     * Private constructor to prevent instantiation from outside the class.
     */
    private SubscriptionManager() {
        // Initially, the user has no active plan.
        activePlan = null;
    }

    /**
     * Provides a global access point to the single instance of the SubscriptionManager.
     *
     * @return The singleton instance of SubscriptionManager.
     */
    public static SubscriptionManager getInstance() {
        if (instance == null) {
            instance = new SubscriptionManager();
        }
        return instance;
    }

    /**
     * Sets the active subscription plan.
     *
     * @param planName The name of the plan to set as active (e.g., "Elite Plan").
     */
    public void setActivePlan(String planName) {
        this.activePlan = planName;
    }

    /**
     * Retrieves the name of the currently active plan.
     *
     * @return The active plan name, or null if no plan is active.
     */
    public String getActivePlan() {
        return activePlan;
    }

    /**
     * Checks if the user has an active subscription.
     *
     * @return true if a plan is active, false otherwise.
     */
    public boolean hasActivePlan() {
        return activePlan != null && !activePlan.isEmpty();
    }
}
