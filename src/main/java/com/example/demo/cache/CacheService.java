package com.example.demo.cache;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.vaadin.flow.component.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CacheService {
    private final Logger log = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private CacheManager cacheManager;

    @Scheduled(cron = "0 0 0/1 * * ?")
    private void refreshCache() {
        cacheManager.getCacheNames().stream()
                    .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    public void addProductToCache(Product product) {
        Cache cache = cacheManager.getCache("productCache");
        cache.putIfAbsent(product.getId(), product);
    }

    public void addSupplierToCache(Supplier supplier) {
        Cache cache = cacheManager.getCache("supplierCache");
        cache.putIfAbsent(supplier.getId(), supplier);
    }

    // TODO: getEntryByKey
//
//    public void getEntryByKey(String cacheName, Key key) {
//        log.info("Get entry from " + cacheName + " by key :" + key);
//        Object result = null;
//        Cache cache = cacheManager.getCache("supplierCache");
//        cache.putIfAbsent(supplier.getId(), supplier);
//    }
}
