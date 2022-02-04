package com.example.demo.service;

import com.example.demo.cache.CacheName;
import com.example.demo.cache.CacheService;
import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MainService {
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CacheService cacheService;

    private static Logger log = LoggerFactory.getLogger(MainService.class);

    /**
     * Get all suppliers from cache
     * @param stringFilter
     * */
    public List<Supplier> getAllSuppliersFromCache(String stringFilter) {
        log.info("Find all suppliers and put in cache");
        List<Supplier> supplierList = new ArrayList<>();

        try {
            if (stringFilter == null || stringFilter.isEmpty()) {
                log.info("Load data from cache " + CacheName.SUPPLIER_CACHE);
                supplierList = cacheService.getAllSuppliersFromCache();
            } else {
                supplierList = supplierRepository.search(stringFilter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Could not create supplier");
        }
        return supplierList;
    }

    /**
     * Create supplier
     * @param supplier
     * */
    public Supplier createSupplier(Supplier supplier) {
        Supplier newSupplier = null;

        try {
            if (supplier == null) {
                System.err.println("Supplier is null!");
                return null;
            }
            newSupplier = supplierRepository.save(supplier);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Could not create new supplier");
        }
        return newSupplier;
    }

    /**
     * Get supplier by id
     * @param id
     * */
    public Supplier getSupplierById(Long id) {
        Supplier supplier = new Supplier();

        try {
            if (supplier != null) {
                return supplier;
            } else {
                Optional<Supplier> optionalSupplier = supplierRepository.findById(id);
                if (optionalSupplier.isPresent()) {
                    return optionalSupplier.get();
                }
            }
        } catch (Exception e) {
            log.error("Could not retrieve supplier from cache with id: " + id);
        }
        return supplier;
    }

    /**
     * Delete supplier
     * @param supplier
     * */
    public void deleteSupplier(Supplier supplier) {
        try {
            supplierRepository.delete(supplier);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Could not delete supplier with id: " + supplier.getId());
        }
    }

    /**
     * Delete supplier by id
     * @param id
     * */
    public boolean deleteSupplierById(Long id) {
        boolean isFound = false;

        try {
            supplierRepository.deleteById(id);
            isFound = supplierRepository.existsById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Could not delete supplier with id: " + id);
        }
        return isFound;
    }

    /**
     * Update supplier
     * @param supplier
     * */
    public Supplier updateSupplier(Supplier supplier) {
        Supplier updateSupplier = null;

        try {
            if (supplier == null) {
                System.err.println("Supplier is null!");
                return null;
            }
            updateSupplier = supplierRepository.save(supplier);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Could not update supplier with id: " + supplier.getId());
        }
        return updateSupplier;
    }

    /**
     * Get all products
     * @param stringFilter
     * */
    public List<Product> getAllProductsFromCache(String stringFilter) {
        log.info("Find all products and put in cache");
        List<Product> productList = new ArrayList<>();

        try {
            if (stringFilter == null || stringFilter.isEmpty()) {
                log.info("Load data from cache " + CacheName.PRODUCT_CACHE);
                productList = cacheService.getAllProductsFromCache();
                return productList;
            } else {
                productList = productRepository.search(stringFilter);
                return productList;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return productList;
    }

    /**
     * Create product
     * @param product
     * */
    public Product createProduct(Product product) {
        Product newProduct = null;
        try {
            if (product == null) {
                System.err.println("Product is null!");
                return null;
            }
            newProduct = productRepository.save(product);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return newProduct;
    }

    /**
     * Get product by id
     * @param productId
     * */
    public Product getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        return null;
    }

    /**
     * Delete product
     * @param product
     * */
    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    /**
     * Delete product by id
     * @param id
     * */
    public boolean deleteProductById(Long id) {
        boolean isFound = false;
        try {
            productRepository.deleteById(id);
            isFound = productRepository.existsById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isFound;
    }

    /**
     * Update product
     * @param product
     * */
    public Product updateProduct(Product product) {
        if (product == null) {
            System.err.println("Product is null!");
            return null;
        }
        return productRepository.save(product);
    }

    /**
     * Show error notification
     * @param errMessage
     * */
    public void showErrorNotification(String errMessage) {
        Notification notification = Notification.show(errMessage);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }

    /**
     * Show success notification
     * @param successMessage
     * */
    public void showSuccessNotification(String successMessage) {
        Notification notification = Notification.show(successMessage);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }
}
