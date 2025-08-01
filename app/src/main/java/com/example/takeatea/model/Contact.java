package com.example.takeatea.model;

public class Contact {
    private int id;
    private int userId;
    private String subject;
    private String message;
    private String createdAt;

    public Contact() {}

    public Contact(int userId, String subject, String message, String createdAt) {
        this.userId = userId;
        this.subject = subject;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Getters & Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
