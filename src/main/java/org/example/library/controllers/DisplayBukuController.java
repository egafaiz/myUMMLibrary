package org.example.library.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.library.Book;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DisplayBukuController {
    @FXML private ComboBox<String> kategoriComboBox;
    @FXML private TextField searchField;
    @FXML private VBox bukuListContainer;

    private final String DATA_FILE = "src/main/resources/org/example/library/books.json";
    private final String CATEGORY_FILE = "src/main/resources/org/example/library/categories.json";

    @FXML
    private void initialize() {
        loadCategories();
        loadAllBooks();
        kategoriComboBox.setValue("All"); // Set default value for category
        kategoriComboBox.setOnAction(this::handleCategorySearch);
    }

    @FXML
    private void handleBackToAdmin(MouseEvent event) {
        loadScene("admin.fxml");
    }

    @FXML
    private void handleSearch(MouseEvent event) {
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

    private void loadCategories() {
        List<String> categories = loadCategoriesFromJson();
        categories.add(0, "All");
        kategoriComboBox.getItems().addAll(categories);
    }

    private void loadAllBooks() {
        List<Book> books = loadBooksFromJson();
        bukuListContainer.getChildren().clear();
        for (Book book : books) {
            addBookToView(book);
        }
    }

    private void addBookToView(Book book) {
        if (bukuListContainer.getChildren().isEmpty() || ((HBox)bukuListContainer.getChildren().get(bukuListContainer.getChildren().size() - 1)).getChildren().size() == 5) {
            HBox newRow = new HBox(20); // 20px spacing between book boxes
            newRow.setAlignment(Pos.CENTER);
            bukuListContainer.getChildren().add(newRow);
        }
        HBox currentRow = (HBox) bukuListContainer.getChildren().get(bukuListContainer.getChildren().size() - 1);
        currentRow.getChildren().add(createBookView(book));
    }

    private List<Book> loadBooksFromJson() {
        File dataFile = new File(DATA_FILE);
        if (!dataFile.exists()) {
            return new ArrayList<>();
        }

        try {
            String content = new String(Files.readAllBytes(dataFile.toPath()));
            Gson gson = new Gson();
            Type bookListType = new TypeToken<ArrayList<Book>>(){}.getType();
            return gson.fromJson(content, bookListType);
        } catch (JsonSyntaxException | IOException e) {
            showError("Error loading books", "Terjadi kesalahan saat memuat data buku.");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<String> loadCategoriesFromJson() {
        File dataFile = new File(CATEGORY_FILE);
        if (!dataFile.exists()) {
            return new ArrayList<>();
        }

        try {
            String content = new String(Files.readAllBytes(dataFile.toPath()));
            Gson gson = new Gson();
            Type categoryListType = new TypeToken<ArrayList<String>>(){}.getType();
            return gson.fromJson(content, categoryListType);
        } catch (JsonSyntaxException | IOException e) {
            showError("Error loading categories", "Terjadi kesalahan saat memuat data kategori.");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Stage stage = (Stage) searchField.getScene().getWindow();
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private VBox createBookView(Book book) {
        VBox bookBox = new VBox();
        bookBox.setSpacing(10);
        bookBox.getStyleClass().add("book-box");

        ImageView bookImage = new ImageView(new Image(new File(book.getFoto()).toURI().toString()));
        bookImage.setFitHeight(200);
        bookImage.setFitWidth(150);
        bookImage.setPreserveRatio(false); // Make sure to fill the dimensions
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

    private void loadScene(String fxml) {
        Stage stage = (Stage) searchField.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/" + fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error loading scene", "Terjadi kesalahan saat memuat scene.");
            e.printStackTrace();
        }
    }
}
