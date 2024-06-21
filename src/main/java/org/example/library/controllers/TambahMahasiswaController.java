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
import javafx.scene.layout.StackPane;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class TambahMahasiswaController {
    @FXML private TextField namaField;
    @FXML private TextField nimField;
    @FXML private TextField emailField;
    @FXML private TextField fakultasField;
    @FXML private TextField prodiField;
    @FXML private TextField picField;
    @FXML private ImageView fotoMahasiswaView;

    private File selectedImageFile;
    private final String DATA_FILE = "src/main/resources/org/example/library/mahasiswa.json";
    private final String IMAGE_DIR = "src/main/resources/org/example/library/images/";
    private final String PRODI_FILE = "src/main/resources/org/example/library/prodi.json";

    @FXML
    private void handleBackToAdmin(MouseEvent event) {
        loadScene("admin.fxml");
    }

    @FXML
    private void handleTambahMahasiswa(ActionEvent event) {
        // Validasi input
        if (isInputValid()) {
            // Simpan data ke berkas JSON
            try {
                saveMahasiswaToJson();
                showSuccessMessage("Mahasiswa berhasil ditambahkan!");
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
        File file = fileChooser.showOpenDialog(fotoMahasiswaView.getScene().getWindow());
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            fotoMahasiswaView.setImage(image);
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (namaField.getText() == null || namaField.getText().isEmpty()) {
            errorMessage += "Nama tidak boleh kosong!\n";
        }

        if (nimField.getText() == null || nimField.getText().isEmpty()) {
            errorMessage += "Nim tidak boleh kosong!\n";
        } else {
            if (nimField.getText().length() != 15 || !nimField.getText().matches("\\d+")) {
                errorMessage += "Nim harus 15 angka!\n";
            }
        }

        if (emailField.getText() == null || emailField.getText().isEmpty()) {
            errorMessage += "Email tidak boleh kosong!\n";
        } else {
            if (!isValidEmail(emailField.getText())) {
                errorMessage += "Format email tidak valid!\n";
            }
        }

        if (fakultasField.getText() == null || fakultasField.getText().isEmpty()) {
            errorMessage += "Fakultas tidak boleh kosong!\n";
        }

        if (prodiField.getText() == null || prodiField.getText().isEmpty()) {
            errorMessage += "Prodi tidak boleh kosong!\n";
        }

        if (picField.getText() == null || picField.getText().isEmpty()) {
            errorMessage += "PIC tidak boleh kosong!\n";
        } else {
            if (picField.getText().length() < 6) {
                errorMessage += "PIC harus minimal 6 karakter!\n";
            }
        }

        if (selectedImageFile == null) {
            errorMessage += "Foto tidak boleh kosong!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showErrorMessage(errorMessage);
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return Pattern.compile(emailPattern).matcher(email).matches();
    }

    private void saveMahasiswaToJson() throws Exception {
        List<Mahasiswa> mahasiswaList = loadMahasiswaFromJson();
        String newNim = nimField.getText();

        for (Mahasiswa mahasiswa : mahasiswaList) {
            if (mahasiswa.getNim().equals(newNim)) {
                throw new Exception("Nim sudah ada!");
            }
        }

        Mahasiswa newMahasiswa = new Mahasiswa();
        newMahasiswa.setNama(namaField.getText());
        newMahasiswa.setNim(newNim);
        newMahasiswa.setEmail(emailField.getText());
        newMahasiswa.setFakultas(fakultasField.getText());
        newMahasiswa.setProdi(prodiField.getText());
        newMahasiswa.setPic(picField.getText());

        // Simpan gambar ke direktori
        File imageDir = new File(IMAGE_DIR);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        File destFile = new File(IMAGE_DIR + selectedImageFile.getName());
        try {
            Files.copy(selectedImageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            newMahasiswa.setFoto(destFile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mahasiswaList.add(newMahasiswa);

        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            Gson gson = new Gson();
            gson.toJson(mahasiswaList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save prodi to JSON if it's new
        saveProdiToJson(prodiField.getText());
    }

    private void saveProdiToJson(String prodi) {
        List<String> prodiList = loadProdiFromJson();
        if (!prodiList.contains(prodi)) {
            prodiList.add(prodi);
            try (FileWriter writer = new FileWriter(PRODI_FILE)) {
                Gson gson = new Gson();
                gson.toJson(prodiList, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        } catch (IOException e) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void loadScene(String fxml) {
        Stage stage = (Stage) namaField.getScene().getWindow();
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
        handleBackToAdmin(null); // Kembali ke halaman admin setelah menampilkan pesan sukses
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
