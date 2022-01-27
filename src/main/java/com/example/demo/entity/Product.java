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
    private Long id;

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

    public String getProductName() {
        return this.firstname.concat(" ").concat(this.lastname);
    }

    public String getSupplierName() {
        return supplier.getFirstname().concat(" ").concat(supplier.getLastname());
    }

    public Product() {}

    public Product(Long id, String firstname, String lastname, int quantity, double price) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.quantity = quantity;
        this.price = price;
    }

    @Override
    public String toString() {
        return id + " " + firstname + " " + lastname;
    }
}
