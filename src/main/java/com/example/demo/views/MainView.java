package com.example.demo.views;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.SupplierRepository;
import com.example.demo.service.MainService;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// implements Table.ColumnGenerator
@Route(value = "")
public class MainView extends VerticalLayout implements Serializable {
    private static final long serialVersionUID = 6529685098267757690L;

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

    private Dialog supplierDialog;
    private Dialog productDialog;

    private final MainService service;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    private List<Supplier> listSuppliers = new ArrayList<>();
    private List<Product> listProducts = new ArrayList<>();

    private NewSupplierForm supplierForm;
    private NewProductForm productForm;

    public MainView(MainService service, SupplierRepository supplierRepository, ProductRepository productRepository) {
        this.service = service;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
        configureNewSupplierForm();
        setupLayout(DEFAULT_LAYOUT);

        supplierForm.addListener(NewSupplierForm.SaveEvent.class, this::updateSupplier);
//        supplierForm.addListener(NewSupplierForm.DeleteEvent.class, this::deleteProduct);
        supplierForm.addListener(NewSupplierForm.CloseEvent.class, e -> closeEditor());
        add(mainLayout);
    }

    private void configureNewSupplierForm() {
        supplierForm = new NewSupplierForm(Collections.emptyList(), Collections.emptyList());
        supplierForm.setWidth("25em");

        productForm = new NewProductForm(Collections.emptyList(), Collections.emptyList());
        productForm.setWidth("25em");
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

//        VerticalLayout supplierDialogLayout = createSupplierDialogLayout(supplierDialog, btnName);
//        supplierDialog.add(supplierDialogLayout);

        supplierDialog.add(supplierForm);

        Button button = new Button(title, e -> supplierDialog.open());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(supplierDialog, button);
        supplierActionButtonsLayout.add(button);
    }

    private void createNewProductButton(String btnName, String title) {
        productDialog.getElement().setAttribute("aria-label", title);

//        VerticalLayout supplierDialogLayout = createProductDialogLayout(productDialog, btnName);
//        productDialog.add(supplierDialogLayout);

        productDialog.add(productForm);

        Button button = new Button(title, e -> productDialog.open());
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(productDialog, button);
        productActionButtonsLayout.add(button);
    }

    private VerticalLayout createSupplierDialogLayout(Dialog dialog, String title) {
        H2 headline = new H2(title);
        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold");

        TextField firstName = new TextField("First name");
        TextField lastName = new TextField("Last name");
        DatePicker dateOfBirth = new DatePicker("Birthdate");
        TextField phoneNumber = new TextField("Phone Number");
        TextField address = new TextField("Address");
        EmailField email = new EmailField("Email");

        VerticalLayout fieldLayout = new VerticalLayout(firstName, lastName, dateOfBirth, phoneNumber, email, address);
        fieldLayout.setSpacing(false);
        fieldLayout.setPadding(false);
        fieldLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        Button cancelButton = new Button("Cancel", e -> dialog.close());
        Button saveButton = new Button("Save", e -> {
//            try {
                // Shorthand for cases without extra configuration
                dialog.close();
//                supplierBinder.writeBean(supplier);
                // A real application would also save the updated person
                // using the application's backend
//            } catch (ValidationException ex) {
//                Notification.show("Person could not be saved, please check error messages for each field.");
//            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(headline, fieldLayout, buttonLayout);
        dialogLayout.setPadding(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "500px").set("max-width", "100%");

        return dialogLayout;
    }

    private VerticalLayout createProductDialogLayout(Dialog dialog, String title) {
        H2 headline = new H2(title);
        headline.getStyle().set("margin", "var(--lumo-space-m) 0 0 0")
                .set("font-size", "1.5em").set("font-weight", "bold");

        TextField firstNameField = new TextField("First name");
        TextField lastNameField = new TextField("Last name");
        TextField addressField = new TextField("Address");
        EmailField emailField = new EmailField("Email");

        ComboBox<Supplier> supplierComboBox = new ComboBox<>("Supplier");

        VerticalLayout fieldLayout = new VerticalLayout(firstNameField, lastNameField, emailField, addressField, supplierComboBox);
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
        supplierFilterText.setPlaceholder("Search");
        supplierFilterText.setClearButtonVisible(true);
        supplierFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        supplierFilterText.addValueChangeListener(e -> updateSupplierList());
        supplierActionButtonsLayout.add(supplierFilterText);

        // New button
        createNewSupplierButton("Supplier", "New Supplier");
    }

    private void updateSupplierList() {
        listSuppliers = service.getAllSuppliers(supplierFilterText.getValue());
        supplierGrid.setItems(listSuppliers);
    }

    private void updateProductList() {
        listProducts = service.getAllProducts(productFilterText.getValue());
        productGrid.setItems(listProducts);
    }

    private void createProductSearchArea() {
        productFilterText.setPlaceholder("Search");
        productFilterText.setClearButtonVisible(true);
        productFilterText.setValueChangeMode(ValueChangeMode.LAZY);
        productFilterText.addValueChangeListener(e -> updateProductList());
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

        listSuppliers = service.getAllSuppliers("");

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
        supplierGrid.setItems(listSuppliers);
        supplierVerticalLayout.add(supplierGrid);
    }

    private void initProductGrid() {
        productGrid = new Grid<>(Product.class, false);

        listProducts = service.getAllProducts("");

        productGrid.addColumn(Product::getProductName).setHeader(new Html("<b>Product Name</b>"));
        productGrid.addColumn(Product::getQuantity).setHeader(new Html("<b>Quantity</b>"));
        productGrid.addColumn(Product::getPrice).setHeader(new Html("<b>Price</b>"));
//        productGrid.addColumn(Product::getSupplierName).setHeader(new Html("<b>Supplier Name</b>"));

        productGrid.addColumn(new ComponentRenderer<>(item -> {
            Select<String> select = new Select<>();
            List<String> listName = supplierRepository.findAllSuppliersName();
            select.setItems(listName);
            select.setValue(listName.get(0));

            // Layouts for placing the buttons
            HorizontalLayout selectLayout = new HorizontalLayout(select);
            return new VerticalLayout(selectLayout);
        })).setHeader(new Html("<b>Supplier Name</b>"));

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
        productGrid.setItems(listProducts);
        productVerticalLayout.add(productGrid);
    }

    /**
     * Saving Product Detail Form
     *
     * @author tailam
     */
    private void updateSupplier(supplierForm.SaveEvent evt) {
        productService.updateProduct(evt.getProduct());
        productCacheService.updateProductCache(evt.getProduct());
        listDataProvider.refreshItem(evt.getProduct());
        closeEditor();
    }

    /**
     * Close Supplier Form
     */
    public void closeEditor() {
        supplierForm.setSupplier(null);
        supplierDialog.close();
//        removeClassName("editing");
//        supplierGrid.getDataProvider().refreshAll();
        supplierGrid.setVisible(true);
        mainLayout.setVisible(true);
    }
}