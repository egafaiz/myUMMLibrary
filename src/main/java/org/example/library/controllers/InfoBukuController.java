package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.library.Book;

import java.io.File;
import java.io.IOException;

public class InfoBukuController {
    @FXML private Label bookTitleLabel;
    @FXML private Label bookAuthorLabel;
    @FXML private Label bookCategoryLabel;
    @FXML private Label bookYearLabel;
    @FXML private Label bookStockLabel;
    @FXML private ImageView bookImageView;
    @FXML private VBox profileMenu;
    @FXML private Label loginCountLabel;

    private Book book;
    private LoginController.Mahasiswa mahasiswa;
    private int loginCount;
    private StudentController studentController;

    @FXML
    private void initialize() {
        // Initialize if needed
    }

    public void setBook(Book book) {
        this.book = book;
        updateUI();
    }

    public void setMahasiswa(LoginController.Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
        updateUI();
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
        updateUI();
    }

    public void setStudentController(StudentController studentController) {
        this.studentController = studentController;
    }

    private void updateUI() {
        if (book != null) {
            bookTitleLabel.setText(book.getJudul());
            bookAuthorLabel.setText(book.getPenulis());
            bookCategoryLabel.setText(book.getKategori());
            bookYearLabel.setText(String.valueOf(book.getTahun()));
            bookStockLabel.setText("Stok: " + book.getStok());
            bookImageView.setImage(new Image(new File(book.getFoto()).toURI().toString()));
        }
        if (loginCountLabel != null) {
            loginCountLabel.setText("Login Count: " + loginCount);
        }
    }

    @FXML
    private void handleHomeClick() {
        loadSceneWithMahasiswa("student.fxml", StudentController.class);
    }

    @FXML
    private void handleSearchClick() {
        loadSceneWithMahasiswa("cari_buku.fxml", CariBukuController.class);
    }

    @FXML
    private void handleMyShelfClick() {
        loadSceneWithMahasiswa("buku_saya.fxml", BukuSayaController.class);
    }

    @FXML
    private void handlePinjamClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/pinjam_dialog.fxml"));
            Parent root = loader.load();

            PinjamDialogController controller = loader.getController();
            controller.setBook(book);
            controller.setStudentController(studentController);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Pinjam Buku");
            dialogStage.setScene(new Scene(root));
            dialogStage.showAndWait();

            if (controller.isConfirmed()) {
                studentController.updateUI();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileMenu() {
        boolean isVisible = profileMenu.isVisible();
        profileMenu.setVisible(!isVisible);
        profileMenu.setManaged(!isVisible);
    }

    @FXML
    private void handleLogout() {
        loadScene("login.fxml");
    }

    private void loadScene(String fxml) {
        Stage stage = (Stage) bookTitleLabel.getScene().getWindow();
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
                    ((StudentController) controller).setLoginCount(loginCount);
                } else if (controller instanceof CariBukuController) {
                    ((CariBukuController) controller).setMahasiswa(mahasiswa);
                    ((CariBukuController) controller).setLoginCount(loginCount);
                } else if (controller instanceof BukuSayaController) {
                    ((BukuSayaController) controller).setMahasiswa(mahasiswa);
                    ((BukuSayaController) controller).setLoginCount(loginCount);
                }
            }

            Stage stage = (Stage) bookTitleLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
