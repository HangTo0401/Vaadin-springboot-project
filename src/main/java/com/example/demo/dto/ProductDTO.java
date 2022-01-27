package com.example.demo.dto;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Data
public class ProductDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private int quantity;
    private double price;
    private String productName;
    private String supplierName;

    public ProductDTO(Product productEntity) {
        this.id = productEntity.getId();
        this.firstname = productEntity.getFirstname();
        this.lastname = productEntity.getLastname();
        this.quantity = productEntity.getQuantity();
        this.price = productEntity.getPrice();
    }
}
