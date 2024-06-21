package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private Button loginButton;
    @FXML private ImageView togglePasswordVisibility;

    private boolean isPasswordVisible = false;
    private Map<String, Mahasiswa> studentData;

    private final String DATA_FILE = "src/main/resources/org/example/library/mahasiswa.json";

    @FXML
    private void initialize() {
        loadStudentData();

        // Prepare text field to display password as plain text
        passwordTextField.setManaged(false);
        passwordTextField.setVisible(false);
        passwordTextField.managedProperty().bind(passwordTextField.visibleProperty());
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());

        // Set the initial icon for the password visibility toggle
        updateTogglePasswordVisibilityIcon();

        togglePasswordVisibility.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> togglePasswordVisibility());
    }

    private void loadStudentData() {
        studentData = new HashMap<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(DATA_FILE)));
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Mahasiswa>>() {}.getType();
            List<Mahasiswa> mahasiswaList = gson.fromJson(content, listType);
            for (Mahasiswa mhs : mahasiswaList) {
                studentData.put(mhs.getNim(), mhs);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load student data.");
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordTextField.setVisible(false);
            passwordField.setVisible(true);
        } else {
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
        }
        isPasswordVisible = !isPasswordVisible;
        updateTogglePasswordVisibilityIcon();
    }

    private void updateTogglePasswordVisibilityIcon() {
        if (isPasswordVisible) {
            togglePasswordVisibility.setImage(new Image(getClass().getResourceAsStream("/org/example/library/mdi_eye.png")));
        } else {
            togglePasswordVisibility.setImage(new Image(getClass().getResourceAsStream("/org/example/library/off.png")));
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Implement login logic
        if ("admin".equals(username) && "admin".equals(password)) {
            loadScene("admin.fxml");
        } else if (studentData.containsKey(username) && studentData.get(username).getPic().equals(password)) {
            Mahasiswa mahasiswa = studentData.get(username);
            mahasiswa.incrementLogins();
            saveStudentData();
            loadStudentScene(mahasiswa);
        } else {
            showAlert("Login Failed", "Invalid NIM or PIC.");
        }
    }

    private void loadScene(String fxml) {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/" + fxml));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the scene.");
        }
    }

    private void loadStudentScene(Mahasiswa mahasiswa) {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/library/views/student.fxml"));
            Parent root = loader.load();

            // Pass Mahasiswa object to the controller
            StudentController controller = loader.getController();
            controller.setMahasiswa(mahasiswa);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the scene.");
        }
    }

    private void saveStudentData() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(studentData.values());
            Files.write(Paths.get(DATA_FILE), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save student data.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        passwordTextField.setText("");
        passwordTextField.setVisible(false);
        passwordField.setVisible(true);
        isPasswordVisible = false;
        updateTogglePasswordVisibilityIcon();
    }

    // Kelas untuk mahasiswa
    public class Mahasiswa {
        private String nim;
        private String pic;
        private int loginCount;
        private List<String> borrowedBooks;
        private List<String> returnedBooks;

        public Mahasiswa() {
            this.loginCount = 0;
        }

        public String getNim() { return nim; }
        public void setNim(String nim) { this.nim = nim; }
        public String getPic() { return pic; }
        public void setPic(String pic) { this.pic = pic; }
        public int getLoginCount() { return loginCount; }
        public void setLoginCount(int loginCount) { this.loginCount = loginCount; }
        public List<String> getBorrowedBooks() { return borrowedBooks; }
        public void setBorrowedBooks(List<String> borrowedBooks) { this.borrowedBooks = borrowedBooks; }
        public List<String> getReturnedBooks() { return returnedBooks; }
        public void setReturnedBooks(List<String> returnedBooks) { this.returnedBooks = returnedBooks; }

        public void incrementLogins() {
            this.loginCount++;
        }
    }
}