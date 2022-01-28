package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "supplier", schema = "demo_db")
@Data
public class Supplier extends AbstractEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    private String name;

    @Column(name="firstname")
//    @NotEmpty(message = "Firstname cannot be null")
    private String firstname;

    @Column(name="lastname")
//    @NotEmpty(message = "Lastname cannot be null")
    private String lastname;

    @Column(name="email")
//    @Email(message = "Email should be valid")
    private String email;

    @Column(name="address")
//    @NotEmpty(message = "Address cannot be null")
    private String address;

    @Column(name="date_of_birth")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    @Column(name="phone_number")
//    @NotEmpty(message = "Phone number cannot be null")
    private String phoneNumber;

    public String getName() {
        return this.firstname.concat(" ").concat(this.lastname);
    }

    //Parent class
    @OneToMany(mappedBy="supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    public void setProducts(List<Product> products) {
        // This will override the list that Hibernate is tracking.
        this.products.clear();
        this.products.addAll(products);
    }

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return Objects.equals(id, supplier.getId());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return id + " " + firstname + " " + lastname;
    }
}
