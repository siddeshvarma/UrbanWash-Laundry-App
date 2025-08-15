package com.urban_wash.Model;

import java.util.ArrayList;
import java.util.List;

public class Business {
    private String documentId;
    private String shopName;
    private String address;
    private String owner;
    private String email;
    private String phone;
    private String password;
    private String status;
    private long createTime;
    private String ownerUid; // ✅ NEW FIELD
    private List<Service> services = new ArrayList<>();

    public Business() {}

    public Business(String shopName, String address, String owner, String email, String phone, String password) {
        this.shopName = shopName;
        this.address = address;
        this.owner = owner;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    // --- Getters ---
    public String getDocumentId() { return documentId; }
    public String getShopName() { return shopName; }
    public String getAddress() { return address; }
    public String getOwner() { return owner; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getStatus() { return status; }
    public long getCreateTime() { return createTime; }
    public String getOwnerUid() { return ownerUid; } // ✅ NEW GETTER
    public List<Service> getServices() { return services; }

    // --- Setters ---
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public void setAddress(String address) { this.address = address; }
    public void setOwner(String owner) { this.owner = owner; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setStatus(String status) { this.status = status; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }
    public void setOwnerUid(String ownerUid) { this.ownerUid = ownerUid; } // ✅ NEW SETTER
    public void setServices(List<Service> services) { this.services = services; }
}
