package org.example.library.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.library.Book;
import org.example.library.BorrowedBook;
import org.example.library.EmailSender;
import org.example.library.LocalDateAdapter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class StudentController {
    @FXML private ComboBox<String> kategoriComboBox;
    @FXML private TextField searchField;
    @FXML private HBox bukuListContainer;
    @FXML private Button homeButton;
    @FXML private Button searchButton;
    @FXML private Button myShelfButton;
    @FXML private VBox profileMenu;
    @FXML private Label loginCountLabel;
    @FXML private Label totalKunjunganLabel;
    @FXML private Label bukuTerpinjamLabel;
    @FXML private Label terlambatLabel;

    private Map<String, Object> mahasiswa;
    private int loginCount;
    private int totalBorrowedBooks = 0;
    private Gson gson;
    private EmailSender emailSender;

    @FXML
    private void initialize() {
        gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();
        emailSender = new EmailSender();
        loadCategories();
        loadAllBooks();
        kategoriComboBox.setValue("All");
        kategoriComboBox.setOnAction(this::handleCategorySearch);
        setActiveButton(homeButton);
    }

    public void setMahasiswa(Map<String, Object> mahasiswa) {
        this.mahasiswa = mahasiswa;
        loadBorrowedBooks();
        loadTotalBorrowedBooks();
        updateUI();
    }

    public Map<String, Object> getMahasiswa() {
        return this.mahasiswa;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
        updateUI();
    }

    public void updateUI() {
        if (loginCountLabel != null) {
            loginCountLabel.setText(String.valueOf(loginCount));
        }
        if (totalKunjunganLabel != null) {
            totalKunjunganLabel.setText(String.valueOf(loginCount));
        }
        if (bukuTerpinjamLabel != null && mahasiswa != null) {
            List<BorrowedBook> borrowedBooks = (List<BorrowedBook>) mahasiswa.get("borrowedBooks");
            bukuTerpinjamLabel.setText(String.valueOf(borrowedBooks != null ? borrowedBooks.size() : 0));
        }
        if (terlambatLabel != null && mahasiswa != null) {
            List<BorrowedBook> borrowedBooks = (List<BorrowedBook>) mahasiswa.get("borrowedBooks");
            terlambatLabel.setText(String.valueOf(countLateBooks(borrowedBooks)));
        }
        saveBorrowedBooks((List<BorrowedBook>) mahasiswa.get("borrowedBooks"));
        saveTotalBorrowedBooks(totalBorrowedBooks);
    }

    private int countLateBooks(List<BorrowedBook> borrowedBooks) {
        if (borrowedBooks == null) {
            return 0;
        }
        int count = 0;
        for (BorrowedBook book : borrowedBooks) {
            if (book.getReturnDate() != null && book.getReturnDate().isBefore(LocalDate.now())) {
                count++;
            }
        }
        return count;
    }

    private long calculateTotalFine(List<BorrowedBook> borrowedBooks) {
        if (borrowedBooks == null) {
            return 0;
        }
        long totalFine = 0;
        for (BorrowedBook book : borrowedBooks) {
            totalFine += book.calculateFine();
        }
        return totalFine;
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
        loadSceneWithMahasiswa("buku_saya.fxml", BukuSayaController.class);
    }

    @FXML
    private void handleSearch(javafx.scene.input.MouseEvent event) {
        String searchQuery = searchField.getText();
        if (searchQuery.isEmpty()) {
            showError("Error", "Please enter a book ID to search.");
            return;
        }

        List<Book> books = loadBooksFromJson();
        bukuListContainer.getChildren().clear();
        boolean found = false;
        for (Book book : books) {
            if (String.valueOf(book.getId()).contains(searchQuery)) {
                addBookToView(book);
                found = true;
            }
        }
        if (!found) {
            showError("Not Found", "No book found with the given ID.");
        }
    }

    @FXML
    private void handleCategorySearch(javafx.event.ActionEvent event) {
        String selectedCategory = kategoriComboBox.getValue();
        List<Book> books = loadBooksFromJson();
        bukuListContainer.getChildren().clear();
        for (Book book : books) {
            if (selectedCategory.equals("All") || book.getKategori().equalsIgnoreCase(selectedCategory)) {
                addBookToView(book);
            }
        }
    }

    @FXML
    private void handleProfileMenu() {
        boolean isVisible = profileMenu.isVisible();
        profileMenu.setVisible(!isVisible);
        profileMenu.setManaged(!isVisible);
    }

    private void loadCategories() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/org/example/library/categories.json")));
            List<String> categories = gson.fromJson(content, new TypeToken<List<String>>() {}.getType());
            categories.add(0, "All");
            kategoriComboBox.getItems().addAll(categories);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAllBooks() {
        List<Book> books = loadBooksFromJson();
        bukuListContainer.getChildren().clear();
        for (Book book : books) {
            addBookToView(book);
        }
    }

    private void addBookToView(Book book) {
        VBox bookBox = createBookView(book);
        bukuListContainer.getChildren().add(bookBox);
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

        bookBox.setOnMouseClicked(event -> handleBookClick(book));

        return bookBox;
    }

    private void handleBookClick(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/info_buku.fxml"));
            Parent root = loader.load();

            InfoBukuController controller = loader.getController();
            controller.setBook(book);
            controller.setMahasiswa(mahasiswa);
            controller.setLoginCount(loginCount);
            controller.setStudentController(this);

            Stage stage = (Stage) bukuListContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Book> loadBooksFromJson() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/org/example/library/books.json")));
            return gson.fromJson(content, new TypeToken<List<Book>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(searchField.getScene().getWindow());
        alert.showAndWait();
    }

    private void loadScene(String fxml) {
        Stage stage = (Stage) totalKunjunganLabel.getScene().getWindow();
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
                    ((CariBukuController) controller).setStudentController(this);
                } else if (controller instanceof BukuSayaController) {
                    ((BukuSayaController) controller).setMahasiswa(mahasiswa);
                    ((BukuSayaController) controller).setLoginCount(loginCount);
                    ((BukuSayaController) controller).setStudentController(this);
                } else if (controller instanceof BukuTerpinjamController) {
                    ((BukuTerpinjamController) controller).setMahasiswa(mahasiswa);
                    ((BukuTerpinjamController) controller).setLoginCount(loginCount);
                    ((BukuTerpinjamController) controller).setStudentController(this);
                }
            }

            Stage stage = (Stage) totalKunjunganLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBorrowedBooks() {
        String filePath = "src/main/resources/org/example/library/borrowed_books_" + mahasiswa.get("nim") + ".json";
        try {
            if (!Files.exists(Paths.get(filePath))) {
                Files.createDirectories(Paths.get(filePath).getParent());
                Files.createFile(Paths.get(filePath));
                Files.write(Paths.get(filePath), "[]".getBytes());
            }
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            List<BorrowedBook> borrowedBooks = gson.fromJson(content, new TypeToken<List<BorrowedBook>>() {}.getType());
            if (borrowedBooks == null) {
                borrowedBooks = new ArrayList<>();
            }
            mahasiswa.put("borrowedBooks", borrowedBooks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveBorrowedBooks(List<BorrowedBook> borrowedBooks) {
        String filePath = "src/main/resources/org/example/library/borrowed_books_" + mahasiswa.get("nim") + ".json";
        try {
            Files.createDirectories(Paths.get(filePath).getParent());
            String json = gson.toJson(borrowedBooks);
            Files.write(Paths.get(filePath), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadTotalBorrowedBooks() {
        String filePath = "src/main/resources/org/example/library/total_borrowed_books_" + mahasiswa.get("nim") + ".json";
        try {
            if (!Files.exists(Paths.get(filePath))) {
                Files.createDirectories(Paths.get(filePath).getParent());
                Files.createFile(Paths.get(filePath));
                Files.write(Paths.get(filePath), "0".getBytes());
            }
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            totalBorrowedBooks = Integer.parseInt(content.trim());
            if (bukuTerpinjamLabel != null) {
                bukuTerpinjamLabel.setText(String.valueOf(totalBorrowedBooks));
            }
        } catch (IOException | NumberFormatException e) {
            totalBorrowedBooks = 0;
            if (bukuTerpinjamLabel != null) {
                bukuTerpinjamLabel.setText(String.valueOf(totalBorrowedBooks));
            }
        }
    }

    public void saveTotalBorrowedBooks(int totalBorrowedBooks) {
        String filePath = "src/main/resources/org/example/library/total_borrowed_books_" + mahasiswa.get("nim") + ".json";
        try {
            Files.createDirectories(Paths.get(filePath).getParent());
            Files.write(Paths.get(filePath), String.valueOf(totalBorrowedBooks).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendEmailNotification(String subject, String body) {
        String email = (String) mahasiswa.get("email");
        if (email != null && !email.isEmpty()) {
            emailSender.sendEmail(email, subject, body);
        } else {
            System.out.println("Email not found for the current student.");
        }
    }

    public void updateBookStock(int bookId, int change) {
        // Implement the logic to update the stock of the book by 'change' amount
        List<Book> books = loadBooksFromJson();
        for (Book book : books) {
            if (book.getId() == bookId) {
                book.setStok(book.getStok() + change);
                break;
            }
        }
        // Save the updated list of books back to the JSON file
        saveBooksToJson(books);
    }

    public void incrementTotalBorrowedBooks() {
        totalBorrowedBooks++;
        saveTotalBorrowedBooks(totalBorrowedBooks);
    }

    public void decrementTotalBorrowedBooks() {
        totalBorrowedBooks--;
        saveTotalBorrowedBooks(totalBorrowedBooks);
    }

    private void saveBooksToJson(List<Book> books) {
        String filePath = "src/main/resources/org/example/library/books.json";
        try {
            String json = gson.toJson(books);
            Files.write(Paths.get(filePath), json.getBytes());
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
