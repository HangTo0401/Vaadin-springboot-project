package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.SupplierDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MainService {
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CachingService cachingService;

    @Autowired
    private ModelMapper modelMapper;

    private static Logger log = LoggerFactory.getLogger(MainService.class);

    @CacheEvict(allEntries = true)
    public void clearCache(){}

//    @Cacheable(value = "supplierCache", key = "'suppliers'")
    public List<Supplier> getAllSuppliers(String stringFilter) {
        log.info("MainService: findAll suppliers list");
        if (stringFilter == null || stringFilter.isEmpty()) {
            List<Supplier> supplierList = supplierRepository.findAll();

            // entity to DTO
//            List<SupplierDTO> supplierResponse = supplierList.stream().map(supplier -> modelMapper.map(supplier, SupplierDTO.class)).collect(Collectors.toList());;

            for (Supplier supplier : supplierList) {
                cachingService.addSupplierToCache(supplier);
            }
            return supplierList;
        } else {
            List<Supplier> supplierList = supplierRepository.search(stringFilter);

            // entity to DTO
//            List<SupplierDTO> supplierResponse = supplierList.stream().map(supplier -> modelMapper.map(supplier, SupplierDTO.class)).collect(Collectors.toList());;

            for (Supplier supplier : supplierList) {
                cachingService.addSupplierToCache(supplier);
            }
            return supplierList;
        }
    }
    @Cacheable(value = "supplierCache", key = "'suppliers'")
    public List<String> getSuppliersName() {
        log.info("MainService: findAll suppliers list");
        List<String> supplierNameList = supplierRepository.findAllSuppliersName();
        return supplierNameList;
    }

    // .findById(doctorId).orElseThrow(() -> new RuntimeException("doctor not found"));
    @CachePut(value = "supplierCache", key = "#result.id")
    public Supplier createSupplier(@RequestBody Supplier supplier) {
        // convert DTO to entity
//        Supplier supplierRequest = modelMapper.map(supplierDTO, Supplier.class);
//
//        Supplier supplier = supplierRepository.save(supplierRequest);
//
//        // convert entity to DTO
//        SupplierDTO supplierResponse = modelMapper.map(supplier, SupplierDTO.class);
        if (supplier == null) {
            System.err.println("Supplier is null!");
            return null;
        }
        return supplierRepository.save(supplier);
    }

    @Cacheable(value = "supplierCache", key = "#id", unless = "#result=null")
    public Supplier getSupplierById(Long id) {
        Optional<Supplier> optionalSupplier = supplierRepository.findById(id);
        if (optionalSupplier.isPresent()) {
//            SupplierDTO supplierResponse = modelMapper.map(optionalSupplier.get(), SupplierDTO.class);
            return optionalSupplier.get();
        }
        return null;
    }

    @CacheEvict(value = "supplierCache", key = "#supplier", allEntries = true)
    public void deleteSupplier(Supplier supplier) {
        supplierRepository.delete(supplier);
    }

    @CacheEvict(value = "supplierCache", key = "#id", allEntries = true)
    public void deleteSupplierById(Long id) {
        supplierRepository.deleteById(id);
    }

//    @CachePut(value = "supplierCache", key = "#supplier.id")
    public Supplier updateSupplier(Supplier supplier) {
        // convert DTO to Entity
//        Supplier supplierRequest = modelMapper.map(supplierDTO, Supplier.class);
//
//        Supplier supplier = supplierRepository.save(supplierRequest);
//
//        // entity to DTO
//        SupplierDTO supplierResponse = modelMapper.map(supplier, SupplierDTO.class);
        if (supplier == null) {
            System.err.println("Supplier is null!");
            return null;
        }
        return supplierRepository.save(supplier);
    }

//    @Cacheable(value = "productCache", key = "'products'")
    public List<Product> getAllProducts(String stringFilter) {
        log.info("MainService: findAll products list");
        if (stringFilter == null || stringFilter.isEmpty()) {
            List<Product> productList = productRepository.findAll();

            // entity to DTO
//            List<ProductDTO> productResponse = productList.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());;

            for (Product product : productList) {
                cachingService.addProductToCache(product);
            }
            return productList;
        } else {
            List<Product> productList = productRepository.search(stringFilter);
            // entity to DTO
//            List<ProductDTO> productResponse = productList.stream().map(product -> modelMapper.map(product, ProductDTO.class)).collect(Collectors.toList());;

            for (Product product : productList) {
                cachingService.addProductToCache(product);
            }
            return productList;
        }
    }

    @CachePut(value = "productCache", key = "#result.id")
    public Product createProduct(@RequestBody Product product) {
        // convert DTO to entity
//        Product productRequest = modelMapper.map(productDTO, Product.class);
//
//        Product product = productRepository.save(productRequest);
//
//        // convert entity to DTO
//        ProductDTO productResponse = modelMapper.map(product, ProductDTO.class);
        if (product == null) {
            System.err.println("Product is null!");
            return null;
        }
        return productRepository.save(product);
    }

    @Cacheable(value = "productCache", key = "#id", unless = "#result=null")
    public Product getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
//            ProductDTO productResponse = modelMapper.map(optionalProduct.get(), ProductDTO.class);
            return optionalProduct.get();
        }
        return null;
    }

    @CacheEvict(value = "productCache", key = "#product", allEntries = true)
    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }

    @CacheEvict(value = "productCache", key = "#id", allEntries = true)
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }

    @CachePut(value = "productCache", key = "#product.id")
    public Product updateProduct(Product product) {
        // convert DTO to Entity
//        Product productRequest = modelMapper.map(productDTO, Product.class);
//
//        Product product = productRepository.save(productRequest);
//
//        // entity to DTO
//        ProductDTO productResponse = modelMapper.map(product, ProductDTO.class);
        if (product == null) {
            System.err.println("Product is null!");
            return null;
        }
        return productRepository.save(product);
    }
}
