package com.example.comp_shop;

public class ReceiptProduct {
    private String productName;
    private String productId;
    private String productCategory;
    private int quantity;
    private double price;
    private double totalAmount;

    // Constructor
    public ReceiptProduct(String productName, String productId, String productCategory, int quantity, double price, double totalAmount) {
        this.productName = productName;
        this.productId = productId;
        this.productCategory = productCategory;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}

