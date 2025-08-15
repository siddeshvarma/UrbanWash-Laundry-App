package com.urban_wash.Model;

/**
 * Represents a single service offered by a business.
 * This is a simple Plain Old Java Object (POJO) used for storing service details.
 */
public class Service {
    private String title;
    // --- 🔴 CHANGED: Price is now a double for calculations ---
    private double price;
    private String unit;
    private String description;
    private String imageUrl;
    private String status;

    // A no-argument constructor is required for Firestore data mapping.
    public Service() {}

    // --- 🔴 CHANGED: Constructor now accepts a double for price ---
    public Service(String title, double price, String unit, String description, String imageUrl, String status) {
        this.title = title;
        this.price = price;
        this.unit = unit;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    // --- Getters ---
    public String getTitle() { return title; }
    // --- 🔴 CHANGED: Getter returns a double ---
    public double getPrice() { return price; }
    public String getUnit() { return unit; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getStatus() { return status; }

    // --- Setters ---
    public void setTitle(String title) { this.title = title; }
    // --- 🔴 CHANGED: Setter accepts a double ---
    public void setPrice(double price) { this.price = price; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setStatus(String status) { this.status = status; }
}