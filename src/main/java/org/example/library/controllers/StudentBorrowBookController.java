package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class StudentBorrowBookController {
    @FXML private TextField bookIdField;
    @FXML private TextField durationField;
    @FXML private Button borrowButton;

    @FXML
    private void handleBorrowBook() {
        String bookId = bookIdField.getText();
        String duration = durationField.getText();
        // Implement logic to borrow the book
    }
}
