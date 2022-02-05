package com.example.demo.views;

import com.example.demo.cache.CacheService;
import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.service.MainService;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import com.vaadin.flow.shared.Registration;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NewProductForm extends FormLayout {
    // Other fields omitted
    Binder<Product> binder = new BeanValidationBinder<>(Product.class);

    H2 headline = new H2("Product");

    TextField firstname = new TextField("Firstname");
    TextField lastname = new TextField("Lastname");
    TextField quantity = new TextField("Quantity");
    TextField price = new TextField("Price");

    ComboBox<Supplier> supplierComboBox = new ComboBox<>("Supplier");

    Button save = new Button("Save");
    Button cancel = new Button("Cancel");

    private MainService service;
    private CacheService cacheService;

    private List<Product> productsList;
    private List<Supplier> suppliersList;

    private boolean isSavedSuccess = false;

    private String filterText;
    private Dialog dialog;
    private Grid<Product> grid;

    public NewProductForm(Dialog dialog,
                          Grid<Product> grid,
                          String filterText,
                          MainService service,
                          CacheService cacheService,
                          List<Product> productsList,
                          List<Supplier> suppliersList) {
        this.dialog = dialog;
        this.grid = grid;
        this.filterText = filterText;
        this.service = service;
        this.cacheService = cacheService;
        this.productsList = productsList;
        this.suppliersList = suppliersList;

        addClassName("new-product-form");
        setPlaceHolder();
        createSupplierComboBox();
        validateForm();
        binder.bindInstanceFields(this);
        this.addListener(NewProductForm.SaveEvent.class, this::saveNewProduct);

        add(headline,
            firstname,
            lastname,
            quantity,
            price,
            supplierComboBox,
            createButtonsLayout());
    }

    private Product product;

    // other methods and fields omitted
    public void setProduct(Product product) {
        this.product = product;
        binder.readBean(product);
    }

    private void setPlaceHolder() {
        firstname.setPlaceholder("Enter firstname...");
        lastname.setPlaceholder("Enter lastname...");
        quantity.setPlaceholder("Enter quantity...");
        price.setPlaceholder("Enter price...");
        supplierComboBox.setPlaceholder("Select supplier");
    }

    public Map<Long, String> convertListToMap(List<Supplier> list) {
        // key = id, value - supplier name
        Map<Long, String> map = list.stream().collect(
                                Collectors.toMap(Supplier::getId, Supplier::getName));
        return map;
    }

    private void saveNewProduct(NewProductForm.SaveEvent saveEvent) {
        String message = "";

        try {
            Product newProduct = service.createProduct(saveEvent.getProduct());
            message = newProduct != null ? cacheService.reloadProductCache("ADD", newProduct.getId()) : "";
            isSavedSuccess = true;
        } catch (Exception exception) {
            exception.printStackTrace();
            isSavedSuccess = false;
        }
        if (isSavedSuccess) {
            firstname.clear();
            lastname.clear();
            quantity.clear();
            price.clear();
            supplierComboBox.clear();

            if (!message.equals("")) {
                service.showSuccessNotification(message);
            } else {
                service.showErrorNotification("New supplier cannot be saved successfully!");
            }

            updateProductGrid();
            fireEvent(new NewProductForm.CloseEvent(this));
        } else {
            service.showErrorNotification("New product cannot be saved!");
        }
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            validateAndSave();
            binder.validate();
        });
        save.addClickShortcut(Key.ENTER);

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(e -> fireEvent(new NewProductForm.CloseEvent(this)));

        binder.addStatusChangeListener(event -> save.setEnabled(binder.isValid()));
        
        return new HorizontalLayout(save, cancel);
    }

    private void createSupplierComboBox() {
        supplierComboBox.setAllowCustomValue(true);
        ComboBox.ItemFilter<Supplier> filter = (supplier, filterString) -> supplier.getName().toLowerCase().startsWith(filterString.toLowerCase());
        supplierComboBox.setItems(filter, suppliersList);
        supplierComboBox.setItemLabelGenerator(Supplier::getName);
    }

    private void validateAndSave() {
        try {
            if (binder.isValid()) {
                if (product == null) {
                    addNewProduct(new Product());
                }
                binder.writeBean(product);
                fireEvent(new NewProductForm.SaveEvent(this, product));
            }
        } catch (ValidationException e) {
            service.showErrorNotification("Validation error count: " + e.getValidationErrors().size());
            e.printStackTrace();
        }
    }

    private void validateForm() {
        // Firstname
        binder.forField(firstname).asRequired("Required")
              .withValidator(firstname -> !firstname.isBlank() && !firstname.isEmpty(), "Firstname is required field!")
              .withValidator(firstname -> firstname.length() >= 3, "Firstname must contain at least 3 characters")
              .withValidator(firstname -> StringUtils.isAlphaSpace(firstname), "Firstname must be a string")
              .bind(Product::getFirstname, Product::setFirstname);

        // Lastname
        binder.forField(lastname).asRequired("Required")
              .withValidator(lastname -> !lastname.isBlank() && !lastname.isEmpty(), "Lastname is required field!")
              .withValidator(lastname -> lastname.length() >= 2, "Lastname must contain at least 2 characters")
              .withValidator(lastname -> StringUtils.isAlphaSpace(lastname), "Lastname must be a string")
              .bind(Product::getLastname, Product::setLastname);

        // Quantity
        binder.forField(quantity).asRequired("Required")
              .withValidator(quantity -> !quantity.isBlank() && !quantity.isEmpty(), "Quantity is required field!")
              .withConverter(new StringToIntegerConverter("Must be a number"))
              .bind(Product::getQuantity, Product::setQuantity);

        // Price
        binder.forField(price).asRequired("Required")
              .withValidator(price -> !price.isBlank() && !price.isEmpty(), "Price is required field!")
              .withValidator(
                    price -> Double.parseDouble(price.replace(",", "")) >= 1.00F &&
                            Double.parseDouble(price.replace(",", "")) <= 100000.00F,
                    "Price must be between 1 and 100,000 \n with format: ##,###.##"
              )
              .withConverter(new StringToDoubleConverter("Must be a number"))
              .bind(Product::getPrice, Product::setPrice);

        // Supplier
        binder.forField(supplierComboBox).asRequired("Required")
              .withValidator(supplierComboBox -> supplierComboBox.getFirstname().concat(" ").concat(supplierComboBox.getLastname()) != null && !"".equals(supplierComboBox.getFirstname().concat(" ").concat(supplierComboBox.getLastname())), "Supplier is required field!")
              .bind(Product::getSupplier, Product::setSupplier);
    }

    /**
     * Setting up Component Events
     */
    public static abstract class NewProductFormEvent extends ComponentEvent<NewProductForm> {
        private final Product product;

        protected NewProductFormEvent(NewProductForm source, Product product) {
            super(source, false);
            this.product = product;
        }

        public Product getProduct() {
            return product;
        }
    }

    /**
     * Save Event
     */
    public static class SaveEvent extends NewProductForm.NewProductFormEvent {
        SaveEvent(NewProductForm source, Product product) {
            super(source, product);
        }
    }

    /**
     * Close Event
     */
    public class CloseEvent extends NewProductForm.NewProductFormEvent {
        CloseEvent(NewProductForm source) {
            super(source, null);
            dialog.close();
        }
    }

    /**
     * Register Event
     * @return event
     */
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

    private void addNewProduct(Product product) {
        if (product == null) {
            service.showErrorNotification("New product is invalid !");
        } else {
            product.setId(null);
            product.setFirstname(firstname.getValue());
            product.setLastname(lastname.getValue());
            product.setQuantity(Integer.valueOf(quantity.getValue()));
            product.setPrice(Double.valueOf(price.getValue()));
            product.setSupplier(supplierComboBox.getValue());
            this.setProduct(product);
            this.setVisible(true);
            addClassName("create");
        }
    }

    /**
     * Update Product grid
     */
    private void updateProductGrid() {
        grid.setItems(service.getAllProductsFromCache(filterText));
    }
}