package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class StudentReturnBookController {
    @FXML private TextField bookIdField;
    @FXML private Button returnButton;

    @FXML
    private void handleReturnBook() {
        String bookId = bookIdField.getText();
        // Implement logic to return the book
    }
}
