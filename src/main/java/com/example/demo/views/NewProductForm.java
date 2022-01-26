package com.example.demo.views;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;

import java.util.List;

public class NewProductForm extends FormLayout {
    // Other fields omitted
    Binder<Supplier> binder = new BeanValidationBinder<>(Supplier.class);

    H2 headline = new H2("Product");

    TextField firstname = new TextField("First name");
    TextField lastName = new TextField("Last name");
    TextField quantity = new TextField("Quantity");
    TextField price = new TextField("Price");

    Button save = new Button("Save");
    Button close = new Button("Cancel");

    ComboBox<Supplier> supplierComboBox = new ComboBox<>("Supplier");

    public NewProductForm(List<Product> productsList, List<Supplier> suppliersList) {
        addClassName("new-product-form");
        binder.bindInstanceFields(this);

        supplierComboBox.setItems(suppliersList);
        supplierComboBox.setAllowCustomValue(true);
        supplierComboBox.setItemLabelGenerator(Supplier::getName);

        add(headline,
            firstname,
            lastName,
            quantity,
            price,
            supplierComboBox,
            createButtonsLayout());
    }

    private Supplier supplier;

    // other methods and fields omitted
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        binder.readBean(supplier);
    }

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        return new HorizontalLayout(save, close);
    }
}
