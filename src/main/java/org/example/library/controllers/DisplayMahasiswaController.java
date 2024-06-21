package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Pos;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class DisplayMahasiswaController {
    @FXML private ComboBox<String> prodiComboBox;
    @FXML private TextField searchField;
    @FXML private VBox mahasiswaListContainer;

    private final String DATA_FILE = "src/main/resources/org/example/library/mahasiswa.json";
    private final String PRODI_FILE = "src/main/resources/org/example/library/prodi.json";

    @FXML
    private void initialize() {
        loadProdi();
        loadAllMahasiswa();
        prodiComboBox.setValue("All"); // Set default value for prodi
        prodiComboBox.setOnAction(this::handleProdiSearch);
    }

    @FXML
    private void handleBackToAdmin(MouseEvent event) {
        loadScene("admin.fxml");
    }

    @FXML
    private void handleSearch(MouseEvent event) {
        String searchQuery = searchField.getText();
        if (searchQuery.isEmpty()) {
            showError("Error", "Please enter a NIM to search.");
            return;
        }

        List<Mahasiswa> mahasiswa = loadMahasiswaFromJson();
        mahasiswaListContainer.getChildren().clear();
        boolean found = false;
        for (Mahasiswa mhs : mahasiswa) {
            if (mhs.getNim().contains(searchQuery) || mhs.getNama().toLowerCase().contains(searchQuery.toLowerCase())) {
                addMahasiswaToView(mhs);
                found = true;
            }
        }
        if (!found) {
            showError("Not Found", "No student found with the given NIM or Name.");
        }
    }

    @FXML
    private void handleProdiSearch(javafx.event.ActionEvent event) {
        String selectedProdi = prodiComboBox.getValue();
        List<Mahasiswa> mahasiswa = loadMahasiswaFromJson();
        mahasiswaListContainer.getChildren().clear();
        for (Mahasiswa mhs : mahasiswa) {
            if (selectedProdi.equals("All") || mhs.getProdi().equalsIgnoreCase(selectedProdi)) {
                addMahasiswaToView(mhs);
            }
        }
    }

    private void loadProdi() {
        List<String> prodi = loadProdiFromJson();
        if (prodi == null) {
            prodi = new ArrayList<>();
        }
        prodi.add(0, "All");
        prodiComboBox.getItems().addAll(prodi);
    }

    private void loadAllMahasiswa() {
        List<Mahasiswa> mahasiswa = loadMahasiswaFromJson();
        mahasiswaListContainer.getChildren().clear();
        for (Mahasiswa mhs : mahasiswa) {
            addMahasiswaToView(mhs);
        }
    }

    private void addMahasiswaToView(Mahasiswa mahasiswa) {
        if (mahasiswaListContainer.getChildren().isEmpty() || ((HBox) mahasiswaListContainer.getChildren().get(mahasiswaListContainer.getChildren().size() - 1)).getChildren().size() == 5) {
            HBox newRow = new HBox(20); // 20px spacing between student boxes
            newRow.setAlignment(Pos.CENTER);
            mahasiswaListContainer.getChildren().add(newRow);
        }
        HBox currentRow = (HBox) mahasiswaListContainer.getChildren().get(mahasiswaListContainer.getChildren().size() - 1);
        currentRow.getChildren().add(createMahasiswaView(mahasiswa));
    }

    private List<Mahasiswa> loadMahasiswaFromJson() {
        File dataFile = new File(DATA_FILE);
        if (!dataFile.exists()) {
            return new ArrayList<>();
        }

        try {
            String content = new String(Files.readAllBytes(dataFile.toPath()));
            Gson gson = new Gson();
            Type mahasiswaListType = new TypeToken<ArrayList<Mahasiswa>>(){}.getType();
            return gson.fromJson(content, mahasiswaListType);
        } catch (JsonSyntaxException | IOException e) {
            showError("Error loading students", "Terjadi kesalahan saat memuat data mahasiswa.");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<String> loadProdiFromJson() {
        File dataFile = new File(PRODI_FILE);
        if (!dataFile.exists()) {
            return new ArrayList<>();
        }

        try {
            String content = new String(Files.readAllBytes(dataFile.toPath()));
            Gson gson = new Gson();
            Type prodiListType = new TypeToken<ArrayList<String>>(){}.getType();
            return gson.fromJson(content, prodiListType);
        } catch (JsonSyntaxException | IOException e) {
            showError("Error loading programs", "Terjadi kesalahan saat memuat data prodi.");
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(searchField.getScene().getWindow());
        alert.showAndWait();
    }

    private VBox createMahasiswaView(Mahasiswa mahasiswa) {
        VBox mahasiswaBox = new VBox();
        mahasiswaBox.setSpacing(10);
        mahasiswaBox.getStyleClass().add("mahasiswa-box");

        HBox contentBox = new HBox(20); // Container for image and data
        contentBox.setAlignment(Pos.CENTER_LEFT);

        ImageView mahasiswaImage = new ImageView(new Image(new File(mahasiswa.getFoto()).toURI().toString()));
        mahasiswaImage.setFitHeight(150);
        mahasiswaImage.setFitWidth(100);
        mahasiswaImage.setPreserveRatio(false); // Make sure to fill the dimensions
        mahasiswaImage.getStyleClass().add("mahasiswa-image");

        VBox dataBox = new VBox();
        dataBox.setSpacing(5);
        dataBox.getStyleClass().add("mahasiswa-data");

        Label mahasiswaNama = new Label("Nama     : " + mahasiswa.getNama());
        mahasiswaNama.getStyleClass().add("mahasiswa-title");

        Label mahasiswaNim = new Label("NIM      : " + mahasiswa.getNim());
        mahasiswaNim.getStyleClass().add("mahasiswa-author");

        Label mahasiswaFakultas = new Label("Fakultas : " + mahasiswa.getFakultas());
        mahasiswaFakultas.getStyleClass().add("mahasiswa-author");

        Label mahasiswaProdi = new Label("Prodi    : " + mahasiswa.getProdi());
        mahasiswaProdi.getStyleClass().add("mahasiswa-author");

        Label mahasiswaPic = new Label("PIC      : " + mahasiswa.getPic());
        mahasiswaPic.getStyleClass().add("mahasiswa-author");

        dataBox.getChildren().addAll(mahasiswaNama, mahasiswaNim, mahasiswaFakultas, mahasiswaProdi, mahasiswaPic);
        contentBox.getChildren().addAll(mahasiswaImage, dataBox);
        mahasiswaBox.getChildren().add(contentBox);

        return mahasiswaBox;
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

    // Kelas untuk mahasiswa
    class Mahasiswa {
        private String nama;
        private String nim;
        private String email;
        private String fakultas;
        private String prodi;
        private String pic;
        private String foto;

        // Getter dan setter
        public String getNama() { return nama; }
        public void setNama(String nama) { this.nama = nama; }
        public String getNim() { return nim; }
        public void setNim(String nim) { this.nim = nim; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFakultas() { return fakultas; }
        public void setFakultas(String fakultas) { this.fakultas = fakultas; }
        public String getProdi() { return prodi; }
        public void setProdi(String prodi) { this.prodi = prodi; }
        public String getPic() { return pic; }
        public void setPic(String pic) { this.pic = pic; }
        public String getFoto() { return foto; }
        public void setFoto(String foto) { this.foto = foto; }
    }
}
