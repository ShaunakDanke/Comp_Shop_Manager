package com.example.comp_shop;

public class InvoiceTemp {
    private String productName;
    private String productCatageory;
    private int quantity;
    private double price;
    private double amount;

    // Constructor
    public InvoiceTemp(String productName,String productCatageory, int quantity, double price, double amount) {
        this.productName = productName;
        this.productCatageory=productCatageory;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
    }

    // Getters and Setters
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getproductCatageory() {return productCatageory; }

    public void setProductCatageory(){this.productCatageory=productCatageory;}

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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
