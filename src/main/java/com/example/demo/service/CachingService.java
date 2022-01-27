package com.example.demo.service;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CachingService {
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
}
