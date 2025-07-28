package com.example.takeatea.model;

import java.util.Date;

public class Order {
    private int id;
    private int userId;        // ID người đặt hàng (liên kết với bảng User)
    private String orderDate;  // Ngày đặt hàng (dạng chuỗi yyyy-MM-dd)
    private double totalAmount;
    private String status;     // Trạng thái: Đang xử lý, Đang giao, Hoàn tất, Hủy

    public Order() {
    }

    public Order(int id, int userId, String orderDate, double totalAmount, String status) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Order(int userId, String orderDate, double totalAmount, String status) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
