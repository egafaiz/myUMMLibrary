package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.library.Book;
import org.example.library.BorrowedBook;

import java.time.LocalDate;
import java.util.List;

public class PinjamDialogController {
    @FXML private TextField durationField;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private boolean confirmed = false;
    private Book book;
    private StudentController studentController;

    @FXML
    private void initialize() {
        confirmButton.setOnAction(event -> handleConfirm());
        cancelButton.setOnAction(event -> handleCancel());
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setStudentController(StudentController studentController) {
        this.studentController = studentController;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void handleConfirm() {
        if (isBookAlreadyBorrowed(book.getId())) {
            showError("Error", "Buku sudah terpinjam dan tidak bisa dipinjam lagi.");
            return;
        }

        if (studentController.getMahasiswa().getBorrowedBooks().size() >= 10) {
            showError("Error", "Anda sudah mencapai batas maksimal peminjaman 10 buku. Kembalikan buku terlebih dahulu.");
            return;
        }

        String durationText = durationField.getText();
        int duration;

        try {
            duration = Integer.parseInt(durationText);
            if (duration > 7) {
                showError("Error", "Durasi tidak boleh lebih dari 7 hari.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Error", "Masukkan angka yang valid untuk durasi.");
            return;
        }

        LocalDate borrowedDate = LocalDate.now();
        BorrowedBook borrowedBook = new BorrowedBook();
        borrowedBook.setId(book.getId());
        borrowedBook.setBorrowedDate(borrowedDate);
        borrowedBook.setDuration(duration);

        studentController.getMahasiswa().getBorrowedBooks().add(borrowedBook);
        studentController.updateBookStock(book.getId(), -1);
        studentController.incrementTotalBorrowedBooks();

        studentController.saveBorrowedBooks(studentController.getMahasiswa().getBorrowedBooks());

        confirmed = true;
        closeDialog();
    }

    @FXML
    private void handleCancel() {
        confirmed = false;
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }

    private boolean isBookAlreadyBorrowed(int bookId) {
        List<BorrowedBook> borrowedBooks = studentController.getMahasiswa().getBorrowedBooks();
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
