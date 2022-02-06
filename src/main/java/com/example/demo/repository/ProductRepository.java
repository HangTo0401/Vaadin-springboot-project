package com.example.demo.repository;

import com.example.demo.entity.Product;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

import java.util.List;

// This will be AUTO IMPLEMENTED CRUD by Spring into a Bean called ProductRepository
@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p " +
            "where lower(p.firstname) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(p.lastname) like lower(concat('%', :searchTerm, '%'))")
    List<Product> search(@Param("searchTerm") String searchTerm);
}