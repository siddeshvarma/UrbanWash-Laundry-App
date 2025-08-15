package com.urban_wash.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FirebaseAuthController {

    private static final String API_KEY = "AIzaSyAeeOZbVSV0uP9qMkgueO0xZ1Mky6xAPcQ"; // Replace with your actual API key
    private static final String SIGN_UP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;
    private static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;

    // This is your original method, left unchanged as requested.
    public String signUpAndGetUid(String email, String password) {
        try {
            String payload = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);
            String response = makeRequest(SIGN_UP_URL, payload);

            if (response.contains("localId")) {
                return extractValue(response, "localId");
            } else {
                return "Error: " + extractValue(response, "message");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * ✅ CORRECTED: This method now returns the user's UID on success.
     * This is the "new thing" added to fix the issue.
     */
    public String signUpWithEmailAndPassword(String email, String password) {
        try {
            String payload = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);
            String response = makeRequest(SIGN_UP_URL, payload);

            if (response.contains("localId")) {
                // FIX: Return the UID ("localId") from the response instead of "Success"
                return extractValue(response, "localId");
            } else {
                return "Error: " + extractValue(response, "message");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    // This is your original method, left unchanged as requested.
    public String signInWithEmailAndPassword(String email, String password) {
        try {
            String payload = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", email, password);
            String response = makeRequest(SIGN_IN_URL, payload);

            if (response.contains("localId")) {
                String uid = extractValue(response, "localId");
                
                // --- DEBUGGING STEP 1 ---
                // This message will appear in your console if the login is successful.
                // It confirms that the UID has been retrieved and is being set in the session.
                System.out.println("DEBUG: Login successful. Setting UID in SessionManager: " + uid);
                
                // Assuming SessionManager class exists and is set up correctly
                SessionManager.getInstance().setCurrentUserUid(uid);
                
                return "Success";
            } else {
                System.err.println("DEBUG: Login failed. Firebase response: " + response);
                return "Error: " + extractValue(response, "message");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // This is your original method, left unchanged as requested.
    private String makeRequest(String endpoint, String payload) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        int responseCode = conn.getResponseCode();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream(),
                StandardCharsets.UTF_8))) {
            
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }
            return responseBuilder.toString();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    // This is your original method, left unchanged as requested.
    private String extractValue(String json, String key) {
        try {
            String keyPattern = "\"" + key + "\": \"";
            int startIndex = json.indexOf(keyPattern) + keyPattern.length();
            int endIndex = json.indexOf("\"", startIndex);
            return json.substring(startIndex, endIndex);
        } catch (Exception e) {
            return "Could not parse field '" + key + "' from JSON response.";
        }
    }
}