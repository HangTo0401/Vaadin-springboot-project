package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

// This will be AUTO IMPLEMENTED CRUD by Spring into a Bean called ProductRepository
@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Product, Integer> {

}