package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "supplier", schema = "demo_db")
@Data
public class Supplier extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    private String name;

    @Column(name="firstname")
    private String firstname;

    @Column(name="lastname")
    private String lastname;

    @Column(name="email")
    private String email;

    @Column(name="address")
    private String address;

    @Column(name="date_of_birth")
    private Date dateOfBirth;

    @Column(name="phone_number")
    private String phoneNumber;

    public String getName() {
        return this.firstname.concat(" ").concat(this.lastname);
    }

    @OneToMany(mappedBy="supplier", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Product> products;

    public Supplier(){}

    public Supplier(Long id, String firstname, String lastname, Date dateOfBirth, String email, String phoneNumber, String address) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.name = firstname + " " + lastname;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    @Override
    public String toString() {
        return id + " " + firstname + " " + lastname;
    }
}
