package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.library.BorrowedBook;

import java.io.IOException;
import java.util.List;

public class BukuSayaController {
    @FXML private Button homeButton;
    @FXML private Button searchButton;
    @FXML private Button myShelfButton;
    @FXML private VBox profileMenu;
    @FXML private VBox borrowedBooksContainer;

    private LoginController.Mahasiswa mahasiswa;
    private int loginCount;

    @FXML
    private void initialize() {
        setActiveButton(myShelfButton); // Set My Shelf button as active initially
        displayBorrowedBooks(); // Display borrowed books on initialization
    }

    public void setMahasiswa(LoginController.Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
        displayBorrowedBooks();
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    @FXML
    private void handleLogout() {
        loadScene("login.fxml");
    }

    @FXML
    private void handleHomeClick() {
        setActiveButton(homeButton);
        loadSceneWithMahasiswa("student.fxml", StudentController.class);
    }

    @FXML
    private void handleSearchClick() {
        setActiveButton(searchButton);
        loadSceneWithMahasiswa("cari_buku.fxml", CariBukuController.class);
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

    private <T> void loadSceneWithMahasiswa(String fxml, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/" + fxml));
            Parent root = loader.load();

            T controller = loader.getController();
            if (controllerClass.isInstance(controller)) {
                if (controller instanceof StudentController) {
                    ((StudentController) controller).setMahasiswa(mahasiswa);
                    ((StudentController) controller).setLoginCount(loginCount); // Pass loginCount
                } else if (controller instanceof CariBukuController) {
                    ((CariBukuController) controller).setMahasiswa(mahasiswa);
                } else if (controller instanceof BukuSayaController) {
                    ((BukuSayaController) controller).setMahasiswa(mahasiswa);
                    ((BukuSayaController) controller).setLoginCount(loginCount); // Pass loginCount
                }
            }

            Stage stage = (Stage) homeButton.getScene().getWindow();
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

    private void displayBorrowedBooks() {
        if (borrowedBooksContainer != null && mahasiswa != null) {
            borrowedBooksContainer.getChildren().clear();
            List<BorrowedBook> borrowedBooks = mahasiswa.getBorrowedBooks();
            for (BorrowedBook borrowedBook : borrowedBooks) {
                // Create UI elements for each borrowed book and add them to the container
                // Here you can create Labels, ImageViews, etc., and set their properties accordingly
                Label bookLabel = new Label("Book ID: " + borrowedBook.getId());
                bookLabel.getStyleClass().add("borrowed-book-label");
                borrowedBooksContainer.getChildren().add(bookLabel);
            }
        }
    }
}
