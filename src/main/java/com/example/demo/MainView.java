package com.example.demo;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;
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
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

// implements Table.ColumnGenerator
@Route("")
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

    private Dialog supplierDialog;
    private Dialog productDialog;

    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;


    public MainView(SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        setupLayout(DEFAULT_LAYOUT);
        add(mainLayout);
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
        supplierDialog.getElement().setAttribute("aria-label", title);

        VerticalLayout supplierDialogLayout = createDialogLayout(supplierDialog, btnName);
        supplierDialog.add(supplierDialogLayout);

        Button button = new Button(title, e -> supplierDialog.open());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(supplierDialog, button);
        supplierActionButtonsLayout.add(button);
    }

    private void createNewProductButton(String btnName, String title) {
        productDialog.getElement().setAttribute("aria-label", title);

        VerticalLayout supplierDialogLayout = createDialogLayout(productDialog, btnName);
        productDialog.add(supplierDialogLayout);

        Button button = new Button(title, e -> productDialog.open());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(productDialog, button);
        productActionButtonsLayout.add(button);
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

    private void createSupplierSearchArea() {
        TextField textField = new TextField();
        textField.setPlaceholder("Search");
        supplierActionButtonsLayout.add(textField);

        // New button
        createNewSupplierButton("Supplier", "New Supplier");
    }

    private void createProductSearchArea() {
        TextField textField = new TextField();
        textField.setPlaceholder("Search");
        productActionButtonsLayout.add(textField);

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

        supplierDialog = new Dialog();
        productDialog = new Dialog();
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
    }

    private void initSupplierGrid() {
        supplierGrid = new Grid<>(Supplier.class, false);
        // Supplier employee = new Supplier(1, "Cuong phan", Date.valueOf("1984-05-10"), "cuong.phan@axonactive.com", "0906678806", "Tân Bình HCM");
        List<Supplier> listSuppliers = new ArrayList<>();

        if (StringUtils.isEmpty("")) {
            listSuppliers = supplierRepository.findAll();
        }
        else {
            listSuppliers = supplierRepository.findByNameStartsWithIgnoreCase("");
        }

        supplierGrid.addColumn(Supplier::getName).setHeader(new Html("<b>Name</b>"));
        supplierGrid.addColumn(Supplier::getDateOfBirth).setHeader(new Html("<b>Birthdate</b>"));
        supplierGrid.addColumn(Supplier::getEmail).setHeader(new Html("<b>Email</b>"));
        supplierGrid.addColumn(Supplier::getPhoneNumber).setHeader(new Html("<b>Phone Number</b>"));
        supplierGrid.addColumn(Supplier::getAddress).setHeader(new Html("<b>Address</b>"));

        supplierGrid.addColumn(new ComponentRenderer<>(person -> {
            // Button for editing person to backend
            Button editBtn = new Button("Edit", event -> {
                supplierDialog.open();
                supplierGrid.getDataProvider().refreshItem(person);
            });
            editBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

            // Button for removing person
            Button removeBtn = new Button("Delete", event -> {
                ListDataProvider<Supplier> dataProvider = (ListDataProvider<Supplier>) supplierGrid.getDataProvider();
                dataProvider.getItems().remove(person);
                dataProvider.refreshAll();
            });
            removeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

            // Layouts for placing the buttons
            HorizontalLayout buttons = new HorizontalLayout(editBtn, removeBtn);
            return new VerticalLayout(buttons);
        })).setHeader(new Html("<b>Actions</b>"));
//        supplierGrid.setItems(employee);
        supplierGrid.setItems(listSuppliers);
        supplierVerticalLayout.add(supplierGrid);
    }

    private void initProductGrid() {
        productGrid = new Grid<>(Product.class, false);
        Product product = new Product(2, "Product", 10, 10000.23, "Cuong Phan");

        productGrid.addColumn(Product::getProductName).setHeader(new Html("<b>Product Name</b>"));
        productGrid.addColumn(Product::getQuantity).setHeader(new Html("<b>Quantity</b>"));
        productGrid.addColumn(Product::getPrice).setHeader(new Html("<b>Price</b>"));
        productGrid.addColumn(Product::getSupplierName).setHeader(new Html("<b>Supplier Name</b>"));

        productGrid.addColumn(new ComponentRenderer<>(item -> {
            // Button for editing person to backend
            Button editBtn = new Button("Edit", event -> {
                productDialog.open();
                productGrid.getDataProvider().refreshItem(item);
            });
            editBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);

            // Button for removing person
            Button removeBtn = new Button("Delete", event -> {
                ListDataProvider<Product> dataProvider = (ListDataProvider<Product>) productGrid.getDataProvider();
                dataProvider.getItems().remove(item);
                dataProvider.refreshAll();
            });
            removeBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);

            // Layouts for placing the buttons
            HorizontalLayout buttons = new HorizontalLayout(editBtn, removeBtn);
            return new VerticalLayout(buttons);
        })).setHeader(new Html("<b>Actions</b>"));
        productGrid.setItems(product);
        productVerticalLayout.add(productGrid);
    }

    private void getListSuppliers(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            supplierGrid.setItems(supplierRepository.findAll());
        }
        else {
            supplierGrid.setItems(supplierRepository.findByNameStartsWithIgnoreCase(filterText));
        }
        supplierVerticalLayout.add(supplierGrid);
    }
}
