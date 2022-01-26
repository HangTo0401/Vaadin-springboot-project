package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name="product", schema = "demo_db")
@Data
public class Product extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="product_id")
    private Long productId;

    @Column(name="firstname")
    private String firstname;

    @Column(name="lastname")
    private String lastname;

    @Column(name="quantity")
    private int quantity;

    @Column(name="price")
    private double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable=false)
    private Supplier supplier;

    private String productName;
    private String supplierName;

    public String getProductName() {
        return this.firstname.concat(" ").concat(this.lastname);
    }

    public String getSupplierName() {
        return supplier.getFirstname().concat(" ").concat(supplier.getLastname());
    }

    public Product() {}

    public Product(Long productId, String firstname, String lastname, int quantity, double price) {
        this.productId = productId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString() {
        return productId + " " + firstname + " " + lastname;
    }
}
