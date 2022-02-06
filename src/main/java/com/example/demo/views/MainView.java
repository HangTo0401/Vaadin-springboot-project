package com.example.demo.views;

import com.example.demo.cache.CacheService;
import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.service.MainService;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;

import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Theme(themeFolder = "demo")
@Route(value = "")
public class MainView extends VerticalLayout {

    private String[] buttonsName = new String[] {"Supplier", "Product"};
    private String DEFAULT_LAYOUT = "Supplier";

    private HorizontalLayout buttonsLayout;
    private HorizontalLayout supplierActionButtonsLayout;
    private HorizontalLayout productActionButtonsLayout;

    private VerticalLayout mainLayout;
    private VerticalLayout supplierVerticalLayout;
    private VerticalLayout productVerticalLayout;

    private Grid<Supplier> supplierGrid;
    private Grid<Product> productGrid;

    private TextField productFilterText;
    private TextField supplierFilterText;

    private Dialog newSupplierDialog;
    private Dialog newProductDialog;

    private Dialog supplierDetailDialog;
    private Dialog productDetailDialog;

    private final MainService service;
    private final CacheService cacheService;

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    private List<Supplier> listSuppliers = new ArrayList<>();
    private List<Product> listProducts = new ArrayList<>();

    private ListDataProvider<Supplier> listSupplierDataProvider;
    private ListDataProvider<Product> listProductDataProvider;

    private NewSupplierForm newSupplierForm;
    private NewProductForm newProductForm;

    private SupplierDetailForm supplierDetailForm;
    private ProductDetailForm productDetailForm;

    public MainView(MainService service, CacheService cacheService, SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.service = service;
        this.cacheService = cacheService;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        addClassName("main-view");
        setupLayout(DEFAULT_LAYOUT);

        add(mainLayout);
        updateSupplierGrid();
    }

    private void configureForms() {
        supplierDetailForm = new SupplierDetailForm(supplierDetailDialog,
                                                    supplierGrid,
                                                    service,
                                                    cacheService,
                                                    service.getAllProductsFromCache(""),
                                                    service.getAllSuppliersFromCache(""));
        supplierDetailForm.setWidth("25em");

        productDetailForm = new ProductDetailForm(productDetailDialog,
                                                  productGrid,
                                                  service,
                                                  cacheService,
                                                  service.getAllProductsFromCache(""),
                                                  service.getAllSuppliersFromCache(""));
        productDetailForm.setWidth("25em");
    }

    private void createButtons(String[] btnsName) {
        createTopButton(btnsName[0]);
        createTopButton(btnsName[1]);
        mainLayout.add(buttonsLayout);
    }

    private void createTopButton(String btnName) {
        Button button = new Button(btnName);
        button.addClickListener(click -> {
            if (btnName.equals("Supplier")) {
                mainLayout.replace(productActionButtonsLayout, supplierActionButtonsLayout);
                mainLayout.replace(productVerticalLayout, supplierVerticalLayout);
            } else if (btnName.equals("Product")) {
                mainLayout.replace(supplierActionButtonsLayout, productActionButtonsLayout);
                mainLayout.replace(supplierVerticalLayout, productVerticalLayout);
            }
        });
        button.addClickShortcut(Key.ENTER);
        buttonsLayout.add(button);
    }

    private void createNewSupplierButton(String btnName, String title) {
        newSupplierDialog.getElement().setAttribute("aria-label", title);

        Button button = new Button(title, e -> {
            newSupplierDialog = new Dialog();
            newSupplierForm = new NewSupplierForm(newSupplierDialog,
                                                  supplierGrid,
                                                  supplierFilterText.getValue(),
                                                  service,
                                                  cacheService,
                                                  service.getAllProductsFromCache(""),
                                                  service.getAllSuppliersFromCache(""));
            newSupplierForm.setWidth("25em");
            newSupplierForm.setId("new-supplier-form");
            newSupplierDialog.add(newSupplierForm);
            newSupplierDialog.open();
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newSupplierDialog, button);
        supplierActionButtonsLayout.add(button);
    }

    private void createNewProductButton(String btnName, String title) {
        newProductDialog.getElement().setAttribute("aria-label", title);

        Button button = new Button(title, e -> {
            newProductDialog = new Dialog();
            newProductForm = new NewProductForm(newProductDialog,
                                                productGrid,
                                                productFilterText.getValue(),
                                                service,
                                                cacheService,
                                                service.getAllProductsFromCache(""),
                                                service.getAllSuppliersFromCache(""));
            newProductForm.setWidth("25em");
            newProductDialog.add(newProductForm);
            newProductDialog.open();
        });
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newProductDialog, button);
        productActionButtonsLayout.add(button);
    }

