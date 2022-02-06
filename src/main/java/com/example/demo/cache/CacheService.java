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
        cacheManager.clearAll();
    }

    public List<Supplier> getAllSuppliersFromCache() {
        List<Supplier> supplierList = cacheConfig.getAllSuppliersFromCache();
        return supplierList;
    }

    public Supplier getSupplierByKeyFromCache(Long id) {
        Cache supplierCache = cacheManager.getCache(CacheName.SUPPLIER_CACHE);
        Element element = supplierCache.get(id);
        return element != null ? (Supplier) element.getObjectValue() : null;
    }

    public String reloadSupplierCache(String action, Supplier supplier) {
        String message = cacheConfig.reloadSupplierCache(action, supplier);
        return message;
    }

    public List<Product> getAllProductsFromCache() {
        List<Product> productList = cacheConfig.getAllProductsFromCache();
        return productList;
    }

    public Product getProductByKeyFromCache(Long id) {
        Cache productCache = cacheManager.getCache(CacheName.PRODUCT_CACHE);
        Element element = productCache.get(id);
        return element != null ? (Product) element.getObjectValue() : null;
    }

    public String reloadProductCache(String action, Product product) {
        String message = cacheConfig.reloadProductCache(action, product);
        return message;
    }
}
