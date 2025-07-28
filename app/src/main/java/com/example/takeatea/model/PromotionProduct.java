package com.example.takeatea.model;

public class PromotionProduct {
    private int id;
    private int promotionId;
    private int productId;

    public PromotionProduct() {
    }

    public PromotionProduct(int promotionId, int productId) {
        this.promotionId = promotionId;
        this.productId = productId;
    }

    public PromotionProduct(int id, int promotionId, int productId) {
        this.id = id;
        this.promotionId = promotionId;
        this.productId = productId;
    }

    // Getters và Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}
