package com.example.demo.cache;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;

import lombok.Getter;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@EnableCaching //annotation enables the Spring Boot caching abstraction layer in our application.
@Configuration //annotation marks the CacheConfig class as a Spring configuration class.
public class CacheConfig {
    private final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Getter
    private CacheManager cacheManager;

    private Cache supplierCache;

    private Cache productCache;

    private SupplierRepository supplierRepository;

    private ProductRepository productRepository;

    private List<Supplier> supplierList = new ArrayList<>();

    private List<Product> productList = new ArrayList<>();

    public CacheConfig(SupplierRepository supplierRepository, ProductRepository productRepository) {
        super();
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        init();
    }

    private void init() {
        // Get config from ehcache.xml
        cacheManager = CacheManager.newInstance(getClass().getResource("/ehcache.xml"));
        CacheManager.create();

        supplierCache = cacheManager.getCache(CacheName.SUPPLIER_CACHE);
        productCache = cacheManager.getCache(CacheName.PRODUCT_CACHE);

        List<Supplier> supplierList = supplierRepository.findAll();
        List<Product> productList = productRepository.findAll();

        // Add suppliers to cache
        addAllSuppliersToCache(supplierList);

        // Add products to cache
        addAllProductsToCache(productList);
    }

    /**
     * Add all suppliers to cache
     * @param supplierList
     * */
    public void addAllSuppliersToCache(List<Supplier> supplierList) {
        log.info("Add all suppliers to cache");
        try {
            for (Supplier supplier : supplierList) {
                log.info("Element: " + new Element(supplier.getId(), supplier));
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
                log.info("Element: " + new Element(product.getId(), product));
                productCache.put(new Element(product.getId(), product));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get all suppliers from cache
     * */
    public List<Supplier> getAllSuppliersFromCache() {
        log.info("Get all suppliers from cache");
        try {
            Query supplierCacheQuery = supplierCache.createQuery();

            // Get list supplier from cache
            supplierList = supplierCacheQuery.includeValues()
                                             .execute().all()
                                             .stream().map(result -> (Supplier) result.getValue()).collect(Collectors.toList());
            supplierList.forEach(System.out::println);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return supplierList;
    }

    /**
     * Get all products from cache
     * */
    public List<Product> getAllProductsFromCache() {
        log.info("Get all products from cache");
        try {
            Query productCacheQuery = productCache.createQuery();

            // Get list supplier from cache
            productList = productCacheQuery.includeValues()
                                            .execute().all()
                                            .stream().map(result -> (Product) result.getValue()).collect(Collectors.toList());
            productList.forEach(System.out::println);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return productList;
    }

    /**
     * Search supplier by id from cache
     * @param id
     * */
    public Supplier getSupplierByIdFromCache(Long id) {
        log.info("Get supplier from cache");
        Element element = null;

        try {
            element = supplierCache.get(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return element != null ? (Supplier) element.getObjectValue() : null;
    }

    /**
     * Search product by id from cache
     * @param id
     * */
    public Product getProductByIdFromCache(Long id) {
        log.info("Get product from cache");
        Element element = null;

        try {
            element = productCache.get(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return element != null ? (Product) element.getObjectValue() : null;
    }

    /**
     * Reload data in supplier cache
     * @param action
     * @param id
     * */
    public String reloadSupplierCache(String action, Long id) {
        log.info("Reload data in cache " + CacheName.SUPPLIER_CACHE);
        String responseMessage = "";

        try {
            // Get record from db
            Supplier supplier = supplierRepository.findById(id).orElseThrow(() -> new RuntimeException("Supplier not found"));

            if (action.equals("ADD")) {
                // Add new record to cache
                responseMessage = addNewSupplierToCache(supplier);
            } else if (action.equals("UPDATE")) {
                // Update record to cache
                responseMessage = updateSupplierInCache(supplier.getId());
            } else if (action.equals("DELETE")) {
                // Delete record to cache
                responseMessage = deleteSupplierInCache(supplier.getId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return responseMessage;
    }

    /**
     * Add new supplier to cache
     * @param supplier
     * */
    public String addNewSupplierToCache(Supplier supplier) {
        log.info("Add new supplier in cache:");
        String message = "";
        try {
            if (supplier != null) {
                log.info("New element: " + new Element(supplier.getId(), supplier));
                supplierCache.put(new Element(supplier.getId(), supplier));
                message = "New supplier saved to cache successfully!";
            } else {
                log.info("New supplier is invalid!");
                message = "New supplier is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    /**
     * Update supplier in cache
     * @param updateId
     * */
    public String updateSupplierInCache(Long updateId) {
        log.info("Update supplier in cache:");
        String message = "";

        try {
            if (updateId != null) {
                Supplier existSupplier = (Supplier) supplierList.stream().filter(supplier -> supplier.getId() == updateId);
                log.info("Exist element: " + new Element(existSupplier.getId(), existSupplier));
                supplierCache.put(new Element(existSupplier.getId(), existSupplier));
                message = "Exist supplier updated in cache successfully!";
            } else {
                log.info("Supplier is invalid!");
                message = "Supplier is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    /**
     * Delete supplier in cache
     * @param deleteId
     * */
    public String deleteSupplierInCache(Long deleteId) {
        log.info("Delete supplier in cache:");
        String message = "";

        try {
            if (deleteId != null) {
                Supplier existSupplier = (Supplier) supplierList.stream().filter(supplier -> supplier.getId() == deleteId);
                log.info("Delete element: " + new Element(existSupplier.getId(), existSupplier));
                supplierCache.remove(new Element(existSupplier.getId(), existSupplier));
                message = "Supplier deleted in cache successfully!";
            } else {
                log.info("Delete supplier is invalid!");
                message = "Delete supplier is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    /**
     * Reload data in product cache
     * @param action
     * @param id
     * */
    public String reloadProductCache(String action, Long id) {
        log.info("Reload data in cache " + CacheName.PRODUCT_CACHE);
        String responseMessage = "";

        try {
            // Get record from db
            Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

            if (action.equals("ADD")) {
                // Add new record to cache
                responseMessage = addNewProductToCache(product);
            } else if (action.equals("UPDATE")) {
                // Update record to cache
                responseMessage = updateProductInCache(product.getId());
            } else if (action.equals("DELETE")) {
                // Delete record to cache
                responseMessage = deleteProductInCache(product.getId());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return responseMessage;
    }

    /**
     * Add new product to cache
     * @param product
     * */
    public String addNewProductToCache(Product product) {
        log.info("Add new product in cache:");
        String message = "";

        try {
            if (product != null) {
                log.info("New element: " + new Element(product.getId(), product));
                supplierCache.put(new Element(product.getId(), product));
            } else {
                log.info("New product is invalid!");
                message = "New product is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    /**
     * Update product in cache
     * @param updateId
     * */
    public String updateProductInCache(Long updateId) {
        log.info("Update product in cache:");
        String message = "";

        try {
            if (updateId != null) {
                Product existProduct = (Product) productList.stream().filter(product -> product.getId() == updateId);
                log.info("Exist element: " + new Element(existProduct.getId(), existProduct));
                supplierCache.put(new Element(existProduct.getId(), existProduct));
            } else {
                log.info("Product is invalid!");
                message = "Product is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    /**
     * Delete product in cache
     * @param deleteId
     * */
    public String deleteProductInCache(Long deleteId) {
        log.info("Delete product in cache:");
        String message = "";

        try {
            if (deleteId != null) {
                Product existProduct = (Product) productList.stream().filter(product -> product.getId() == deleteId);
                log.info("Delete element: " + new Element(existProduct.getId(), existProduct));
                supplierCache.remove(new Element(existProduct.getId(), existProduct));
            } else {
                log.info("Delete product is invalid!");
                message = "Delete product is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }
}
