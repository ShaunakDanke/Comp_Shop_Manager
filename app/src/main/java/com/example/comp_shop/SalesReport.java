package com.example.comp_shop;

public class SalesReport {
    private String customerId;
    private String customerName;
    private String customerContact;
    private String dateOfPurchase;
    private String gst;
    private String finalAmount;

    // Constructor
    public SalesReport(String customerId, String customerName, String customerContact, String dateOfPurchase, String gst, String finalAmount) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.dateOfPurchase = dateOfPurchase;
        this.gst = gst;
        this.finalAmount = finalAmount;
    }

    // Default constructor for Firebase
    public SalesReport() {
    }

    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(String dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public String getGst() {
        return gst;
    }

    public void setGst(String gst) {
        this.gst = gst;
    }

    public String getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(String finalAmount) {
        this.finalAmount = finalAmount;
    }
}
