package com.urban_wash.Model;

import java.util.List;
import java.util.Map;

/**
 * Represents a customer order.
 * This class holds all the details of an order, including the user who placed it,
 * the business it's for, the services selected, delivery details, and payment information.
 */
public class Order {

    private String documentId; // The document ID from Firestore
    private String businessId;
    private String userId;
    private String customerName; // To display on the order page
    private String customerPhone; // ✅ ADDED: To hold the user's phone number.
    private String orderDate;
    private String status;
    private double totalAmount;
    private String deliveryMethod;
    private double deliveryFee;
    private String paymentMethod;
    private Map<String, String> deliveryAddress;
    private List<Map<String, Object>> orderedServices; // A list of the services in the order

    public Order() {}

    // --- Getters ---
    public String getDocumentId() { return documentId; }
    public String getBusinessId() { return businessId; }
    public String getUserId() { return userId; }
    public String getCustomerName() { return customerName; }
    public String getCustomerPhone() { return customerPhone; } // ✅ ADDED
    public String getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public double getTotalAmount() { return totalAmount; }
    public String getDeliveryMethod() { return deliveryMethod; }
    public double getDeliveryFee() { return deliveryFee; }
    public String getPaymentMethod() { return paymentMethod; }
    public Map<String, String> getDeliveryAddress() { return deliveryAddress; }
    public List<Map<String, Object>> getOrderedServices() { return orderedServices; }
    
    // Special getters for TableView compatibility
    public String getAmount() { return String.format("₹%.2f", totalAmount); }
    public String getOrderId() { return documentId; } // Using documentId for Order ID column

    // --- Setters ---
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public void setBusinessId(String businessId) { this.businessId = businessId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; } // ✅ ADDED
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public void setStatus(String status) { this.status = status; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setDeliveryMethod(String deliveryMethod) { this.deliveryMethod = deliveryMethod; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setDeliveryAddress(Map<String, String> deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setOrderedServices(List<Map<String, Object>> orderedServices) { this.orderedServices = orderedServices; }
}
