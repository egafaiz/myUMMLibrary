package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class StudentExtendBookController {
    @FXML private TextField bookIdField;
    @FXML private TextField durationField;
    @FXML private Button extendButton;

    @FXML
    private void handleExtendBook() {
        String bookId = bookIdField.getText();
        String duration = durationField.getText();
        // Implement logic to extend the book
    }
}
