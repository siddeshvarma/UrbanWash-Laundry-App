package com.urban_wash.Controller;

import com.urban_wash.Model.Business;
import com.urban_wash.Model.Order;
import com.urban_wash.Model.Service;
import com.urban_wash.Model.User;
import com.urban_wash.view.admin_ui.RegisteredUsersPage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FirestoreService {

    private static final String PROJECT_ID = "urbanwash-90d04";
    private static final String API_KEY = "AIzaSyAeeOZbVSV0uP9qMkgueO0xZ1Mky6xAPcQ";

    // --- Business Methods ---
    public String registerBusiness(Business business) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/Business?key=%s",
                PROJECT_ID, API_KEY
            );
            long timestamp = System.currentTimeMillis();
            String payload = String.format(
                "{\"fields\": {" +
                "\"shopName\": {\"stringValue\": \"%s\"}, " +
                "\"address\": {\"stringValue\": \"%s\"}, " +
                "\"owner\": {\"stringValue\": \"%s\"}, " +
                "\"email\": {\"stringValue\": \"%s\"}, " +
                "\"phone\": {\"stringValue\": \"%s\"}, " +
                "\"password\": {\"stringValue\": \"%s\"}, " +
                "\"status\": {\"stringValue\": \"Pending Review\"}, " +
                "\"createTime\": {\"integerValue\": \"%d\"}" +
                "}}",
                business.getShopName(), business.getAddress(), business.getOwner(), business.getEmail(), business.getPhone(),
                business.getPassword(),
                timestamp
            );
            return performPostRequest(endpoint, payload);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String updateBusinessServices(String documentId, List<Service> services) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/Business/%s?key=%s&updateMask.fieldPaths=services",
                PROJECT_ID, documentId, API_KEY
            );
            String payload = serializeServicesToJson(services);
            return performPatchRequest(endpoint, payload);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    
    // --- 🔴 ALL OTHER METHODS ARE UNCHANGED. ONLY THE SERVICE PARSING/SERIALIZATION IS MODIFIED. ---
    
    private List<Service> parseServices(String docJson) {
        List<Service> services = new ArrayList<>();
        // This pattern finds the 'services' array in the Firestore JSON response.
        Pattern pattern = Pattern.compile("\"services\":\\s*\\{\\s*\"arrayValue\":\\s*\\{\\s*\"values\":\\s*\\[([^\\]]*)\\]");
        Matcher matcher = pattern.matcher(docJson);

        if (matcher.find()) {
            String servicesArrayContent = matcher.group(1);
            // This pattern finds each individual service map within the array.
            Pattern servicePattern = Pattern.compile("\\{\\s*\"mapValue\":\\s*\\{([^\\]]*?)\\}\\}");
            Matcher serviceMatcher = servicePattern.matcher(servicesArrayContent);

            while (serviceMatcher.find()) {
                String serviceMapContent = "{" + serviceMatcher.group(1) + "}";
                Service service = new Service();
                service.setTitle(getValue(serviceMapContent, "title", "stringValue"));
                
                // --- 🔴 CHANGED: Price is now parsed as a number (double). ---
                service.setPrice(getDoubleValue(serviceMapContent, "price"));
                
                service.setUnit(getValue(serviceMapContent, "unit", "stringValue"));
                service.setDescription(getValue(serviceMapContent, "description", "stringValue"));
                service.setImageUrl(getValue(serviceMapContent, "imageUrl", "stringValue"));
                service.setStatus(getValue(serviceMapContent, "status", "stringValue"));
                services.add(service);
            }
        }
        return services;
    }

    private String serializeServicesToJson(List<Service> services) {
        StringBuilder valuesBuilder = new StringBuilder();
        for (Service service : services) {
            if (valuesBuilder.length() > 0) {
                valuesBuilder.append(",");
            }
            // --- 🔴 CHANGED: Price is now saved as a 'doubleValue' to Firestore. ---
            valuesBuilder.append(String.format(
                "{\"mapValue\": {\"fields\": {" +
                "\"title\": {\"stringValue\": \"%s\"}," +
                "\"price\": {\"doubleValue\": %f}," + // Use doubleValue for numbers
                "\"unit\": {\"stringValue\": \"%s\"}," +
                "\"description\": {\"stringValue\": \"%s\"}," +
                "\"imageUrl\": {\"stringValue\": \"%s\"}," +
                "\"status\": {\"stringValue\": \"%s\"}" +
                "}}}",
                escapeJson(service.getTitle()),
                service.getPrice(), // Pass the double value directly
                escapeJson(service.getUnit()),
                escapeJson(service.getDescription()),
                escapeJson(service.getImageUrl()),
                escapeJson(service.getStatus())
            ));
        }
        return String.format(
            "{\"fields\": {\"services\": {\"arrayValue\": {\"values\": [%s]}}}}",
            valuesBuilder.toString()
        );
    }

    private double getDoubleValue(String json, String key) {
        try {
            // This pattern handles both "doubleValue": 12.0 and "integerValue": "12"
            String keyPattern = "\"" + key + "\":\\s*\\{\\s*\"(doubleValue|integerValue)\":\\s*\"?([^,\"}]+)\"?";
            Pattern p = Pattern.compile(keyPattern);
            Matcher m = p.matcher(json);
            if (m.find()) {
                return Double.parseDouble(m.group(2));
            }
            
            // --- 🔴 ADDED FALLBACK: For old data where price was a string like "$12" ---
            String stringValue = getValue(json, key, "stringValue");
            if (stringValue != null && !stringValue.isEmpty()) {
                // Remove all non-digit and non-decimal-point characters before parsing.
                return Double.parseDouble(stringValue.replaceAll("[^\\d.]", ""));
            }

            return 0.0;
        } catch (Exception e) {
            System.err.println("Could not parse double for key '" + key + "'. Returning 0.0. Error: " + e.getMessage());
            return 0.0;
        }
    }

    // --- UNCHANGED METHODS BELOW ---

    public String approveBusinessRegistration(String documentId, String email, String password) {
        try {
            String lookupEndpoint = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=" + API_KEY;
            String lookupPayload = String.format("{\"email\":[\"%s\"]}", email);
            String lookupResponse = performPostRequest(lookupEndpoint, lookupPayload);

            if (!lookupResponse.contains("\"users\"")) {
                System.out.println("User does not exist. Creating new user in Authentication...");
                String signUpEndpoint = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + API_KEY;
                String signUpPayload = String.format(
                        "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
                        email, password
                );
                String signUpResponse = performPostRequest(signUpEndpoint, signUpPayload);
                if (signUpResponse.startsWith("Error:")) {
                    return "Authentication Failed: " + signUpResponse;
                }
            } else {
                System.out.println("User already exists in Authentication. Skipping creation.");
            }

            System.out.println("Updating business status to Approved in Firestore...");
            String firestoreUpdateEndpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/Business/%s?key=%s&updateMask.fieldPaths=status",
                PROJECT_ID, documentId, API_KEY
            );
            String firestorePayload = "{\"fields\": {\"status\": {\"stringValue\": \"Approved\"}}}";
            String firestoreResponse = performPatchRequest(firestoreUpdateEndpoint, firestorePayload);

            if (firestoreResponse.startsWith("Error:")) {
                return "Firestore Update Failed: " + firestoreResponse;
            }
            return "Success! Business Approved.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public Business fetchBusinessByOwnerUid(String ownerUid) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents:runQuery?key=%s",
                PROJECT_ID, API_KEY
            );
            String queryPayload = String.format(
                "{\"structuredQuery\": {" +
                "\"from\": [{\"collectionId\": \"Business\"}]," +
                "\"where\": {\"fieldFilter\": {" +
                "\"field\": {\"fieldPath\": \"ownerUid\"}," +
                "\"op\": \"EQUAL\"," +
                "\"value\": {\"stringValue\": \"%s\"}" +
                "}}," +
                "\"limit\": 1" +
                "}}",
                ownerUid
            );

            String response = performPostRequest(endpoint, queryPayload);
            if (response.startsWith("Error:")) {
                System.err.println("Error fetching business by owner UID: " + response);
                return null;
            }

            List<Business> businesses = parseMultipleBusinesses(response);
            if (!businesses.isEmpty()) {
                return businesses.get(0);
            } else {
                System.err.println("No business found for owner UID: " + ownerUid);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Business fetchLatestBusiness() {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents:runQuery?key=%s",
                PROJECT_ID, API_KEY
            );
            String queryPayload = "{\"structuredQuery\": {" +
                                  "\"from\": [{\"collectionId\": \"Business\"}]," +
                                  "\"orderBy\": [{\"field\": {\"fieldPath\": \"createTime\"}, \"direction\": \"DESCENDING\"}]," +
                                  "\"limit\": 1" +
                                  "}}";
            String response = performPostRequest(endpoint, queryPayload);
            if (response.startsWith("Error:")) {
                System.err.println("Error fetching latest business: " + response);
                return null;
            }
            List<Business> businesses = parseMultipleBusinesses(response);
            return !businesses.isEmpty() ? businesses.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Business> fetchApprovedBusinesses() {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents:runQuery?key=%s",
                PROJECT_ID, API_KEY
            );
            String queryPayload = "{\"structuredQuery\": {" +
                                  "\"from\": [{\"collectionId\": \"Business\"}]," +
                                  "\"where\": {\"fieldFilter\": {" +
                                  "\"field\": {\"fieldPath\": \"status\"}," +
                                  "\"op\": \"EQUAL\"," +
                                  "\"value\": {\"stringValue\": \"Approved\"}" +
                                  "}}," +
                                  "\"orderBy\": [{\"field\": {\"fieldPath\": \"shopName\"}, \"direction\": \"ASCENDING\"}]" +
                                  "}}";
            String response = performPostRequest(endpoint, queryPayload);
            if (response.startsWith("Error:")) {
                System.err.println("Error fetching approved businesses: " + response);
                return new ArrayList<>();
            }
            return parseMultipleBusinesses(response);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Business fetchBusinessById(String businessId) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/Business/%s?key=%s",
                PROJECT_ID, businessId, API_KEY
            );
            String response = performGetRequest(endpoint);
            if (response.startsWith("Error:")) {
                System.err.println("Error fetching business by ID: " + response);
                return null;
            }
            return parseSingleBusiness(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // ... other unchanged methods ...
    public String createUserProfile(User user) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/users?documentId=%s&key=%s",
                PROJECT_ID, user.getUid(), API_KEY
            );
            String payload = String.format(
                "{\"fields\": {" +
                "\"name\": {\"stringValue\": \"%s\"}, " +
                "\"firstName\": {\"stringValue\": \"%s\"}, " +
                "\"lastName\": {\"stringValue\": \"%s\"}, " +
                "\"email\": {\"stringValue\": \"%s\"}, " +
                "\"phone\": {\"stringValue\": \"%s\"}, " +
                "\"location\": {\"stringValue\": \"%s\"}, " +
                "\"city\": {\"stringValue\": \"%s\"}, " +
                "\"state\": {\"stringValue\": \"%s\"}, " +
                "\"country\": {\"stringValue\": \"%s\"}, " +
                "\"zipCode\": {\"stringValue\": \"%s\"}, " +
                "\"dateJoined\": {\"stringValue\": \"%s\"}, " +
                "\"subscriptionStatus\": {\"stringValue\": \"%s\"}" +
                "}}",
                user.getName(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(),
                user.getAddress(), user.getCity(), user.getState(), user.getCountry(), user.getZipCode(),
                user.getDateJoined(), user.getSubscriptionStatus()
            );
            return performPostRequest(endpoint, payload);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public User getUserProfile(String uid) {
        try {
            String endpoint = String.format(
                    "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/users/%s?key=%s",
                    PROJECT_ID, uid, API_KEY
            );
            String response = performGetRequest(endpoint);
            if (response.startsWith("Error:")) return null;

            User user = new User();
            user.setUid(uid);
            user.setName(getValue(response, "name", "stringValue"));
            user.setFirstName(getValue(response, "firstName", "stringValue"));
            user.setLastName(getValue(response, "lastName", "stringValue"));
            user.setEmail(getValue(response, "email", "stringValue"));
            user.setPhone(getValue(response, "phone", "stringValue"));

            String addressValue = getValue(response, "location", "stringValue");
            if (addressValue == null || addressValue.isEmpty()) {
                addressValue = getValue(response, "address", "stringValue");
            }
            user.setAddress(addressValue);

            user.setCity(getValue(response, "city", "stringValue"));
            user.setZipCode(getValue(response, "zipCode", "stringValue"));
            user.setState(getValue(response, "state", "stringValue"));
            user.setCountry(getValue(response, "country", "stringValue"));
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String updateUserProfile(User user) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/users/%s?key=%s" +
                "&updateMask.fieldPaths=name&updateMask.fieldPaths=phone&updateMask.fieldPaths=location" +
                "&updateMask.fieldPaths=city&updateMask.fieldPaths=zipCode&updateMask.fieldPaths=state&updateMask.fieldPaths=country",
                PROJECT_ID, user.getUid(), API_KEY
            );
            String payload = String.format(
                "{\"fields\": {" +
                "\"name\": {\"stringValue\": \"%s\"}, " +
                "\"phone\": {\"stringValue\": \"%s\"}, " +
                "\"location\": {\"stringValue\": \"%s\"}, " +
                "\"city\": {\"stringValue\": \"%s\"}, " +
                "\"zipCode\": {\"stringValue\": \"%s\"}, " +
                "\"state\": {\"stringValue\": \"%s\"}, " +
                "\"country\": {\"stringValue\": \"%s\"}" +
                "}}",
                user.getName(), user.getPhone(), user.getAddress(), user.getCity(),
                user.getZipCode(), user.getState(), user.getCountry()
            );
            return performPatchRequest(endpoint, payload);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public String placeOrder(Order order) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/Business/%s/orders?key=%s",
                PROJECT_ID, order.getBusinessId(), API_KEY
            );
            String payload = serializeOrderToJson(order);
            return performPostRequest(endpoint, payload);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error placing order: " + e.getMessage();
        }
    }

    public List<Order> fetchOrdersForBusiness(String businessId) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/Business/%s/orders?key=%s",
                PROJECT_ID, businessId, API_KEY
            );
            String response = performGetRequest(endpoint);
            if (response.startsWith("Error:")) {
                System.err.println("Error fetching orders: " + response);
                return new ArrayList<>();
            }
            
            List<Order> orders = parseMultipleOrdersFromListResponse(response, businessId);
    
            for (Order order : orders) {
                String userId = order.getUserId();
                if (userId != null && !userId.isEmpty()) {
                    User user = getUserProfile(userId);
                    if (user != null) {
                        if (order.getCustomerPhone() == null || order.getCustomerPhone().isEmpty()) {
                             order.setCustomerPhone(user.getPhone());
                        }
    
                        if (order.getDeliveryAddress() == null || order.getDeliveryAddress().isEmpty()) {
                            Map<String, String> userAddress = new HashMap<>();
                            userAddress.put("address1", user.getAddress());
                            userAddress.put("city", user.getCity());
                            userAddress.put("state", user.getState());
                            userAddress.put("pincode", user.getZipCode());
                            userAddress.put("country", user.getCountry());
                            order.setDeliveryAddress(userAddress);
                        }
                    }
                }
            }
            
            return orders;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public Order fetchLatestOrderByUserId(String userId) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents:runQuery?key=%s",
                PROJECT_ID, API_KEY
            );

            String queryPayload = String.format(
                "{\"structuredQuery\": {" +
                "\"from\": [{\"collectionId\": \"orders\", \"allDescendants\": true}]," +
                "\"where\": {\"fieldFilter\": {" +
                "\"field\": {\"fieldPath\": \"userId\"}," +
                "\"op\": \"EQUAL\"," +
                "\"value\": {\"stringValue\": \"%s\"}" +
                "}}," +
                "\"orderBy\": [{\"field\": {\"fieldPath\": \"orderDate\"}, \"direction\": \"DESCENDING\"}]," +
                "\"limit\": 1" +
                "}}",
                userId
            );

            String response = performPostRequest(endpoint, queryPayload);
            if (response.startsWith("Error:")) {
                System.err.println("Error fetching latest order for user: " + response);
                return null;
            }

            List<Order> orders = parseOrdersFromQueryResponse(response);
            return !orders.isEmpty() ? orders.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String updateOrder(Order order) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/Business/%s/orders/%s?key=%s",
                PROJECT_ID, order.getBusinessId(), order.getDocumentId(), API_KEY
            );
            String payload = serializeOrderToJson(order);
            return performPatchRequest(endpoint, payload);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error updating order: " + e.getMessage();
        }
    }

    public String deleteOrder(String businessId, String orderId) {
        try {
            String endpoint = String.format(
                "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/Business/%s/orders/%s?key=%s",
                PROJECT_ID, businessId, orderId, API_KEY
            );
            return performDeleteRequest(endpoint);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error deleting order: " + e.getMessage();
        }
    }

    private List<Business> parseMultipleBusinesses(String jsonResponse) {
        List<Business> businesses = new ArrayList<>();
        if (jsonResponse == null || !jsonResponse.contains("\"document\"")) {
            return businesses;
        }
        String[] docParts = jsonResponse.split("\\{\"document\":");
        for (int i = 1; i < docParts.length; i++) {
            String docJson = "{\"document\":" + docParts[i];
            try {
                businesses.add(parseSingleBusiness(docJson));
            } catch (Exception e) {
                System.err.println("Failed to parse a business document from multiple: " + e.getMessage());
            }
        }
        return businesses;
    }

    private Business parseSingleBusiness(String jsonResponse) {
        if (jsonResponse == null || !jsonResponse.contains("\"name\"")) {
            return null;
        }
        try {
            Business business = new Business();
            business.setDocumentId(extractDocumentId(jsonResponse, "Business"));
            business.setShopName(getValue(jsonResponse, "shopName", "stringValue"));
            business.setAddress(getValue(jsonResponse, "address", "stringValue"));
            business.setOwner(getValue(jsonResponse, "owner", "stringValue"));
            business.setPhone(getValue(jsonResponse, "phone", "stringValue"));
            business.setEmail(getValue(jsonResponse, "email", "stringValue"));
            business.setOwnerUid(getValue(jsonResponse, "ownerUid", "stringValue"));
            business.setServices(parseServices(jsonResponse));
            return business;
        } catch (Exception e) {
            System.err.println("Failed to parse single business document: " + e.getMessage());
            return null;
        }
    }
    
    private List<Order> parseMultipleOrdersFromListResponse(String jsonResponse, String businessId) {
        List<Order> orders = new ArrayList<>();
        if (jsonResponse == null || !jsonResponse.contains("\"documents\"")) {
            return orders;
        }

        try {
            int startIndex = jsonResponse.indexOf("\"documents\": [");
            if (startIndex == -1) return orders;
            startIndex += "\"documents\": [".length();
            
            int endIndex = jsonResponse.lastIndexOf("]");
            if (startIndex >= endIndex) return orders; 
            
            String documentsContent = jsonResponse.substring(startIndex, endIndex);

            String[] docJsonParts = documentsContent.split("\\},\\s*\\{");

            for (String part : docJsonParts) {
                String docJson = part;
                if (!docJson.startsWith("{")) {
                    docJson = "{" + docJson;
                }
                if (!docJson.endsWith("}")) {
                    docJson = docJson + "}";
                }
                
                try {
                    orders.add(parseSingleOrder(docJson, businessId));
                } catch (Exception e) {
                    System.err.println("Failed to parse an individual order document from list: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing multiple orders from list response: " + e.getMessage());
        }
        
        return orders;
    }

    private List<Order> parseOrdersFromQueryResponse(String jsonResponse) {
        List<Order> orders = new ArrayList<>();
        if (jsonResponse == null || !jsonResponse.contains("\"document\"")) {
            return orders;
        }
        String[] docParts = jsonResponse.split("\\{\"document\":");
        for (int i = 1; i < docParts.length; i++) {
            String docJson = "{\"document\":" + docParts[i];
            try {
                String businessId = extractBusinessIdFromOrderPath(docJson);
                orders.add(parseSingleOrder(docJson, businessId));
            } catch (Exception e) {
                System.err.println("Failed to parse an order document from query response: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return orders;
    }
    
    private Order parseSingleOrder(String docJson, String businessId) {
        Order order = new Order();
        order.setBusinessId(businessId);
        order.setDocumentId(extractDocumentId(docJson, "orders"));
        order.setCustomerName(getValue(docJson, "customerName", "stringValue"));
        order.setStatus(getValue(docJson, "status", "stringValue"));
        order.setTotalAmount(getDoubleValue(docJson, "totalAmount"));
        order.setDeliveryMethod(getValue(docJson, "deliveryMethod", "stringValue"));
        order.setPaymentMethod(getValue(docJson, "paymentMethod", "stringValue"));
        order.setUserId(getValue(docJson, "userId", "stringValue"));
        order.setCustomerPhone(getValue(docJson, "phone", "stringValue"));
        order.setDeliveryAddress(parseMapValue(docJson, "deliveryAddress"));

        String dateValue = getValue(docJson, "orderDate", "stringValue");
        if (dateValue != null && !dateValue.isEmpty()) {
            order.setOrderDate(dateValue);
        } else {
            String timestampStr = getValue(docJson, "orderDate", "timestampValue");
            if (timestampStr != null && !timestampStr.isEmpty()) {
                 try {
                      Instant instant = Instant.parse(timestampStr);
                      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
                      order.setOrderDate(formatter.format(instant));
                 } catch (Exception e) {
                      order.setOrderDate(timestampStr);
                 }
            } else {
                 order.setOrderDate("N/A");
            }
        }
        return order;
    }
    
    private String serializeOrderToJson(Order order) {
        StringBuilder fieldsBuilder = new StringBuilder();

        appendStringField(fieldsBuilder, "businessId", order.getBusinessId());
        appendStringField(fieldsBuilder, "status", order.getStatus());
        appendDoubleField(fieldsBuilder, "totalAmount", order.getTotalAmount());
        appendStringField(fieldsBuilder, "userId", order.getUserId());
        appendStringField(fieldsBuilder, "customerName", order.getCustomerName());
        appendStringField(fieldsBuilder, "customerPhone", order.getCustomerPhone());
        appendStringField(fieldsBuilder, "deliveryMethod", order.getDeliveryMethod());
        appendDoubleField(fieldsBuilder, "deliveryFee", order.getDeliveryFee());
        appendStringField(fieldsBuilder, "paymentMethod", order.getPaymentMethod());

        if (order.getOrderDate() != null) {
            if (order.getOrderDate().contains("T") && order.getOrderDate().endsWith("Z")) {
                appendTimestampField(fieldsBuilder, "orderDate", order.getOrderDate());
            } else {
                appendStringField(fieldsBuilder, "orderDate", order.getOrderDate());
            }
        }
        
        if (order.getDeliveryAddress() != null && !order.getDeliveryAddress().isEmpty()) {
            String addressJsonMap = order.getDeliveryAddress().entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .map(entry -> String.format("\"%s\": {\"stringValue\": \"%s\"}", 
                                         escapeJson(entry.getKey()), escapeJson(entry.getValue())))
                .collect(Collectors.joining(","));
            fieldsBuilder.append(String.format(",\"deliveryAddress\": {\"mapValue\": {\"fields\": {%s}}}", addressJsonMap));
        }

        if (order.getOrderedServices() != null && !order.getOrderedServices().isEmpty()) {
            String servicesJsonArray = order.getOrderedServices().stream()
                .map(serviceMap -> {
                    String title = (String) serviceMap.getOrDefault("title", "");
                    int quantity = (int) serviceMap.getOrDefault("quantity", 0);
                    double price = (double) serviceMap.getOrDefault("price", 0.0);
                    return String.format(
                        "{\"mapValue\": {\"fields\": {" +
                        "\"title\": {\"stringValue\": \"%s\"}," +
                        "\"quantity\": {\"integerValue\": \"%d\"}," +
                        "\"price\": {\"doubleValue\": %f}" +
                        "}}}",
                        escapeJson(title), quantity, price
                    );
                })
                .collect(Collectors.joining(","));
            fieldsBuilder.append(String.format(",\"orderedServices\": {\"arrayValue\": {\"values\": [%s]}}", servicesJsonArray));
        }

        String fieldsJson = fieldsBuilder.length() > 0 ? fieldsBuilder.substring(1) : "";
        return String.format("{\"fields\": {%s}}", fieldsJson);
    }
    
    private void appendStringField(StringBuilder builder, String key, String value) {
        if (value != null && !value.isEmpty()) {
            builder.append(String.format(",\"%s\": {\"stringValue\": \"%s\"}", key, escapeJson(value)));
        }
    }

    private void appendDoubleField(StringBuilder builder, String key, double value) {
        if (value != 0.0) {
             builder.append(String.format(",\"%s\": {\"doubleValue\": %f}", key, value));
        }
    }

    private void appendTimestampField(StringBuilder builder, String key, String value) {
        if (value != null && !value.isEmpty()) {
            builder.append(String.format(",\"%s\": {\"timestampValue\": \"%s\"}", key, value));
        }
    }
    
    private String extractDocumentId(String docJson, String collectionName) {
        Pattern p = Pattern.compile(String.format("/%s/([^/]+?)\"", collectionName));
        Matcher m = p.matcher(docJson);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private String extractBusinessIdFromOrderPath(String docJson) {
        Pattern p = Pattern.compile("/Business/([^/]+)/orders/");
        Matcher m = p.matcher(docJson);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }


    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String getValue(String json, String key, String type) {
        try {
            String keyPattern = "\"" + key + "\":\\s*\\{\\s*\"" + type + "\":\\s*\"";
            Pattern p = Pattern.compile(keyPattern + "([^\"]*)\"");
            Matcher m = p.matcher(json);
            return m.find() ? m.group(1) : "";
        } catch (Exception e) {
            return "";
        }
    }
    
    private Map<String, String> parseMapValue(String json, String mapKey) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            String mapPatternStr = "\"" + mapKey + "\":\\s*\\{\\s*\"mapValue\":\\s*\\{\\s*\"fields\":\\s*\\{([^}]+)\\}";
            Pattern mapPattern = Pattern.compile(mapPatternStr);
            Matcher mapMatcher = mapPattern.matcher(json);

            if (mapMatcher.find()) {
                String fieldsJson = mapMatcher.group(1);
                Pattern fieldPattern = Pattern.compile("\"([^\"]+)\":\\s*\\{\\s*\"stringValue\":\\s*\"([^\"]*)\"");
                Matcher fieldMatcher = fieldPattern.matcher(fieldsJson);
                while (fieldMatcher.find()) {
                    resultMap.put(fieldMatcher.group(1), fieldMatcher.group(2));
                }
            }
        } catch (Exception e) {
            System.err.println("Could not parse map for key '" + mapKey + "': " + e.getMessage());
        }
        return resultMap;
    }

    private String performGetRequest(String endpointUrl) throws Exception {
        URL url = new URL(endpointUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        return readResponse(conn);
    }
    
    private String performPostRequest(String endpointUrl, String payload) throws Exception {
        URL url = new URL(endpointUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }
        return readResponse(conn);
    }

    private String performPatchRequest(String endpointUrl, String payload) throws Exception {
        URL url = new URL(endpointUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST"); 
        conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }
        return readResponse(conn);
    }
    
    private String performDeleteRequest(String endpointUrl) throws Exception {
        URL url = new URL(endpointUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        return readResponse(conn);
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        int responseCode = conn.getResponseCode();
        
        if (responseCode == 200 && conn.getRequestMethod().equals("DELETE")) {
            conn.disconnect();
            return "Success";
        }
        
        InputStream inputStream = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        conn.disconnect();
        if (responseCode >= 200 && responseCode < 300) {
            return response.toString();
        } else {
            System.err.println("Firestore API Error (" + responseCode + "): " + response.toString());
            return "Error: " + response.toString();
        }
    }
    
    public List<RegisteredUsersPage.User> fetchUsers() throws Exception {
        String endpoint = String.format(
            "https://firestore.googleapis.com/v1/projects/%s/databases/(default)/documents/users?key=%s",
            PROJECT_ID, API_KEY
        );
        String jsonResponse = performGetRequest(endpoint);
        if (jsonResponse.startsWith("Error:")) {
            throw new Exception(jsonResponse);
        }
        return parseMultipleUsersForAdmin(jsonResponse);
    }

    private List<RegisteredUsersPage.User> parseMultipleUsersForAdmin(String jsonResponse) {
        List<RegisteredUsersPage.User> users = new ArrayList<>();
        if (jsonResponse == null || !jsonResponse.contains("\"documents\"")) return users;
        String[] docParts = jsonResponse.split("\\{\"name\":");
        for (int i = 1; i < docParts.length; i++) {
            String docJson = "{\"name\":" + docParts[i];
            String name = getValue(docJson, "name", "stringValue");
            String email = getValue(docJson, "email", "stringValue");
            String location = getValue(docJson, "location", "stringValue");
            String dateJoined = getValue(docJson, "dateJoined", "stringValue");
            String status = getValue(docJson, "subscriptionStatus", "stringValue");
            users.add(new RegisteredUsersPage.User(name, email, location, dateJoined, status, "/user_avatar.png"));
        }
        return users;
    }
}