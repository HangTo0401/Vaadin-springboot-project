package com.example.demo;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.ehcache.config.CacheConfiguration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.example.demo")
@EnableVaadin
@EnableCaching
public class DemoApplication {
	@Autowired
	private CacheManager cacheManager;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Test
	public void testCache() {

	}
}
