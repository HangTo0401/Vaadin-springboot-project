package com.example.demo;

import com.vaadin.flow.spring.annotation.EnableVaadin;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;

import javax.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages = "com.example.demo")
@EnableVaadin
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Test
	public void testCache() {

	}
}
