package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MainService {
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheManager cacheManager;

    private static Logger log = LoggerFactory.getLogger(MainService.class);

    @CacheEvict(allEntries = true)
    public void clearCache(){}

    @Cacheable(value = "supplierCache", key = "'suppliers'")
    public List<Supplier> getAllSuppliers(String stringFilter) {
        log.info("MainService: findAll suppliers list");
        if (stringFilter == null || stringFilter.isEmpty()) {
            List<Supplier> supplierList = supplierRepository.findAll();
            for (Supplier supplier : supplierList) {
                addSupplierToCache(supplier);
            }
            return supplierList;
        } else {
            return supplierRepository.search(stringFilter);
        }
    }

    @CachePut(value = "supplierCache", key = "#result.id")
    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Cacheable(value = "supplierCache", key = "#id", unless = "#result=null")
    public Supplier getSupplierById(Long id) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(id);
        if (optionalSupplier.isPresent()) {
            return optionalSupplier.get();
        }
        return null;
    }

    @CacheEvict(value = "supplierCache", key = "#id")
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    @CachePut(value = "supplierCache", key = "#supplier.id")
    public Supplier updateSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    @Cacheable(value = "productCache", key = "'products'")
    public List<Product> getAllProducts(String stringFilter) {
        log.info("MainService: findAll products list");
        if (stringFilter == null || stringFilter.isEmpty()) {
            List<Product> productList = productRepository.findAll();
            for (Product product : productList) {
                addProductToCache(product);
            }
            return productList;
        } else {
            return productRepository.search(stringFilter);
        }
    }

    @CachePut(value = "productCache", key = "#result.id")
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Cacheable(value = "productCache", key = "#productId", unless = "#result=null")
    public Product getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        return null;
    }

    @CacheEvict(value = "productCache", key = "#productId")
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    @CachePut(value = "productCache", key = "#product.id")
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public void addProductToCache(Product product) {
        Cache cache = cacheManager.getCache("productCache");
        cache.putIfAbsent(product.getProductId(), product);
    }

    public void addSupplierToCache(Supplier supplier) {
        Cache cache = cacheManager.getCache("supplierCache");
        cache.putIfAbsent(supplier.getId(), supplier);
    }
}
