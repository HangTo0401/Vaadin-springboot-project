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

import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.BeanValidationBinder;

import com.vaadin.flow.data.converter.StringToDoubleConverter;
import com.vaadin.flow.data.converter.StringToIntegerConverter;

import com.vaadin.flow.shared.Registration;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class ProductDetailForm extends FormLayout implements Serializable {

    private MainService service;

    private CacheService cacheService;

    private Dialog dialog;

    private Grid<Product> grid;

    private Product product;

    private boolean isUpdatedSuccess = false;

    // Creates a new binder that uses reflection based on the provided bean type to resolve bean properties.
    Binder<Product> binder = new BeanValidationBinder<>(Product.class);

    H2 headline = new H2("Product Detail Form");

    TextField firstname = new TextField("Firstname");
    TextField lastname = new TextField("Lastname");
    TextField quantity = new TextField("Quantity");
    TextField price = new TextField("Price");

    ComboBox<Supplier> supplierComboBox = new ComboBox<>("Supplier");

    Button save = new Button("Save");
    Button cancel = new Button("Cancel");

    public ProductDetailForm(Dialog dialog,
                             Grid<Product> grid,
                             MainService service,
                             CacheService cacheService) {
        this.dialog = dialog;
        this.grid = grid;
        this.service = service;
        this.cacheService = cacheService;

        addClassName("product-detail-form");
        setPlaceHolder();
        createSupplierComboBox();
        validateForm();
        binder.bindInstanceFields(this);
        this.addListener(ProductDetailForm.SaveEvent.class, this::updateProduct);

        add(headline,
            firstname,
            lastname,
            quantity,
            price,
            supplierComboBox,
            createButtonsLayout());
    }

    /**
     * Reading product detail
     */
    public void setProduct(Product product) {
        this.product = product;
        if (product != null && product.getId() != null) {
            supplierComboBox.setValue(product.getSupplier());
            binder.readBean(product);
        }
    }

    /**
     * Update product detail
     */
    private void updateProduct(ProductDetailForm.SaveEvent saveEvent) {
        String message = "";

        try {
            Product updateProduct = service.updateProduct(saveEvent.getProduct());
            message = updateProduct != null ? cacheService.reloadProductCache("UPDATE", updateProduct) : "";
            isUpdatedSuccess = true;
        } catch (Exception exception) {
            exception.printStackTrace();
            isUpdatedSuccess = false;
        }
        if (isUpdatedSuccess) {
            firstname.clear();
            lastname.clear();
            quantity.clear();
            price.clear();
            supplierComboBox.clear();

            if (!message.equals("")) {
                service.showSuccessNotification(message);
            } else {
                service.showErrorNotification("Exist product cannot be updated successfully!");
            }

            updateProductGrid();
            fireEvent(new ProductDetailForm.CloseEvent(this));
        } else {
            service.showErrorNotification("Product cannot be updated!");
        }
    }

    private void setPlaceHolder() {
        firstname.setPlaceholder("Enter firstname...");
        lastname.setPlaceholder("Enter lastname...");
        quantity.setPlaceholder("Enter quantity...");
        price.setPlaceholder("Enter price...");
        supplierComboBox.setPlaceholder("Select supplier");
    }

    private void createSupplierComboBox() {
        supplierComboBox.setAllowCustomValue(true);
        ComboBox.ItemFilter<Supplier> filter = (supplier, filterString) -> supplier.getName().toLowerCase().startsWith(filterString.toLowerCase());
        supplierComboBox.setItems(filter, this.service.getAllSuppliersFromCache());
        supplierComboBox.setItemLabelGenerator(Supplier::getName);
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
        cancel.addClickListener(e -> fireEvent(new ProductDetailForm.CloseEvent(this)));

        binder.addStatusChangeListener(event -> {
            if (binder.hasChanges()) {
                save.setEnabled(true);
            } else {
                save.setEnabled(false);
            }
        });

        return new HorizontalLayout(save, cancel);
    }

    private void validateAndSave() {
        try {
            if (binder.isValid()) {
                binder.writeBean(product);
                fireEvent(new ProductDetailForm.SaveEvent(this, product));
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
                                Double.parseDouble(price.replace(",", "")) <= 10000.00F,
                        "Price must be between 1 and 10,000 \n with correction format: ##,###.##"
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
    public static abstract class ProductDetailFormEvent extends ComponentEvent<ProductDetailForm> {
        private final Product product;

        protected ProductDetailFormEvent(ProductDetailForm source, Product product) {
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
    public static class SaveEvent extends ProductDetailForm.ProductDetailFormEvent {
        SaveEvent(ProductDetailForm source, Product product) {
            super(source, product);
        }
    }

    /**
     * Delete Event
     */
    public static class DeleteEvent extends ProductDetailForm.ProductDetailFormEvent {
        DeleteEvent(ProductDetailForm source, Product product) {
            super(source, product);
        }
    }

    /**
     * Close Event
     */
    public class CloseEvent extends ProductDetailForm.ProductDetailFormEvent {
        CloseEvent(ProductDetailForm source) {
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

    /**
     * Update Product grid
     */
    private void updateProductGrid() {
        grid.setItems(service.getAllProductsFromCache());
    }
}
