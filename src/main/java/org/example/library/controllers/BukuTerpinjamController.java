package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.library.Book;
import org.example.library.BorrowedBook;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BukuTerpinjamController {
    @FXML private ImageView bookImage;
    @FXML private Label bookTitle;
    @FXML private Label bookAuthor;
    @FXML private Label bookCategory;
    @FXML private Label bookYear;
    @FXML private Label bookStock;
    @FXML private Label borrowDateLabel;
    @FXML private Label returnDateLabel;
    @FXML private Label fineLabel;
    @FXML private Button homeButton;
    @FXML private Button searchButton;
    @FXML private Button myShelfButton;
    @FXML private Button profileButton;
    @FXML private VBox profileMenu;
    @FXML private Label loginCountLabel;

    private Book book;
    private BorrowedBook borrowedBook;
    private Map<String, Object> mahasiswa;
    private int loginCount;
    private StudentController studentController;

    public void setBook(Book book) {
        this.book = book;
        updateUI();
    }

    public void setBorrowedBook(BorrowedBook borrowedBook) {
        this.borrowedBook = borrowedBook;
        updateUI();
    }

    public void setMahasiswa(Map<String, Object> mahasiswa) {
        this.mahasiswa = mahasiswa;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
        updateLoginCountUI();
    }

    public void setStudentController(StudentController studentController) {
        this.studentController = studentController;
    }

    private void updateUI() {
        if (book != null) {
            bookImage.setImage(new Image(new File(book.getFoto()).toURI().toString()));
            bookTitle.setText(book.getJudul());
            bookAuthor.setText(book.getPenulis());
            bookCategory.setText(book.getKategori());
            bookYear.setText(book.getTahun());
            bookStock.setText(String.valueOf(book.getStok()));
        }

        if (borrowedBook != null) {
            borrowDateLabel.setText(borrowedBook.getBorrowedDate().toString());
            returnDateLabel.setText(borrowedBook.getReturnDate().toString());
            fineLabel.setText("Rp " + borrowedBook.calculateFine());
        }
    }

    private void updateLoginCountUI() {
        if (loginCountLabel != null) {
            loginCountLabel.setText(String.valueOf(loginCount));
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
    private void handleProfileMenu() {
        boolean isVisible = profileMenu.isVisible();
        profileMenu.setVisible(!isVisible);
        profileMenu.setManaged(!isVisible);
    }

    @FXML
    private void handlePerpanjangClick() {
        if (borrowedBook != null) {
            if (borrowedBook.getExtensionCount() >= 2) {
                showAlert("Perpanjang Buku", "Peminjaman buku ini sudah diperpanjang maksimal 2 kali.");
                return;
            }

            if (!borrowedBook.getReturnDate().minusDays(1).isEqual(LocalDate.now())) {
                showAlert("Perpanjang Buku", "Perpanjangan hanya dapat dilakukan ketika durasi peminjaman tersisa 1 hari.");
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Perpanjang Durasi Peminjaman");
            alert.setHeaderText("Masukkan jumlah hari perpanjangan (maksimal 7 hari)");
            alert.setContentText("Hari:");

            TextField input = new TextField();
            input.setPromptText("Jumlah hari");

            alert.getDialogPane().setContent(input);
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    int days = Integer.parseInt(input.getText());
                    if (days <= 0 || days > 7) {
                        showAlert("Error", "Jumlah hari perpanjangan tidak valid.");
                    } else {
                        borrowedBook.setDuration(borrowedBook.getDuration() + days);
                        borrowedBook.incrementExtensionCount();
                        updateUI();
                        studentController.sendEmailNotification("Extended book borrowing", "You have extended the borrowing period for the book: " + book.getJudul() + " by " + days + " days.");
                        showAlert("Perpanjang Buku", "Buku berhasil diperpanjang selama " + days + " hari.");
                    }
                } catch (NumberFormatException e) {
                    showAlert("Error", "Jumlah hari perpanjangan tidak valid.");
                }
            }
        } else {
            showAlert("Error", "BorrowedBook is null.");
        }
    }

    private <T> void loadSceneWithMahasiswa(String fxml, Class<T> controllerClass) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/" + fxml));
            Parent root = loader.load();

            T controller = loader.getController();
            if (controllerClass.isInstance(controller)) {
                if (controller instanceof BukuTerpinjamController) {
                    ((BukuTerpinjamController) controller).setStudentController(this.studentController);
                }
                if (controller instanceof BukuSayaController) {
                    ((BukuSayaController) controller).setMahasiswa(this.mahasiswa);
                    ((BukuSayaController) controller).setLoginCount(this.loginCount);
                    ((BukuSayaController) controller).setStudentController(this.studentController);
                }
                if (controller instanceof StudentController) {
                    ((StudentController) controller).setMahasiswa(this.mahasiswa);
                    ((StudentController) controller).setLoginCount(this.loginCount);
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

    @FXML
    private void handleKembalikanClick() {
        try {
            if (borrowedBook != null && mahasiswa != null) {
                List<BorrowedBook> borrowedBooks = (List<BorrowedBook>) mahasiswa.get("borrowedBooks");

                if (!borrowedBooks.remove(borrowedBook)) {
                    showAlert("Error", "Failed to remove the borrowed book.");
                    return;
                }

                if (studentController != null) {
                    studentController.saveBorrowedBooks(borrowedBooks);
                    studentController.decrementTotalBorrowedBooks();
                    studentController.updateBookStock(book.getId(), 1);
                    studentController.updateUI();
                    studentController.sendEmailNotification("Returned a book", "You have returned the book: " + book.getJudul());

                    showAlert("Kembalikan Buku", "Buku berhasil dikembalikan.");

                    loadSceneWithMahasiswa("buku_saya.fxml", BukuSayaController.class);
                } else {
                    showAlert("Error", "StudentController is not set.");
                }
            } else {
                showAlert("Error", "BorrowedBook or Mahasiswa is null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        loadScene("login.fxml");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(bookImage.getScene().getWindow());
        alert.showAndWait();
    }
}
