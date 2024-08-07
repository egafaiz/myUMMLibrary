package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.library.Book;
import org.example.library.BorrowedBook;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    private Map<String, Object> mahasiswa;
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

    public void setMahasiswa(Map<String, Object> mahasiswa) {
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
        if (isBookAlreadyBorrowed(book.getId())) {
            showError("Error", "Buku sudah terpinjam dan tidak bisa dipinjam lagi.");
            return;
        }

        List<BorrowedBook> borrowedBooks = (List<BorrowedBook>) mahasiswa.get("borrowedBooks");
        if (borrowedBooks.size() >= 10) {
            showError("Error", "Anda sudah mencapai batas maksimal peminjaman 10 buku. Kembalikan buku terlebih dahulu.");
            return;
        }

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
                studentController.sendEmailNotification("Borrowed a book", "You have borrowed a new book: " + book.getJudul());
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

    private boolean isBookAlreadyBorrowed(int bookId) {
        List<BorrowedBook> borrowedBooks = (List<BorrowedBook>) mahasiswa.get("borrowedBooks");
        for (BorrowedBook borrowedBook : borrowedBooks) {
            if (borrowedBook.getId() == bookId) {
                return true;
            }
        }
        return false;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
