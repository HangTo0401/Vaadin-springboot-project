package com.example.demo.cache;

import com.example.demo.entity.Supplier;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import net.sf.ehcache.Element;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
     * Cleans the entire cache
     */
    @Scheduled(cron = CRON_TAB_EVERY_MID_NIGHT)
    private void refreshCache() {
//        cacheManager.getCacheNames().stream()
//                    .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
        cacheManager.clearAll();
    }
//
//    public void addProductToCache(Product product) {
//        Cache cache = cacheManager.getCache("productCache");
//        cache.putIfAbsent(product.getId(), product);
//    }
//

    public List<Supplier> getAllSuppliersFromCache() {
        List<Supplier> supplierList = cacheConfig.getAllSuppliersFromCache();
        return supplierList;
    }

    public Supplier getSupplierByKeyFromCache(Long id) {
        Cache supplierCache = cacheManager.getCache(CacheName.SUPPLIER_CACHE);
        Element element = supplierCache.get(id);
        return element != null ? (Supplier) element.getObjectValue() : null;
    }

    public String reloadSupplierCache(String action, Long id) {
        String message = cacheConfig.reloadSupplierCache(action, id);
        return message;
    }

    public String reloadProductCache(String action, Long id) {
        String message = cacheConfig.reloadProductCache(action, id);
        return message;
    }
}
