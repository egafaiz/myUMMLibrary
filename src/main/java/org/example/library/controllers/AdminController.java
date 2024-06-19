package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.util.Objects;

public class AdminController {
    @FXML private Button tambahBukuButton;
    @FXML private Button editBukuButton;
    @FXML private Button hapusBukuButton;
    @FXML private Button displayBukuButton;
    @FXML private Button tambahMahasiswaButton;
    @FXML private Button displayMahasiswaButton;
    @FXML private Button logoutButton;
    @FXML private VBox adminMenu;

    @FXML
    private void handleTambahBuku() {
        loadScene("tambah_buku.fxml");
    }

    @FXML
    private void handleEditBuku() {
        loadScene("edit_buku.fxml");
    }

    @FXML
    private void handleHapusBuku() {
        loadScene("hapus_buku.fxml");
    }

    @FXML
    private void handleDisplayBuku() {
        loadScene("display_buku.fxml");
    }

    @FXML
    private void handleTambahMahasiswa() {
        loadScene("tambah_mahasiswa.fxml");
    }

    @FXML
    private void handleDisplayMahasiswa() {
        loadScene("display_mahasiswa.fxml");
    }

    @FXML
    private void handleLogout() {
        loadLoginScene("login.fxml");
    }

    @FXML
    private void handleAdminMenu() {
        adminMenu.setVisible(!adminMenu.isVisible());
    }

    private void loadScene(String fxml) {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/library/views/" + fxml));
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLoginScene(String fxml) {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/" + fxml));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.resetFields();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/org/example/library/styles.css")).toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
