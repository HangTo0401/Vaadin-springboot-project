package com.example.demo.dto;

import com.example.demo.entity.Supplier;

import lombok.Data;

import java.util.Date;

@Data
public class SupplierDTO {
    private Long id;
    private String name;
    private String firstname;
    private String lastname;
    private String email;
    private String address;
    private Date dateOfBirth;
    private String phoneNumber;

    public SupplierDTO(Supplier supplierEntity) {
        this.id = supplierEntity.getId();
        this.firstname = supplierEntity.getFirstname();
        this.lastname = supplierEntity.getLastname();
        this.name = supplierEntity.getFirstname() + " " + supplierEntity.getLastname();
        this.dateOfBirth = supplierEntity.getDateOfBirth();
        this.email = supplierEntity.getEmail();
        this.phoneNumber = supplierEntity.getPhoneNumber();
        this.address = supplierEntity.getAddress();
    }
}
