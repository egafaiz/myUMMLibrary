package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class UpdateBukuController {
    @FXML private TextField idBukuField;
    @FXML private TextField judulBukuField;
    @FXML private TextField penulisBukuField;
    @FXML private TextField kategoriBukuField;
    @FXML private TextField stokBukuField;
    @FXML private TextField tahunTerbitField;
    @FXML private ImageView fotoBukuView;

    private File selectedImageFile;
    private final String DATA_FILE = "src/main/resources/org/example/library/books.json";
    private final String IMAGE_DIR = "src/main/resources/org/example/library/images/";
    private final String CATEGORY_FILE = "src/main/resources/org/example/library/categories.json";

    @FXML
    private void handleBackToAdmin(MouseEvent event) {
        loadScene("admin.fxml");
    }

    @FXML
    private void handleUpdateBuku(ActionEvent event) {
        if (isInputValid()) {
            try {
                updateBookInJson();
                showSuccessMessage("Buku berhasil diupdate!");
            } catch (Exception e) {
                showErrorMessage(e.getMessage());
            }
        }
    }

    @FXML
    private void handleUploadImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(fotoBukuView.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            fotoBukuView.setImage(image);
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (idBukuField.getText() == null || idBukuField.getText().isEmpty()) {
            errorMessage += "ID Buku tidak boleh kosong!\n";
        } else {
            try {
                Integer.parseInt(idBukuField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "ID Buku harus berupa angka!\n";
            }
        }

        if (judulBukuField.getText() == null || judulBukuField.getText().isEmpty()) {
            errorMessage += "Judul Buku tidak boleh kosong!\n";
        }

        if (penulisBukuField.getText() == null || penulisBukuField.getText().isEmpty()) {
            errorMessage += "Penulis Buku tidak boleh kosong!\n";
        } else {
            if (penulisBukuField.getText().matches(".*\\d.*")) {
                errorMessage += "Penulis Buku tidak boleh mengandung angka!\n";
            }
        }

        if (kategoriBukuField.getText() == null || kategoriBukuField.getText().isEmpty()) {
            errorMessage += "Kategori Buku tidak boleh kosong!\n";
        } else {
            if (kategoriBukuField.getText().matches(".*\\d.*")) {
                errorMessage += "Kategori Buku tidak boleh mengandung angka!\n";
            }
        }

        if (stokBukuField.getText() == null || stokBukuField.getText().isEmpty()) {
            errorMessage += "Stok Buku tidak boleh kosong!\n";
        } else {
            try {
                Integer.parseInt(stokBukuField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Stok Buku harus berupa angka!\n";
            }
        }

        if (tahunTerbitField.getText() == null || tahunTerbitField.getText().isEmpty()) {
            errorMessage += "Tahun Terbit tidak boleh kosong!\n";
        } else {
            try {
                Integer.parseInt(tahunTerbitField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Tahun Terbit harus berupa angka!\n";
            }
        }

        if (selectedImageFile == null && fotoBukuView.getImage() == null) {
            errorMessage += "Foto Buku tidak boleh kosong!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showErrorMessage(errorMessage);
            return false;
        }
    }

    private void updateBookInJson() throws Exception {
        List<Book> books = loadBooksFromJson();
        int bookId = Integer.parseInt(idBukuField.getText());

        Book existingBook = null;
        for (Book book : books) {
            if (book.getId() == bookId) {
                existingBook = book;
                break;
            }
        }

        if (existingBook == null) {
            throw new Exception("ID Buku tidak ditemukan!");
        }

        existingBook.setJudul(judulBukuField.getText());
        existingBook.setPenulis(penulisBukuField.getText());
        existingBook.setKategori(kategoriBukuField.getText());
        existingBook.setStok(Integer.parseInt(stokBukuField.getText()));
        existingBook.setTahun(Integer.parseInt(tahunTerbitField.getText()));

        if (selectedImageFile != null) {
            File imageDir = new File(IMAGE_DIR);
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }
            File destFile = new File(IMAGE_DIR + selectedImageFile.getName());
            try {
                Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                existingBook.setFoto(destFile.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            Gson gson = new Gson();
            gson.toJson(books, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void setBookData(int id, String judul, String penulis, String kategori, int stok, int tahun, String foto) {
        idBukuField.setText(String.valueOf(id));
        judulBukuField.setText(judul);
        penulisBukuField.setText(penulis);
        kategoriBukuField.setText(kategori);
        stokBukuField.setText(String.valueOf(stok));
        tahunTerbitField.setText(String.valueOf(tahun));
        if (foto != null) {
            Image image = new Image(new File(foto).toURI().toString());
            fotoBukuView.setImage(image);
        }
    }

    private void loadScene(String fxml) {
        Stage stage = (Stage) idBukuField.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/" + fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Sukses");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        handleBackToAdmin(null);
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Kelas untuk buku
    class Book {
        private int id;
        private String judul;
        private String penulis;
        private String kategori;
        private int stok;
        private int tahun;
        private String foto;

        // Getter dan setter
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getJudul() { return judul; }
        public void setJudul(String judul) { this.judul = judul; }
        public String getPenulis() { return penulis; }
        public void setPenulis(String penulis) { this.penulis = penulis; }
        public String getKategori() { return kategori; }
        public void setKategori(String kategori) { this.kategori = kategori; }
        public int getStok() { return stok; }
        public void setStok(int stok) { this.stok = stok; }
        public int getTahun() { return tahun; }
        public void setTahun(int tahun) { this.tahun = tahun; }
        public String getFoto() { return foto; }
        public void setFoto(String foto) { this.foto = foto; }
    }
}
