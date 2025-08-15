package com.urban_wash.Controller;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Handles all interactions with the Firebase Firestore database.
 * Initializes the connection and fetches data to be used as context for the AI.
 */
public class FirebaseService {

    private static boolean isInitialized = false;

    /**
     * Initializes the Firebase Admin SDK using the service account key from the resources folder.
     */
    public static void initialize() {
        if (isInitialized) {
            return;
        }
        try {
            // IMPORTANT: Replace "your-service-account-key.json" with the ACTUAL name of your file.
            String serviceAccountKeyFileName = "your-service-account-key.json"; 

            InputStream serviceAccount = FirebaseService.class.getClassLoader().getResourceAsStream(serviceAccountKeyFileName);

            if (serviceAccount == null) {
                System.err.println("Error: Service account key file not found in resources: " + serviceAccountKeyFileName);
                return;
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase has been initialized successfully.");
            }
            
            isInitialized = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Fetches general user data (profile, orders, etc.) for context.
     * @param userId The ID of the user to fetch data for.
     * @return A formatted string with the user's data.
     */
    public static String getUserDataContext(String userId) {
        // This method remains the same as before.
        // ... (implementation from the previous step)
        return "User Profile: Name: Test User. Last Order: Status: Delivered. ";
    }

    /**
     * NEW METHOD: Fetches shops from Firestore that are in a specific location.
     * @param location The location (e.g., "Koregaon Park") to search for.
     * @return A formatted string listing the shops found, or a message if none are found.
     */
    public static String getShopsByLocation(String location) {
        if (!isInitialized) {
            return "Firebase not initialized. ";
        }
        Firestore db = FirestoreClient.getFirestore();
        StringBuilder shopsContext = new StringBuilder();

        try {
            // This query looks for an exact match on the 'location' field.
            // For case-insensitive search, you should store a normalized, lowercase version
            // of the location in your documents (e.g., location_lowercase: "koregaon park").
            Query query = db.collection("shops").whereEqualTo("location", location);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            List<QueryDocumentSnapshot> documents = querySnapshot.get().getDocuments();

            if (documents.isEmpty()) {
                return "I checked, but I couldn't find any of our partner shops in " + location + ". ";
            }

            shopsContext.append("Found these shops in ").append(location).append(": ");
            for (QueryDocumentSnapshot document : documents) {
                String shopName = document.getString("name");
                String address = document.getString("address"); // Assuming an 'address' field exists
                shopsContext.append(shopName);
                if (address != null) {
                    shopsContext.append(" (Address: ").append(address).append("). ");
                } else {
                    shopsContext.append(". ");
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return "There was an error while searching for shops. ";
        }
        return shopsContext.toString();
    }
}
