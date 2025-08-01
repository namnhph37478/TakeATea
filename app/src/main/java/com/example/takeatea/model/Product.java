package com.example.takeatea.model;

public class Product {
    private int id;
    private String name;
    private double price;
    private String description;
    private String image;       // Đường dẫn ảnh
    private int categoryId;     // ID danh mục
    private int quantity;       // Số lượng tồn
    private int status;         // 1: đang bán, 0: ngừng bán
    private float rating;       // điểm đánh giá trung bình (0-5)

    public Product() {
    }

    // Constructor đầy đủ
    public Product(int id, String name, double price, String description, String image, int categoryId, int quantity, int status, float rating) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.status = status;
        this.rating = rating;
    }

    // Constructor không có ID (dùng khi thêm mới)
    public Product(String name, double price, String description, String image, int categoryId, int quantity, int status, float rating) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.categoryId = categoryId;
        this.quantity = quantity;
        this.status = status;
        this.rating = rating;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
