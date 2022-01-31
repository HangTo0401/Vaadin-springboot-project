package com.example.demo.cache;

import com.example.demo.entity.Supplier;
import com.example.demo.repository.SupplierRepository;

import lombok.Getter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EnableCaching //annotation enables the Spring Boot caching abstraction layer in our application.
@Configuration //annotation marks the CacheConfig class as a Spring configuration class.
public class CacheConfig {
    private final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Getter
    private CacheManager cacheManager;

    private Cache supplierCache;

    private SupplierRepository supplierRepository;

    private List<Supplier> supplierList = new ArrayList<>();

    public CacheConfig(SupplierRepository supplierRepository) {
        super();
        this.supplierRepository = supplierRepository;
        init();
    }

    private void init() {
        // Get config from ehcache.xml
        cacheManager = CacheManager.newInstance(getClass().getResource("/ehcache.xml"));
        CacheManager.create();

        supplierCache = cacheManager.getCache(CacheName.SUPPLIER_CACHE);

        List<Supplier> supplierList = supplierRepository.findAll();

        // Add suppliers to cache
        addAllSuppliersToCache(supplierList);
    }

    public void addAllSuppliersToCache(List<Supplier> supplierList) {
        log.info("Add all suppliers to cache");
        try {
            for (Supplier supplier : supplierList) {
                log.info("Element: " + new Element(supplier.getId(), supplier));
                supplierCache.put(new Element(supplier.getId(), supplier));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<Supplier> getAllSuppliersFromCache() {
        log.info("Get all suppliers from cache");
        try {
            Query supplierQuery = supplierCache.createQuery();

            // Get list supplier from cache
            supplierList = supplierQuery.includeValues()
                    .execute().all()
                    .stream().map(result -> (Supplier) result.getValue()).collect(Collectors.toList());
            supplierList.forEach(System.out::println);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return supplierList;
    }

    // Search by id from cache
    public Supplier getSupplierByIdFromCache(Long id) {
        log.info("Get supplier from cache");
        Element element = null;

        try {
            element = supplierCache.get(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return element != null ? (Supplier) element.getObjectValue() : null;
    }

    public String reloadCache(String action, Long id) {
        String responseMessage = "";

        try {
            // Get record from db
            Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new RuntimeException("Supplier not found"));

            if (action.equals("ADD")) {
                // Add new record to cache
                responseMessage = addNewSupplierToCache(supplier);
            } else if (action.equals("UPDATE")) {
                // Update record to cache
                responseMessage = updateSupplierInCache(supplier.getId());
            } else if (action.equals("DELETE")) {
                // Delete record to cache
                responseMessage = deleteSupplierInCache(supplier.getId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return responseMessage;
    }

    public String addNewSupplierToCache(Supplier supplier) {
        log.info("Add new supplier in cache:");
        String message = "";
        try {
            if (supplier != null) {
                log.info("New element: " + new Element(supplier.getId(), supplier));
                supplierCache.put(new Element(supplier.getId(), supplier));
            } else {
                log.info("New supplier is invalid!");
                message = "New supplier is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    public String updateSupplierInCache(Long updateId) {
        log.info("Update supplier in cache:");
        String message = "";

        try {
            if (updateId != null) {
                Supplier existSupplier = (Supplier) supplierList.stream().filter(supplier -> supplier.getId() == updateId);
                log.info("Exist element: " + new Element(existSupplier.getId(), existSupplier));
                supplierCache.put(new Element(existSupplier.getId(), existSupplier));
            } else {
                log.info("Supplier is invalid!");
                message = "Supplier is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    public String deleteSupplierInCache(Long deleteId) {
        log.info("Delete supplier in cache:");
        String message = "";

        try {
            if (deleteId != null) {
                Supplier existSupplier = (Supplier) supplierList.stream().filter(supplier -> supplier.getId() == deleteId);
                log.info("Delete element: " + new Element(existSupplier.getId(), existSupplier));
                supplierCache.remove(new Element(existSupplier.getId(), existSupplier));
            } else {
                log.info("Delete supplier is invalid!");
                message = "Delete supplier is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }
}
