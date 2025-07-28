package com.example.takeatea.model;

public class Promotion {
    private int id;
    private String code;              // Mã khuyến mãi (duy nhất)
    private int discountPercent;     // Phần trăm giảm giá (1–100)

    public Promotion() {
    }

    public Promotion(String code, int discountPercent) {
        this.code = code;
        this.discountPercent = discountPercent;
    }

    public Promotion(int id, String code, int discountPercent) {
        this.id = id;
        this.code = code;
        this.discountPercent = discountPercent;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }
}
