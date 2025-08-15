package com.urban_wash.view.common_methods;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Handles communication with the Google Gemini API, now supporting
 * text-only and multimodal (text + image) requests.
 */
public class GeminiApiClient {

    private static final String API_KEY = "AIzaSyD-CkrV_lf0lRndAgtAl9N5JIeguxVf3-k";
    // Using a vision-capable model is crucial for image uploads.
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + API_KEY;

    /**
     * Sends a prompt (with optional context and image) to the Gemini API.
     * @param textPrompt The user's text message.
     * @param firebaseContext The data fetched from Firebase to provide context.
     * @param base64ImageData The Base64-encoded string of the uploaded image (can be null).
     * @return The text response from the Gemini model.
     */
    public static String generateContent(String textPrompt, String firebaseContext, String base64ImageData) {
        HttpClient client = HttpClient.newHttpClient();

        // --- Construct the prompt with context ---
        String fullPrompt = "You are 'Washie', a helpful AI assistant for the UrbanWash laundry app. " +
                            "Use the following data to answer user questions: [" + firebaseContext + "]. " +
                            "User's message: " + textPrompt;

        // --- Build the JSON payload ---
        JSONArray parts = new JSONArray();
        parts.put(new JSONObject().put("text", fullPrompt)); // Always include the text part

        // If an image is provided, add it to the payload
        if (base64ImageData != null && !base64ImageData.isEmpty()) {
            JSONObject inlineData = new JSONObject()
                .put("mime_type", "image/jpeg") // Assuming JPEG, can be dynamic
                .put("data", base64ImageData);
            parts.put(new JSONObject().put("inline_data", inlineData));
        }

        JSONObject payload = new JSONObject()
            .put("contents", new JSONObject[]{
                new JSONObject().put("parts", parts)
            });

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(API_URL))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(payload.toString()))
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject responseBody = new JSONObject(response.body());
                // Handle potential API safety blocks or empty responses
                if (responseBody.has("candidates")) {
                     return responseBody.getJSONArray("candidates")
                                   .getJSONObject(0)
                                   .getJSONObject("content")
                                   .getJSONArray("parts")
                                   .getJSONObject(0)
                                   .getString("text");
                } else {
                    return "I'm sorry, I couldn't process that request. It might have been blocked for safety reasons.";
                }
            } else {
                System.err.println("API Error Response: " + response.body());
                return "Sorry, there was an API error. (Status: " + response.statusCode() + ")";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An unexpected error occurred.";
        }
    }
}
