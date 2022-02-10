package com.example.demo.service;

import com.example.demo.cache.CacheConfig;
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

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private CacheConfig cacheConfig;

    private static Logger log = LoggerFactory.getLogger(MainService.class);

    /**
     * Get all suppliers from cache
     * @return List<Supplier>
     * */
    public List<Supplier> getAllSuppliersFromCache() {
        log.info("Find all suppliers and put in cache");
        List<Supplier> supplierList = new ArrayList<>();

        try {
            log.info("Load data from cache " + CacheName.SUPPLIER_CACHE);
            supplierList = cacheService.getAllSuppliersFromCache();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Could not create supplier");
        }
        return supplierList;
    }

    /**
     * Create supplier
     * @param supplier
     * @return Supplier
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
     * @return Supplier
     * */
    public Supplier getSupplierById(Long id) {
        try {
            Optional<Supplier> optionalSupplier = supplierRepository.findById(id);
            if (optionalSupplier.isPresent()) {
                return optionalSupplier.get();
            }
        } catch (Exception e) {
            log.error("Could not retrieve supplier from cache with id: " + id);
        }
        return null;
    }

    /**
     * Delete supplier by id
     * @param id
     * @return boolean
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
     * @return Supplier
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
     * Get all products from cache
     * @return List<Product>
     * */
    public List<Product> getAllProductsFromCache() {
        log.info("Find all products and put in cache");
        List<Product> productList = new ArrayList<>();

        try {
            log.info("Load data from cache " + CacheName.PRODUCT_CACHE);
            productList = cacheService.getAllProductsFromCache();
            return productList;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return productList;
    }

    /**
     * Create product
     * @param product
     * @return Product
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
     * @return Product
     * */
    public Product getProductById(Long productId) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isPresent()) {
                return optionalProduct.get();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Delete product by id
     * @param id
     * @return boolean
     * */
    public boolean deleteProductById(Long id) {
        boolean isFound = false;

        try {
            productRepository.deleteById(id);
            isFound = productRepository.existsById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Could not delete product with id: " + id);
        }
        return isFound;
    }

    /**
     * Update product
     * @param product
     * @return Product
     * */
    public Product updateProduct(Product product) {
        Product updateProduct = null;

        try {
            if (product == null) {
                System.err.println("Product is null!");
                return null;
            }
            updateProduct = productRepository.save(product);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.error("Could not update product with id: " + product.getId());
        }
        return updateProduct;
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
