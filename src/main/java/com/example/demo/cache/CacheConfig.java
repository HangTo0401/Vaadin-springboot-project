package com.example.demo.cache;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@EnableCaching //annotation enables the Spring Boot caching abstraction layer in our application.
@Configuration //annotation marks the CacheConfig class as a Spring configuration class.
public class CacheConfig extends CachingConfigurerSupport {

//    @Autowired
//    private static CacheManager cacheManager;

    public static void initCacheManager() {
//        net.sf.ehcache.CacheManager.setCacheNames(Arrays.asList("supplierCache", "productCache"));
//        cacheManager.addCache(new Cache("castickets", 500, false, false, 30, 30));
    }

    @Bean
    public CacheManager ehCacheManager() {
        CacheConfiguration supplierCache = new CacheConfiguration();
        supplierCache.setName("supplierCache");
        supplierCache.setMemoryStoreEvictionPolicy("LRU");
        supplierCache.setOverflowToOffHeap(false);
        supplierCache.setMaxEntriesLocalHeap(1000);
        supplierCache.setTimeToLiveSeconds(10);
        supplierCache.setTimeToIdleSeconds(10);

        CacheConfiguration productCache = new CacheConfiguration();
        productCache.setName("productCache");
        productCache.setMemoryStoreEvictionPolicy("LRU");
        productCache.setOverflowToOffHeap(false);
        productCache.setMaxEntriesLocalHeap(1000);
        productCache.setTimeToLiveSeconds(10);
        productCache.setTimeToIdleSeconds(10);

        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        config.addCache(supplierCache);
        config.addCache(productCache);
        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    @Override
    public EhCacheCacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

//    private void init() {
//        cacheManager = CacheManager.newInstance("src/main/resources/ehcache.xml");
//        CacheManager.create();
//    }
}
