package com.example.demo.cache;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@EnableCaching //annotation enables the Spring Boot caching abstraction layer in our application.
@Configuration //annotation marks the CacheConfig class as a Spring configuration class.
public class EhCacheConfig {


//    @PostConstruct
//    private void loadDataFromDbToCache(){
//        //call any method
//
//    }
}
