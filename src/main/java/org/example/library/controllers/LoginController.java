package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private Button loginButton;
    @FXML private ImageView togglePasswordVisibility;

    private boolean isPasswordVisible = false;
    private Map<String, String> studentData;

    @FXML
    private void initialize() {
        // Example student data
        studentData = new HashMap<>();
        studentData.put("student1", "studentPassword1");
        studentData.put("student2", "studentPassword2");

        // Prepare text field to display password as plain text
        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);
        passwordTextField.managedProperty().bind(passwordTextField.visibleProperty());
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());

        // Set the initial icon for the password visibility toggle
        updateTogglePasswordVisibilityIcon();

        togglePasswordVisibility.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordTextField.setVisible(false);
            passwordField.setVisible(true);
        } else {
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
        }
        isPasswordVisible = !isPasswordVisible;
        updateTogglePasswordVisibilityIcon();
    }

    private void updateTogglePasswordVisibilityIcon() {
        if (isPasswordVisible) {
            togglePasswordVisibility.setImage(new Image(getClass().getResourceAsStream("/org/example/library/mdi_eye.png")));
        } else {
            togglePasswordVisibility.setImage(new Image(getClass().getResourceAsStream("/org/example/library/off.png")));
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Implement login logic
        if ("admin".equals(username) && "admin".equals(password)) {
            loadScene("admin.fxml");
        } else if (studentData.containsKey(username) && studentData.get(username).equals(password)) {
            loadScene("student.fxml");
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    private void loadScene(String fxml) {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/" + fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the scene.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        passwordTextField.setText("");
        passwordTextField.setVisible(false);
        passwordField.setVisible(true);
        isPasswordVisible = false;
        updateTogglePasswordVisibilityIcon();
    }
}
