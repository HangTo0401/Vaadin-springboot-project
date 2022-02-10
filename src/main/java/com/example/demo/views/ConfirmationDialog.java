package com.example.demo.views;

import com.example.demo.entity.Supplier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;

public class ConfirmDialog extends Dialog {

        private Label title;
        private Label question;
        private Button confirm;

        public ConfirmationDialog() {
                createHeader();
                createContent();
                createFooter();
        }

}
