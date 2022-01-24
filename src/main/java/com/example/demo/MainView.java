package com.example.demo;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.router.Route;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

// implements Table.ColumnGenerator
@Route("")
public class MainView extends VerticalLayout {
    private String[] buttonsName = new String[] {"Supplier", "Product"};
    private HorizontalLayout buttonsLayout;
    private HorizontalLayout actionButtonsLayout;
    private VerticalLayout verticalLayout;
    private Grid<Supplier> supplierGrid;
    private Grid<Product> productGrid;
    private Dialog dialog;
    private Dialog productDialog;
    private String DEFAULT_LAYOUT = "Supplier";

    public MainView() {
        setupLayout(DEFAULT_LAYOUT);
        add(verticalLayout);
    }

    private void createButtons(String[] btnsName) {
        createTopButton(btnsName[0]);
        createTopButton(btnsName[1]);
        verticalLayout.add(buttonsLayout);
    }

    private void createTopButton(String btnName) {
        Button button = new Button(btnName);
        button.addClickListener(click -> {
            changeLayout(btnName);
        });
        button.addClickShortcut(Key.ENTER);
        buttonsLayout.add(button);
    }

    private void createNewButton(String btnName, String title) {
        dialog.getElement().setAttribute("aria-label", title);

        if (btnName.equals("Supplier")) {
            VerticalLayout supplierDialogLayout = createDialogLayout(dialog, btnName);
            dialog.add(supplierDialogLayout);
        } else {
            VerticalLayout productDialogLayout = createDialogLayout(dialog, btnName);
            dialog.add(productDialogLayout);
        }

        Button button = new Button(title, e -> dialog.open());
        add(dialog, button);
        actionButtonsLayout.add(button);
    }

    private static VerticalLayout createDialogLayout(Dialog dialog, String title) {
        H2 headline = new H2(title);
        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold");

        TextField firstNameField = new TextField("First name");
        TextField lastNameField = new TextField("Last name");
        VerticalLayout fieldLayout = new VerticalLayout(firstNameField, lastNameField);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Save", e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(headline, fieldLayout, buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "300px").set("max-width", "100%");

        return dialogLayout;
    }

    private void createSearchArea(String layoutName) {
        TextField textField = new TextField();
        textField.setPlaceholder("Search");
        actionButtonsLayout.add(textField);

        // New button
        createNewButton(layoutName, "New " + layoutName);
        verticalLayout.add(actionButtonsLayout);
    }

    private void setupLayout(String layoutName) {
        buttonsLayout = new HorizontalLayout();
        actionButtonsLayout = new HorizontalLayout();
        verticalLayout = new VerticalLayout();
        dialog = new Dialog();
        setLayoutContent(layoutName);
    }

    private void setLayoutContent(String layoutName) {
        // Create buttons
        createButtons(buttonsName);

        // Create search area
        createSearchArea(layoutName);

        // Init grid
        initGrid();
    }

    private void initGrid() {
        supplierGrid = new Grid<>(Supplier.class, false);
        productGrid = new Grid<>(Product.class, false);
        if (DEFAULT_LAYOUT.equals("Supplier")) {
            Supplier employee = new Supplier(1L, "Cuong phan", Date.valueOf("1984-05-10"), "cuong.phan@axonactive.com", "0906678806", "Tân Bình HCM");
            //        List<Employee> employees = Arrays.asList(employee);

            supplierGrid.addColumn(Supplier::getName).setHeader(new Html("<b>Name</b>"));
            supplierGrid.addColumn(Supplier::getDayOfBirth).setHeader(new Html("<b>Birthdate</b>"));
            supplierGrid.addColumn(Supplier::getEmail).setHeader(new Html("<b>Email</b>"));
            supplierGrid.addColumn(Supplier::getPhoneNumber).setHeader(new Html("<b>Phone Number</b>"));
            supplierGrid.addColumn(Supplier::getAddress).setHeader(new Html("<b>Address</b>"));

            supplierGrid.addColumn(new ComponentRenderer<>(person -> {
                // Button for editing person to backend
                Button editBtn = new Button("Edit", event -> {
                    dialog.open();
                    supplierGrid.getDataProvider().refreshItem(person);
                });

                // Button for removing person
                Button removeBtn = new Button("Delete", event -> {
                    ListDataProvider<Supplier> dataProvider = (ListDataProvider<Supplier>) supplierGrid.getDataProvider();
                    dataProvider.getItems().remove(person);
                    dataProvider.refreshAll();
                });

                // Layouts for placing the buttons
                HorizontalLayout buttons = new HorizontalLayout(editBtn, removeBtn);
                return new VerticalLayout(buttons);
            })).setHeader(new Html("<b>Actions</b>"));
            supplierGrid.setItems(employee);

            if (productGrid.isAttached()) {
                verticalLayout.replace(supplierGrid, productGrid);
            } else {
                verticalLayout.add(supplierGrid);
            }
        } else if (DEFAULT_LAYOUT.equals("Product")) {
            Product product = new Product(2L, "Product", 10, 10000.23, "Cuong Phan");

            productGrid.addColumn(Product::getProductName).setHeader(new Html("<b>Product Name</b>"));
            productGrid.addColumn(Product::getQuantity).setHeader(new Html("<b>Quantity</b>"));
            productGrid.addColumn(Product::getPrice).setHeader(new Html("<b>Price</b>"));
            productGrid.addColumn(Product::getSupplierName).setHeader(new Html("<b>Supplier Name</b>"));

            productGrid.addColumn(new ComponentRenderer<>(item -> {
                // Button for editing person to backend
                Button editBtn = new Button("Edit", event -> {
                    dialog.open();
                    productGrid.getDataProvider().refreshItem(item);
                });

                // Button for removing person
                Button removeBtn = new Button("Delete", event -> {
                    ListDataProvider<Product> dataProvider = (ListDataProvider<Product>) productGrid.getDataProvider();
                    dataProvider.getItems().remove(item);
                    dataProvider.refreshAll();
                });

                // Layouts for placing the buttons
                HorizontalLayout buttons = new HorizontalLayout(editBtn, removeBtn);
                return new VerticalLayout(buttons);
            })).setHeader(new Html("<b>Actions</b>"));
            productGrid.setItems(product);
            if (supplierGrid.isAttached()) {
                verticalLayout.replace(productGrid, supplierGrid);
            } else {
                verticalLayout.add(productGrid);
            }
        }
    }

    private void changeLayout(String layoutName) {
        if (layoutName.equals("Supplier")) {
            DEFAULT_LAYOUT = "Supplier";
        } else {
            DEFAULT_LAYOUT = "Product";
        }
        initGrid();
    }
}
