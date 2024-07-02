package org.example.library.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.library.Book;
import org.example.library.BorrowedBook;
import org.example.library.LocalDateAdapter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BukuSayaController {
    @FXML private Button homeButton;
    @FXML private Button searchButton;
    @FXML private Button myShelfButton;
    @FXML private VBox profileMenu;
    @FXML private HBox borrowedBooksContainer;
    @FXML private Label loginCountLabel;

    private Map<String, Object> mahasiswa;
    private int loginCount;
    private Gson gson;
    private StudentController studentController;

    @FXML
    private void initialize() {
        gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        setActiveButton(myShelfButton);
        displayBorrowedBooks();
    }

    public void setMahasiswa(Map<String, Object> mahasiswa) {
        this.mahasiswa = mahasiswa;
        displayBorrowedBooks();
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
        updateUI();
    }

    public void setStudentController(StudentController studentController) {
        this.studentController = studentController;
    }

    private void updateUI() {
        if (loginCountLabel != null) {
            loginCountLabel.setText(String.valueOf(loginCount));
        }
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
                    ((StudentController) controller).setLoginCount(loginCount);
                    ((StudentController) controller).updateUI();
                } else if (controller instanceof CariBukuController) {
                    ((CariBukuController) controller).setMahasiswa(mahasiswa);
                    ((CariBukuController) controller).setLoginCount(loginCount);
                } else if (controller instanceof BukuSayaController) {
                    ((BukuSayaController) controller).setMahasiswa(mahasiswa);
                    ((BukuSayaController) controller).setLoginCount(loginCount);
                    ((BukuSayaController) controller).setStudentController(studentController);
                } else if (controller instanceof BukuTerpinjamController) {
                    ((BukuTerpinjamController) controller).setMahasiswa(mahasiswa);
                    ((BukuTerpinjamController) controller).setLoginCount(loginCount);
                    ((BukuTerpinjamController) controller).setStudentController(studentController);
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
            List<BorrowedBook> borrowedBooks = (List<BorrowedBook>) mahasiswa.get("borrowedBooks");
            List<Book> allBooks = loadAllBooks();
            if (borrowedBooks != null && !borrowedBooks.isEmpty()) {
                for (BorrowedBook borrowedBook : borrowedBooks) {
                    Book bookDetails = findBookById(borrowedBook.getId(), allBooks);
                    if (bookDetails != null) {
                        addBookToView(bookDetails, borrowedBook);
                    }
                }
            } else {
                borrowedBooksContainer.getChildren().add(new Label("No borrowed books found."));
            }
        }
    }

    private List<BorrowedBook> loadBorrowedBooks() {
        String filePath = "src/main/resources/org/example/library/borrowed_books_" + mahasiswa.get("nim") + ".json";
        File file = new File(filePath);

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<BorrowedBook>>() {}.getType();
                return gson.fromJson(reader, listType);
            } catch (IOException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    private List<Book> loadAllBooks() {
        String filePath = "src/main/resources/org/example/library/books.json";
        File file = new File(filePath);

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Type listType = new TypeToken<List<Book>>() {}.getType();
                return gson.fromJson(reader, listType);
            } catch (IOException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    private Book findBookById(int id, List<Book> books) {
        for (Book book : books) {
            if (book.getId() == id) {
                return book;
            }
        }
        return null;
    }

    private void addBookToView(Book book, BorrowedBook borrowedBook) {
        VBox bookBox = createBookView(book);
        borrowedBooksContainer.getChildren().add(bookBox);

        bookBox.setOnMouseClicked(event -> handleBookClick(book, borrowedBook));
    }

    private VBox createBookView(Book book) {
        VBox bookBox = new VBox();
        bookBox.setSpacing(10);
        bookBox.getStyleClass().add("book-box");

        ImageView bookImage = new ImageView(new Image(new File(book.getFoto()).toURI().toString()));
        bookImage.setFitHeight(200);
        bookImage.setFitWidth(150);
        bookImage.setPreserveRatio(false);
        bookImage.getStyleClass().add("book-image");

        Label bookTitle = new Label(book.getJudul());
        bookTitle.getStyleClass().add("book-title");

        Label bookAuthor = new Label(book.getPenulis() + ", " + book.getTahun());
        bookAuthor.getStyleClass().add("book-author");

        Label bookStock = new Label("Stok: " + book.getStok());
        bookStock.getStyleClass().add("book-stock");

        bookBox.getChildren().addAll(bookImage, bookTitle, bookAuthor, bookStock);

        return bookBox;
    }

    private void handleBookClick(Book book, BorrowedBook borrowedBook) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/buku_terpinjam.fxml"));
            Parent root = loader.load();

            BukuTerpinjamController controller = loader.getController();
            controller.setBook(book);
            controller.setBorrowedBook(borrowedBook);
            controller.setMahasiswa(mahasiswa);
            controller.setLoginCount(loginCount);
            if (studentController != null) {
                controller.setStudentController(studentController);
            }

            Stage stage = (Stage) borrowedBooksContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
