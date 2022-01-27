package com.example.demo.views;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;

import com.example.demo.service.MainService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import com.vaadin.flow.shared.Registration;
import org.apache.commons.validator.routines.EmailValidator;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NewSupplierForm extends FormLayout {
    H2 headline = new H2("Supplier");

    TextField firstname = new TextField("First name");
    TextField lastname = new TextField("Last name");
    DatePicker dateOfBirth = new DatePicker("Birthdate");
    TextField phoneNumber = new TextField("Phone Number");
    TextField address = new TextField("Address");
    EmailField email = new EmailField("Email");

    Button save = new Button("Save");
    Button cancel = new Button("Cancel");

    private Supplier supplier;
    Binder<Supplier> binder = new BeanValidationBinder<>(Supplier.class);
    boolean isSavedSuccess = false;

    private MainService service;

    public NewSupplierForm(MainService service, List<Product> productsList, List<Supplier> suppliersList) {
        this.service = service;
        addClassName("new-supplier-form");
        setPlaceHolder();
        validateForm();
        binder.bindInstanceFields(this);
        this.addListener(NewSupplierForm.SaveEvent.class, this::saveNewSupplier);

        add(headline,
            firstname,
            lastname,
            dateOfBirth,
            phoneNumber,
            email,
            address,
            createButtonsLayout());
    }

    /**
     * Other methods and fields omitted
     */
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
        binder.readBean(supplier);
    }

    private void saveNewSupplier(SaveEvent saveEvent) {
        try {
            service.createSupplier(saveEvent.getSupplier());
            isSavedSuccess = true;
        } catch (Exception exception) {
            exception.printStackTrace();
            isSavedSuccess = false;
        }
        if (isSavedSuccess) {
            firstname.clear();
            lastname.clear();
            dateOfBirth.clear();
            email.clear();
            phoneNumber.clear();
            address.clear();
            showSuccessNotification("New supplier is created successfully!");
        } else {
            showErrorNotification("New supplier cannot saved successfully!");
        }
    }

    /**
     * Setting up Component Events
     */
    public static abstract class NewSupplierFormEvent extends ComponentEvent<NewSupplierForm> {
        private final Supplier supplier;

        protected NewSupplierFormEvent(NewSupplierForm source, Supplier supplier) {
            super(source, false);
            this.supplier = supplier;
        }

        public Supplier getSupplier() {
            return supplier;
        }
    }

    /**
     * Save Event
     */
    public static class SaveEvent extends NewSupplierFormEvent {
        SaveEvent(NewSupplierForm source, Supplier supplier) {
            super(source, supplier);
        }
    }

    /**
     * Close Event
     */
    public class CloseEvent extends NewSupplierFormEvent {
        CloseEvent(NewSupplierForm source) {
            super(source, null);
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

    private HorizontalLayout createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(event -> {
            validateAndSave();
            binder.validate();
        });
        save.addClickShortcut(Key.ENTER);

        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickShortcut(Key.ESCAPE);
        cancel.addClickListener(e -> fireEvent(new CloseEvent(this)));

        binder.addStatusChangeListener(event -> save.setEnabled(binder.isValid()));
        return new HorizontalLayout(save, cancel);
    }

    private void setPlaceHolder() {
        firstname.setPlaceholder("Enter firstname...");
        lastname.setPlaceholder("Enter lastname...");
        dateOfBirth.setPlaceholder("Select birthdate with format MM/DD/YYYY...");
        phoneNumber.setPlaceholder("Enter mobile phone...");
        address.setPlaceholder("Enter address...");
        email.setPlaceholder("Enter email...");
    }

    /**
     * Validate input form
     */
    private void validateForm() {
        // Firstname
        binder.forField(firstname).asRequired("Required")
                .withValidator(firstname -> !firstname.isBlank() && !firstname.isEmpty(), "Firstname is required field!")
                .withValidator(firstname -> firstname.length() >= 4, "Firstname must contain at least 4 characters")
                .bind(Supplier::getFirstname, Supplier::setFirstname);

        // Lastname
        binder.forField(lastname).asRequired("Required")
                .withValidator(lastname -> !lastname.isBlank() && !lastname.isEmpty(), "Lastname is required field!")
                .withValidator(lastname -> lastname.length() >= 2, "Lastname must contain at least 2 characters")
                .bind(Supplier::getLastname, Supplier::setLastname);

        // Address
        binder.forField(address).asRequired("Required")
                .withValidator(address -> !address.isBlank() && !address.isEmpty(), "Address is required field!")
                .bind(Supplier::getAddress, Supplier::setAddress);

        // Birthdate
        binder.forField(dateOfBirth).asRequired("Required")
                .withConverter(new LocalDateToDateConverter())
                .withValidator(
                        dateOfBirth -> !dateOfBirth.toString().isBlank() && !dateOfBirth.toString().isEmpty(),
                        "Birthdate is required field!"
                )
                .withValidator(
                        dateOfBirth -> {
                            LocalDate localDate = LocalDate.ofInstant(dateOfBirth.toInstant(),
                                    ZoneId.systemDefault());
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/dd/yyyy");
                            return localDate.format(formatter).length() == 9;
                        },
                        "Your chosen birthdate is invalid, please try again!"
                )
                .bind(Supplier::getDateOfBirth, Supplier::setDateOfBirth);

        // Phone number
        binder.forField(phoneNumber).asRequired("Required")
                .withValidator(phoneNumber -> !phoneNumber.isBlank() && !phoneNumber.isEmpty(), "Phone number is required field!")
                .bind(Supplier::getPhoneNumber, Supplier::setPhoneNumber);

        // Email
        binder.forField(email).asRequired("Required")
                .withValidator(email -> !email.isBlank() && !email.isEmpty(), "Email is required field!")
                .withValidator(email -> EmailValidator.getInstance().isValid(email), "Email must be valid")
                .bind(Supplier::getEmail, Supplier::setEmail);
    }

    private void validateAndSave() {
        try {
            if (binder.isValid()) {
                if (supplier == null) {
                    addNewSupplier(new Supplier());
                }
                binder.writeBean(supplier);
                fireEvent(new SaveEvent(this, supplier));
            }
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private void addNewSupplier(Supplier supplier) {
        if (supplier == null) {
            showErrorNotification("New supplier is invalid !");
        } else {
            supplier.setId(null);
            supplier.setFirstname(firstname.getValue());
            supplier.setLastname(lastname.getValue());
            supplier.setDateOfBirth(Date.valueOf(dateOfBirth.getValue()));
            supplier.setPhoneNumber(phoneNumber.getValue());
            supplier.setAddress(address.getValue());
            supplier.setEmail(email.getValue());
            this.setSupplier(supplier);
            this.setVisible(true);
            addClassName("create");
        }
    }

    private void showErrorNotification(String errMessage) {
        Notification notification = Notification.show(errMessage);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }

    private void showSuccessNotification(String successMessage) {
        Notification notification = Notification.show(successMessage);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }
}
