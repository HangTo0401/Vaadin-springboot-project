package com.example.demo.views;

import com.example.demo.entity.Product;
import com.example.demo.entity.Supplier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class ConfirmationDialog extends Dialog {

    private Label title;
    private Label question;
    private Button confirm;

    private MainView mainView;

    private Supplier supplier;
    private Product product;


    public ConfirmationDialog(MainView mainView, Supplier supplier, Product product, String title, String content) {
        this.mainView = mainView;
        this.supplier = supplier;
        this.product = product;

        open();
        createHeader();
        createContent();
        createFooter();

        setTitle(title);
        setQuestion(content);
        setWidth("500px");
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setQuestion(String question) {
        this.question.setText(question);
    }

    private void createHeader() {
        this.title = new Label();
        this.title.getElement().getStyle().set("fontWeight", "bold");
        this.title.getElement().getStyle().set("color", "red");
        this.title.getElement().getStyle().set("fontSize", "medium");

        HorizontalLayout header = new HorizontalLayout();
        header.add(this.title);
        header.setFlexGrow(1, this.title);
        header.setHeight("50px");
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        add(header);
    }

    private void createContent() {
        question = new Label();

        VerticalLayout content = new VerticalLayout();
        content.add(question);
        content.setPadding(false);
        add(content);
    }

    private void createFooter() {
        confirm = new Button("Confirm");
        confirm.addClickListener(buttonClickEvent -> {
            if (supplier != null) {
                mainView.deleteSupplier(supplier);
                close();
            } else if (product != null) {
                mainView.deleteProduct(product);
                close();
            }
        });

        Button cancel = new Button("Cancel");
        cancel.addClickListener(buttonClickEvent -> close());

        HorizontalLayout footer = new HorizontalLayout();
        footer.add(confirm, cancel);
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        add(footer);
    }

}