    /**
     * Delete Supplier in grid
     */
    private void deleteSupplier(Supplier supplier) {
        String message = "";
        boolean isFound = service.deleteSupplierById(supplier.getId());
        if (isFound) {
            message = "Fail to delete supplier: " + supplier.getName();
            service.showErrorNotification(message);
        } else {
            // Delete entry in cache
            message = cacheService.reloadSupplierCache("DELETE", supplier);
            service.showSuccessNotification(message);

            // Refresh supplier grid
            ListDataProvider<Supplier> dataProvider = (ListDataProvider<Supplier>) supplierGrid.getDataProvider();
            dataProvider.getItems().remove(supplier);
            dataProvider.refreshAll();
            updateProductGrid();
        }
    }

    /**
     * Update Supplier grid
     */
    private void updateSupplierGrid() {
        supplierGrid.setItems(service.getAllSuppliersFromCache(supplierFilterText.getValue()));
    }

    /**
     * Update Product grid
     */
    private void updateProductGrid() {
        productGrid.setItems(service.getAllProductsFromCache(""));
    }

    /**
     * Delete Product in grid
     */
    private void deleteProduct(Product product) {
        String message = "";
        boolean isFound = service.deleteProductById(product.getId());
        if (isFound) {
            message = "Fail to delete product: " + product.getProductName();
            service.showErrorNotification(message);
        } else {
            // Delete entry in cache
            message = cacheService.reloadProductCache("DELETE", product);
            service.showSuccessNotification(message);

            // Refresh product grid
            ListDataProvider<Product> dataProvider = (ListDataProvider<Product>) productGrid.getDataProvider();
            dataProvider.getItems().remove(product);
            dataProvider.refreshAll();
        }
    }

    private void createSupplierSearchArea() {
        supplierFilterText.setPlaceholder("Search");
        supplierFilterText.setClearButtonVisible(true);
        supplierFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        supplierFilterText.addValueChangeListener(e -> updateSupplierGrid());
        supplierActionButtonsLayout.add(supplierFilterText);

        // New button
        createNewSupplierButton("Supplier", "New Supplier");
    }

    private void createProductSearchArea() {
        productFilterText.setPlaceholder("Search by name...");
        productFilterText.setClearButtonVisible(true);
        productFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        productFilterText.addValueChangeListener(e -> updateProductGrid());
        productActionButtonsLayout.add(productFilterText);

        // New button
        createNewProductButton("Product", "New Product");
    }

    private void setupLayout(String layoutName) {
        // Horizontal layout
        buttonsLayout = new HorizontalLayout();
        supplierActionButtonsLayout = new HorizontalLayout();
        productActionButtonsLayout = new HorizontalLayout();

        // Vertical layout
        mainLayout = new VerticalLayout();
        supplierVerticalLayout = new VerticalLayout();
        productVerticalLayout = new VerticalLayout();

        // Search text
        productFilterText = new TextField();
        supplierFilterText = new TextField();

        newSupplierDialog = new Dialog();
        newProductDialog = new Dialog();

        supplierDetailDialog = new Dialog();
        productDetailDialog = new Dialog();

        supplierGrid = new Grid<>(Supplier.class, false);
        productGrid = new Grid<>(Product.class, false);

        configureForms();

        supplierDetailDialog.add(supplierDetailForm);
        productDetailDialog.add(productDetailForm);

        setLayoutContent(layoutName);
    }

