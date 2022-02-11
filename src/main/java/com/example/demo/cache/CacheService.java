package com.example.demo.cache;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;

import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;

import com.example.demo.exception.NotFoundException;

import net.sf.ehcache.Element;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;
import net.sf.ehcache.search.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CacheService {

    private final Logger log = LoggerFactory.getLogger(CacheService.class);

    @Autowired
    private CacheConfig cacheConfig;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    private CacheManager cacheManager;

    private Cache supplierCache;

    private Cache productCache;

    private List<Supplier> supplierList = new ArrayList<>();

    private List<Product> productList = new ArrayList<>();

    public CacheService() {
        log.info("============CacheService==========");
    }

    @PostConstruct
    private void initCache() {
        cacheManager = cacheConfig.getCacheManager();
        supplierCache = cacheManager.getCache(CacheName.SUPPLIER_CACHE);
        productCache = cacheManager.getCache(CacheName.PRODUCT_CACHE);
    }

    /**
     * Get all entries in supplier cache
     * @return List<Supplier>
     */
    public List<Supplier> getAllSuppliersFromCache() {
        log.info("Get all suppliers from cache");
        try {
            // Create a new query builder for this cache
            Query supplierCacheQuery = supplierCache.createQuery();

            // Return all element values from cache
            supplierCacheQuery.includeValues();

            // If too many results are returned, it could cause an OutOfMemoryError.
            // The maxResults clause is used to limit the number of results returned from the search.
            supplierCacheQuery.maxResults(1000);

            // Execute this query. Every call to this method will re-execute the query and return a distinct results object.
            Results results = supplierCacheQuery.execute();

            // List containing all the search results
            List<Result> all = results.all();

            // Convert list result to supplier list
            supplierList = all.stream().map(result -> (Supplier) result.getValue()).collect(Collectors.toList());
            supplierList.stream().forEach(System.out::println);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return supplierList;
    }

    /**
     * Get entry by key in supplier cache
     * @param id
     * @return Supplier
     */
    public Supplier getSupplierByKeyFromCache(Long id) {
        log.info("Get supplier from cache by id");
        Element element = null;

        try {
            element = supplierCache.get(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return element != null ? (Supplier) element.getObjectValue() : null;
    }

    /**
     * Reload entries in supplier cache
     * @param action
     * @param supplier
     * @return String
     */
    public String reloadSupplierCache(String action, Supplier supplier) {
        log.info("Reload data in cache " + CacheName.SUPPLIER_CACHE);
        Supplier supplierEntry = null;
        String responseMessage = "";

        try {
            // Get record from db
            if (!action.equals("DELETE")) {
                supplierEntry = supplierRepository.findById(supplier.getId())
                        .orElseThrow(() -> new NotFoundException("Supplier not found"));
                if (action.equals("ADD")) {
                    // Add new record to cache
                    responseMessage = addNewSupplierToCache(supplierEntry);
                } else if (action.equals("UPDATE")) {
                    // Update record to cache
                    responseMessage = updateSupplierInCache(supplier);
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
     * @return String
     * */
    public String addNewSupplierToCache(Supplier supplier) {
        log.info("Add new supplier in cache");
        String message = "";

        try {
            if (supplier != null) {
                log.info("New supplier in cache: " + new Element(supplier.getId(), supplier));
                supplierCache.put(new Element(supplier.getId(), supplier));
                message = "New supplier is added successfully!";
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
     * @param updatedSupplier
     * @return String
     * */
    public String updateSupplierInCache(Supplier updatedSupplier) {
        log.info("Update supplier in cache");
        String message = "";

        try {
            if (updatedSupplier.getId() != null) {
                Supplier existSupplier = getSupplierByKeyFromCache(updatedSupplier.getId());
                log.info("Exist supplier in cache: " + new Element(existSupplier.getId(), updatedSupplier));
                supplierCache.remove(existSupplier.getId());
                supplierCache.putIfAbsent(new Element(existSupplier.getId(), updatedSupplier));
                message = "Exist supplier is updated successfully!";
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
     * @return String
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
     * Get all entries in product cache
     * @return List<Product>
     */
    public List<Product> getAllProductsFromCache() {
        log.info("Get all products from cache");
        try {
            // Create a new query builder for this cache
            Query productCacheQuery = productCache.createQuery();

            // Return all element values from cache
            productCacheQuery.includeValues();

            // If too many results are returned, it could cause an OutOfMemoryError.
            // The maxResults clause is used to limit the number of results returned from the search.
            productCacheQuery.maxResults(1000);

            // Execute this query. Every call to this method will re-execute the query and return a distinct results object.
            Results results = productCacheQuery.execute();

            // List containing all the search results
            List<Result> all = results.all();

            // Convert list result to product list
            productList = all.stream().map(result -> (Product) result.getValue()).collect(Collectors.toList());
            productList.forEach(System.out::println);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return productList;
    }

    /**
     * Get entry by key in product cache
     * @param id
     * @return Product
     */
    public Product getProductByKeyFromCache(Long id) {
        log.info("Get product from cache by id");
        Element element = null;

        try {
            element = productCache.get(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return element != null ? (Product) element.getObjectValue() : null;
    }

    /**
     * Reload data in product cache
     * @param action
     * @param product
     * @return String
     * */
    public String reloadProductCache(String action, Product product) {
        log.info("Reload data in cache " + CacheName.PRODUCT_CACHE);
        Product productEntry = null;
        String responseMessage = "";

        try {
            if (!action.equals("DELETE")) {
                // Get record from db
                productEntry = productRepository.findById(product.getId())
                        .orElseThrow(() -> new NotFoundException("Product not found"));

                if (action.equals("ADD")) {
                    // Add new record to cache
                    responseMessage = addNewProductToCache(productEntry);
                } else if (action.equals("UPDATE")) {
                    // Update record to cache
                    responseMessage = updateProductInCache(product);
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
     * @return String
     * */
    public String addNewProductToCache(Product product) {
        log.info("Add new product in cache:");
        String message = "";

        try {
            if (product != null) {
                log.info("New product in cache: " + new Element(product.getId(), product));
                productCache.putIfAbsent(new Element(product.getId(), product));
                message = "New product is added successfully!";
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
     * @param updateProduct
     * @return String
     * */
    public String updateProductInCache(Product updateProduct) {
        log.info("Update product in cache: ");
        String message = "";

        try {
            if (updateProduct.getId() != null) {
                Product existProduct = getProductByKeyFromCache(updateProduct.getId());
                log.info("Exist product in cache: " + new Element(existProduct.getId(), existProduct));
                productCache.remove(existProduct.getId());
                productCache.putIfAbsent(new Element(existProduct.getId(), updateProduct));
                message = "Exist product is updated successfully!";
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
     * @return String
     * */
    public String deleteProductInCache(Product deleteProduct) {
        log.info("Delete product in cache: ");
        String message = "";

        try {
            if (deleteProduct.getId() != null) {
                log.info("Delete product in cache: " + new Element(deleteProduct.getId(), deleteProduct));
                boolean deletedFlag = productCache.remove(deleteProduct.getId());
                message = deletedFlag ? "Product is deleted successfully!" : "Fail to delete product";
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
