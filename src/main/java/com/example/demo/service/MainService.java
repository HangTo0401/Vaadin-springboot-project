package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@CacheConfig(cacheNames = "service")
public class MainService {
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    private static Logger log = LoggerFactory.getLogger(MainService.class);

    @CacheEvict(allEntries = true)
    public void clearCache(){}

    @Cacheable(key = "'suppliers'")
    public List<Supplier> getAllSuppliers() {
        log.info("Get suppliers list: ");
        return supplierRepository.findAll();
    }

    @Cacheable(key = "'products'")
    public List<Product> getAllProducts() {
        log.info("Get products list: ");
        return productRepository.findAll();
    }
}
