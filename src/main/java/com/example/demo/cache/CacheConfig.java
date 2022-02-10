package com.example.demo.cache;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;

import lombok.Getter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PreDestroy;
import java.net.URL;

import java.util.List;

@EnableCaching //annotation enables the Spring Boot caching abstraction layer in our application.
@Configuration //annotation marks the CacheConfig class as a Spring configuration class.
public class CacheConfig {
    private final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    private static final String CRON_TAB_EVERY_MID_NIGHT = "0 0 0 * * *";

    @Getter
    public CacheManager cacheManager;

    private Cache supplierCache;

    private Cache productCache;

    private SupplierRepository supplierRepository;

    private ProductRepository productRepository;

    public CacheConfig(SupplierRepository supplierRepository, ProductRepository productRepository) {
        super();
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        init();
    }

    public CacheManager createCacheManager() {
        log.info("============Init Cache Manager==========");
        // Create CacheManager from a configuration resource in the classpath by getting config from ehcache.xml
        URL url = getClass().getResource("/ehcache.xml");
        cacheManager = CacheManager.newInstance(url);
        CacheManager.create();
        return cacheManager;
    }

    private void init() {
        createCacheManager();

        supplierCache = cacheManager.getCache(CacheName.SUPPLIER_CACHE);
        productCache = cacheManager.getCache(CacheName.PRODUCT_CACHE);

        // Add suppliers to cache
        addAllSuppliersToCache(supplierRepository.findAll());

        // Add products to cache
        addAllProductsToCache(productRepository.findAll());
    }

    /**
     * Cleans all entries in cache
     */
    @Scheduled(cron = CRON_TAB_EVERY_MID_NIGHT)
    private void refreshCache() {
        if (cacheManager != null) {
            log.warn("Cleaning all storage cache");
            cacheManager.getCache(CacheName.SUPPLIER_CACHE).removeAll();
            cacheManager.getCache(CacheName.PRODUCT_CACHE).removeAll();
            cacheManager.clearAll();
        } else {
            log.warn("Skip cleaning all storage cache");
        }
    }

    /**
     * Shutdown cacheManager
     * */
    @PreDestroy
    public void destroy() {
        cacheManager.shutdown();
    }

    /**
     * Add all suppliers to cache
     * @param supplierList
     * */
    public void addAllSuppliersToCache(List<Supplier> supplierList) {
        log.info("Add all suppliers to cache");
        try {
            for (Supplier supplier : supplierList) {
                log.info("Add supplier in cache: " + new Element(supplier.getId(), supplier));
                supplierCache.put(new Element(supplier.getId(), supplier));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add all products to cache
     * @param productList
     * */
    public void addAllProductsToCache(List<Product> productList) {
        log.info("Add all products to cache");
        try {
            for (Product product : productList) {
                log.info("Add product in cache: " + new Element(product.getId(), product));
                productCache.put(new Element(product.getId(), product));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
