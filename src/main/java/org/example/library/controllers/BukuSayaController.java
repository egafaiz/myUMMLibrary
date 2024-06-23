package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class BukuSayaController {
    @FXML private Button homeButton;
    @FXML private Button searchButton;
    @FXML private Button myShelfButton;
    @FXML private VBox profileMenu;

    @FXML
    private void initialize() {
        setActiveButton(myShelfButton); // Set My Shelf button as active initially
    }

    @FXML
    private void handleLogout() {
        loadScene("login.fxml");
    }

    @FXML
    private void handleHomeClick() {
        setActiveButton(homeButton);
        loadScene("student.fxml");
    }

    @FXML
    private void handleSearchClick() {
        setActiveButton(searchButton);
        loadScene("cari_buku.fxml");
    }

    @FXML
    private void handleMyShelfClick() {
        setActiveButton(myShelfButton);
        // This is the current scene, no need to change
    }

    @FXML
    private void handleProfileMenu() {
        boolean isVisible = profileMenu.isVisible();
        profileMenu.setVisible(!isVisible);
        profileMenu.setManaged(!isVisible);
    }

    private void loadScene(String fxml) {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/library/views/" + fxml));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button activeButton) {
        homeButton.getStyleClass().remove("sidebar-button-active");
        searchButton.getStyleClass().remove("sidebar-button-active");
        myShelfButton.getStyleClass().remove("sidebar-button-active");

        activeButton.getStyleClass().add("sidebar-button-active");
    }
}