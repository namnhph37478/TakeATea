package com.example.takeatea.model;

public class Contact {
    private int id;
    private int userId;
    private String message;
    private String createdAt;
    private boolean isFromUser; // ✅ true = user gửi, false = admin hoặc hệ thống

    public Contact() {}

    public Contact(int userId, String message, String createdAt, boolean isFromUser) {
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
        this.isFromUser = isFromUser;
    }

    // Getters & Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public boolean isFromUser() { return isFromUser; }
    public void setFromUser(boolean fromUser) { isFromUser = fromUser; }
}
