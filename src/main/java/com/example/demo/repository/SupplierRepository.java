package com.example.demo.repository;

import com.example.demo.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

// This will be AUTO IMPLEMENTED CRUD by Spring into a Bean called SupplierRepository
@Repository
@Transactional
public interface SupplierRepository extends JpaRepository <Supplier, Long> {
    List<Supplier> findByNameStartsWithIgnoreCase(String name);
}
