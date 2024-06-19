package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class StudentController {
    @FXML private Button borrowBookButton;
    @FXML private Button returnBookButton;
    @FXML private Button extendBookButton;
    @FXML private Button logoutButton;

    @FXML
    private void handleBorrowBook() {
        loadScene("student_borrow_book.fxml");
    }

    @FXML
    private void handleReturnBook() {
        loadScene("student_return_book.fxml");
    }

    @FXML
    private void handleExtendBook() {
        loadScene("student_extend_book.fxml");
    }

    @FXML
    private void handleLogout() {
        loadScene("login.fxml");
    }

    private void loadScene(String fxml) {
        Stage stage = (Stage) borrowBookButton.getScene().getWindow();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/library/views/" + fxml));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
