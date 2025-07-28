package com.example.takeatea.model;

public class Review {
    private int id;
    private int userId;      // Liên kết với bảng User
    private int productId;   // Liên kết với bảng Product
    private int rating;      // Số sao (1 đến 5)
    private String comment;  // Nội dung đánh giá

    public Review() {
    }

    public Review(int userId, int productId, int rating, String comment) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }

    public Review(int id, int userId, int productId, int rating, String comment) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
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

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
