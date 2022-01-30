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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
    private ModelMapper modelMapper;

    private static Logger log = LoggerFactory.getLogger(MainService.class);

    public List<Supplier> getAllSuppliersFromCache(String stringFilter) {
        log.info("Find all suppliers and put in cache");
        if (stringFilter == null || stringFilter.isEmpty()) {
            log.info("Load data from cache " + CacheName.SUPPLIER_CACHE);
            List<Supplier> suppliers = cacheService.getAllSuppliersFromCache();
            return suppliers;
        } else {
            List<Supplier> supplierList = supplierRepository.search(stringFilter);
            return supplierList;
        }
    }

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
        }
        return newSupplier;
    }

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

    public void deleteSupplier(Supplier supplier) {
        supplierRepository.delete(supplier);
    }

    public boolean deleteSupplierById(Long id) {
        boolean isFound = false;
        try {
            supplierRepository.deleteById(id);
            isFound = supplierRepository.existsById(id);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isFound;
    }

    public Supplier updateSupplier(Supplier supplier) {
        if (supplier == null) {
            System.err.println("Supplier is null!");
            return null;
        }
        return supplierRepository.save(supplier);
    }

    public List<Product> getAllProducts(String stringFilter) {
        log.info("findAll products list");
        if (stringFilter == null || stringFilter.isEmpty()) {
            List<Product> productList = productRepository.findAll();
            return productList;
        } else {
            List<Product> productList = productRepository.search(stringFilter);
            return productList;
        }
    }

    public Product createProduct(@RequestBody Product product) {
        if (product == null) {
            System.err.println("Product is null!");
            return null;
        }
        return productRepository.save(product);
    }

    public Product getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        return null;
    }

    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

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

    public Product updateProduct(Product product) {
        if (product == null) {
            System.err.println("Product is null!");
            return null;
        }
        return productRepository.save(product);
    }

    public void showErrorNotification(String errMessage) {
        Notification notification = Notification.show(errMessage);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }

    public void showSuccessNotification(String successMessage) {
        Notification notification = Notification.show(successMessage);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }
}