    private void setLayoutContent(String layoutName) {
        // Create buttons
        createButtons(buttonsName);

        // Create search area
        createSupplierSearchArea();
        createProductSearchArea();
        mainLayout.add(supplierActionButtonsLayout);

        // Init grid
        initSupplierGrid();
        initProductGrid();
        mainLayout.add(supplierVerticalLayout);

        // Add dialogs
        mainLayout.add(new VerticalLayout(supplierDetailDialog, productDetailDialog));
    }

    private void initSupplierGrid() {
        supplierGrid.addClassNames("supplier-grid");

        listSuppliers = service.getAllSuppliersFromCache("");
        listSupplierDataProvider = DataProvider.ofCollection(listSuppliers);

        supplierGrid.addColumn(Supplier::getName).setHeader(new Html("<b>Name</b>"));
        supplierGrid.addColumn(
                                dateOfBirth -> {
                                    LocalDate localDate = LocalDate.ofInstant(dateOfBirth.getDateOfBirth().toInstant(),
                                            ZoneId.systemDefault());
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                    return localDate.format(formatter);
                                }, "dateOfBirth"
                    ).setHeader(new Html("<b>Birthdate</b>"));
        supplierGrid.addColumn(Supplier::getEmail).setHeader(new Html("<b>Email</b>"));
        supplierGrid.addColumn(Supplier::getPhoneNumber).setHeader(new Html("<b>Phone Number</b>"));
        supplierGrid.addColumn(Supplier::getAddress).setHeader(new Html("<b>Address</b>"));

        supplierGrid.addColumn(new ComponentRenderer<>(supplier -> {
                        // Button for editing supplier and update to database and cache
                        Button editBtn = new Button("Edit", event -> {
                            supplierDetailDialog.open();
                            supplierDetailForm.setSupplier(supplier);
                            supplierGrid.getDataProvider().refreshItem(supplier);
                        });
                        editBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

                        // Button for removing supplier and update to database and cache
                        Button removeBtn = new Button("Delete", event -> {
                            deleteSupplier(supplier);
                        });
                        removeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

                        // Layouts for placing the buttons
                        HorizontalLayout buttons = new HorizontalLayout(editBtn, removeBtn);
                        return new VerticalLayout(buttons);
                    })).setHeader(new Html("<b>Actions</b>"));
        supplierGrid.setItems(listSuppliers);
        supplierGrid.setDataProvider(listSupplierDataProvider);
        supplierVerticalLayout.add(supplierGrid);
    }

    private void initProductGrid() {
        productGrid.addClassNames("product-grid");

        listProducts = service.getAllProductsFromCache("");
        listProductDataProvider = DataProvider.ofCollection(listProducts);

        productGrid.addColumn(Product::getProductName).setHeader(new Html("<b>Product Name</b>"));
        productGrid.addColumn(Product::getQuantity).setHeader(new Html("<b>Quantity</b>"));
        productGrid.addColumn(new NumberRenderer<>(Product::getPrice, "$%(,.2f", Locale.US, "$0.00")).setHeader(new Html("<b>Price</b>"));
        productGrid.addColumn(Product::getSupplierName).setHeader(new Html("<b>Supplier Name</b>"));

        productGrid.addColumn(new ComponentRenderer<>(product -> {
            // Button for editing person to backend
            Button editBtn = new Button("Edit", event -> {
                productDetailDialog.open();
                productDetailForm.setProduct(product);
                productGrid.getDataProvider().refreshItem(product);
            });
            editBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

            // Button for removing person
            Button removeBtn = new Button("Delete", event -> {
                deleteProduct(product);
            });
            removeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

            // Layouts for placing the buttons
            HorizontalLayout buttons = new HorizontalLayout(editBtn, removeBtn);
            return new VerticalLayout(buttons);
        })).setHeader(new Html("<b>Actions</b>"));
        productGrid.setItems(listProducts);
        productGrid.setDataProvider(listProductDataProvider);
        productVerticalLayout.add(productGrid);
    }
}
