package com.example.demo.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Supplier {
    private long id;
    private String name;
    private String email;
    private String address;
    private Date dayOfBirth;
    private String phoneNumber;

    public Supplier(long id, String name, Date dayOfBirth, String email, String phoneNumber, String address) {
        this.id = id;
        this.name = name;
        this.dayOfBirth = dayOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
