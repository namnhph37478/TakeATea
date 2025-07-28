package com.example.takeatea.model;

public class Contact {
    private int id;
    private int userId;
    private String message;
    private String createdAt;

    public Contact() {
    }

    public Contact(int userId, String message, String createdAt) {
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Contact(int id, int userId, String message, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
