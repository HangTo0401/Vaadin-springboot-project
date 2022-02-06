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
                log.info("Add supplier in cache: " + new Element(supplier.getId(), supplier));
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
                log.info("Add product in cache: " + new Element(product.getId(), product));
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

            supplierList.stream().forEach(System.out::println);
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
     * @param supplier
     * */
    public String reloadSupplierCache(String action, Supplier supplier) {
        log.info("Reload data in cache " + CacheName.SUPPLIER_CACHE);
        Supplier supplierEntry = null;
        String responseMessage = "";

        try {
            // Get record from db
            if (!action.equals("DELETE")) {
                supplierEntry = supplierRepository.findById(supplier.getId())
                                                  .orElseThrow(() -> new RuntimeException("Supplier not found"));
                if (action.equals("ADD")) {
                    // Add new record to cache
                    responseMessage = addNewSupplierToCache(supplierEntry);
                } else if (action.equals("UPDATE")) {
                    // Update record to cache
                    responseMessage = updateSupplierInCache(supplierEntry.getId());
                }
            } else {
                // Delete record to cache after delete in db successfully
                responseMessage = deleteSupplierInCache(supplier);
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
        log.info("Add new supplier in cache");
        String message = "";

        try {
            if (supplier != null) {
                log.info("New supplier in cache: " + new Element(supplier.getId(), supplier));
                supplierCache.put(new Element(supplier.getId(), supplier));
                message = "New supplier is added to cache successfully!";
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
        log.info("Update supplier in cache");
        String message = "";

        try {
            if (updateId != null) {
                Supplier existSupplier = (Supplier) supplierList.stream()
                                                                .filter(supplier -> supplier.getId() == updateId)
                                                                .findAny() // If 'findAny' then return found
                                                                .orElse(null); // If not found, return null
                log.info("Exist supplier in cache: " + new Element(existSupplier.getId(), existSupplier));
                supplierCache.put(new Element(existSupplier.getId(), existSupplier));
                message = "Exist supplier is updated in cache successfully!";
            } else {
                log.info("Exist supplier which is updated is invalid!");
                message = "Exist supplier which is updated is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    /**
     * Delete supplier in cache
     * @param deleteSupplier
     * */
    public String deleteSupplierInCache(Supplier deleteSupplier) {
        log.info("Delete supplier in cache");
        String message = "";

        try {
            if (deleteSupplier.getId() != null) {
                log.info("Delete supplier in cache: " + new Element(deleteSupplier.getId(), deleteSupplier));
                boolean deletedFlag = supplierCache.remove(deleteSupplier.getId());

                if (deletedFlag) {
                    // If we delete supplier which has products successfully, then delete products in cache too
                    List<Product> productListWithSupplier = productList.stream()
                                                                       .filter(product -> product.getSupplier().getId() == deleteSupplier.getId())
                                                                       .collect(Collectors.toList());
                    if (productListWithSupplier.size() > 0) {
                        for (Product deleteProduct : productListWithSupplier) {
                            if (deleteProduct != null) {
                                log.info("Delete products in cache: " + deleteProduct.getId() + " " + deleteProduct.getProductName());
                                productCache.remove(deleteProduct.getId());
                            }
                        }
                    }
                }

                message = deletedFlag ? "Supplier is deleted successfully!" : "Fail to delete supplier!";
            } else {
                log.info("Exist supplier which is deleted is invalid!");
                message = "Exist supplier which is deleted is invalid";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    /**
     * Reload data in product cache
     * @param action
     * @param product
     * */
    public String reloadProductCache(String action, Product product) {
        log.info("Reload data in cache " + CacheName.PRODUCT_CACHE);
        Product productEntry = null;
        String responseMessage = "";

        try {
            if (!action.equals("DELETE")) {
                // Get record from db
                productEntry = productRepository.findById(product.getId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));

                if (action.equals("ADD")) {
                    // Add new record to cache
                    responseMessage = addNewProductToCache(productEntry);
                } else if (action.equals("UPDATE")) {
                    // Update record to cache
                    responseMessage = updateProductInCache(productEntry.getId());
                }
            } else {
                // Delete record to cache
                responseMessage = deleteProductInCache(product);
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
                log.info("New product in cache: " + new Element(product.getId(), product));
                productCache.put(new Element(product.getId(), product));
                message = "New product is added to cache successfully!";
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
        log.info("Update product in cache: ");
        String message = "";

        try {
            if (updateId != null) {
                Product existProduct = (Product) productList.stream()
                                                            .filter(supplier -> supplier.getId() == updateId)
                                                            .findAny() // If 'findAny' then return found
                                                            .orElse(null); // If not found, return null
                log.info("Exist product in cache: " + new Element(existProduct.getId(), existProduct));
                productCache.put(new Element(existProduct.getId(), existProduct));
                message = "Exist product is updated in cache successfully!";
            } else {
                log.info("Exist product which is updated is invalid!");
                message = "Exist product which is updated is invalid!";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return message;
    }

    /**
     * Delete product in cache
     * @param deleteProduct
     * */
    public String deleteProductInCache(Product deleteProduct) {
        log.info("Delete product in cache: ");
        String message = "";

        try {
            if (deleteProduct.getId() != null) {
                log.info("Delete product in cache: " + new Element(deleteProduct.getId(), deleteProduct));
                boolean deletedFlag = productCache.remove(deleteProduct.getId());
                message = deletedFlag ? "Product is deleted in cache successfully!" : "Fail to delete product in cache";
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
