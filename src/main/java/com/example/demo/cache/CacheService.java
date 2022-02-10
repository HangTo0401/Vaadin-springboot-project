package com.example.demo.cache;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Element;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CacheService {
    private static final String CRON_TAB_EVERY_MID_NIGHT = "0 0 0 * * *";

    private final Logger log = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private CacheConfig cacheConfig;

    private CacheManager cacheManager;

    public CacheService(CacheConfig cacheConfig) {
        this.cacheConfig = cacheConfig;
        this.cacheManager = cacheConfig.getCacheManager();
    }

    /**
     * Cleans all entries in cache
     */
    @Scheduled(cron = CRON_TAB_EVERY_MID_NIGHT)
    private void refreshCache() {
        if (this.cacheManager != null) {
            log.warn("Cleaning all storage cache");
            this.cacheManager.getCache(CacheName.SUPPLIER_CACHE).removeAll();
            this.cacheManager.getCache(CacheName.PRODUCT_CACHE).removeAll();
            this.cacheManager.clearAll();
        } else {
            log.warn("Skip cleaning all storage cache");
        }
    }

    /**
     * Get all entries in supplier cache
     * @return List<Supplier>
     */
    public List<Supplier> getAllSuppliersFromCache() {
        List<Supplier> supplierList = this.cacheConfig.getAllSuppliersFromCache();
        return supplierList;
    }

    /**
     * Get entry by key in supplier cache
     * @param id
     * @return Supplier
     */
    public Supplier getSupplierByKeyFromCache(Long id) {
        Cache supplierCache = cacheManager.getCache(CacheName.SUPPLIER_CACHE);
        Element element = supplierCache.get(id);
        return element != null ? (Supplier) element.getObjectValue() : null;
    }

    /**
     * Reload entries in supplier cache
     * @param action
     * @param supplier
     * @return String
     */
    public String reloadSupplierCache(String action, Supplier supplier) {
        String message = cacheConfig.reloadSupplierCache(action, supplier);
        return message;
    }

    /**
     * Get all entries in product cache
     */
    public List<Product> getAllProductsFromCache() {
        List<Product> productList = cacheConfig.getAllProductsFromCache();
        return productList;
    }

    /**
     * Get entry by key in product cache
     * @param id
     * @return Product
     */
    public Product getProductByKeyFromCache(Long id) {
        Cache productCache = cacheManager.getCache(CacheName.PRODUCT_CACHE);
        Element element = productCache.get(id);
        return element != null ? (Product) element.getObjectValue() : null;
    }

    /**
     * Get entry by key in product cache
     * @param action
     * @param product
     * @return String
     */
    public String reloadProductCache(String action, Product product) {
        String message = cacheConfig.reloadProductCache(action, product);
        return message;
    }
}
