package com.example.demo.entity;

import lombok.Data;

@Data
public class Product {
    private long productId;
    private String productName;
    private int quantity;
    private double price;
    private String supplierName;

    public Product(long productId, String productName, int quantity, double price, String supplierName) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.supplierName = supplierName;
    }
}
