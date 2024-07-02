package org.example.library.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CariBukuController {
    @FXML private ComboBox<String> kategoriComboBox;
    @FXML private TextField searchField;
    @FXML private HBox bukuListContainer;
    @FXML private Button homeButton;
    @FXML private Button searchButton;
    @FXML private Button myShelfButton;
    @FXML private VBox profileMenu;
    @FXML private Label loginCountLabel;

    private Map<String, Object> mahasiswa;
    private int loginCount;
    private StudentController studentController;

    @FXML
    private void initialize() {
        loadCategories();
        loadAllBooks();
        kategoriComboBox.setValue("All");
        kategoriComboBox.setOnAction(this::handleCategorySearch);
        setActiveButton(searchButton);
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
    }

    @FXML
    private void handleMyShelfClick() {
        setActiveButton(myShelfButton);
        loadSceneWithMahasiswa("buku_saya.fxml", BukuSayaController.class);
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

    @FXML
    private void handleProfileMenu() {
        boolean isVisible = profileMenu.isVisible();
        profileMenu.setVisible(!isVisible);
        profileMenu.setManaged(!isVisible);
    }

    private void loadCategories() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/org/example/library/categories.json")));
            List<String> categories = new Gson().fromJson(content, new TypeToken<List<String>>() {}.getType());
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
        return bookBox;
    }

    private List<Book> loadBooksFromJson() {
        try {
            String content = new String(Files.readAllBytes(Paths.get("src/main/resources/org/example/library/books.json")));
            return new Gson().fromJson(content, new TypeToken<List<Book>>() {}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showError("Format Error", "The book data is not in the correct format.");
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
        Stage stage = (Stage) searchField.getScene().getWindow();
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
                    ((CariBukuController) controller).setStudentController(studentController);
                } else if (controller instanceof BukuSayaController) {
                    ((BukuSayaController) controller).setMahasiswa(mahasiswa);
                    ((BukuSayaController) controller).setLoginCount(loginCount);
                }
            }

            Stage stage = (Stage) searchField.getScene().getWindow();
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
