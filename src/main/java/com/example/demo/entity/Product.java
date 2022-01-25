package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="product_id")
    private long productId;

    @Column(name="firstname")
    private String firstname;

    @Column(name="lastname")
    private String lastname;

    private String productName;

    @Column(name="quantity")
    private int quantity;

    @Column(name="price")
    private double price;

    private String supplierName;

    public Product(long productId, String firstname, String lastname, int quantity, double price, String supplierName) {
        this.productId = productId;
        this.productName = firstname + " " + lastname;
        this.quantity = quantity;
        this.price = price;
        this.supplierName = supplierName;
    }
}
