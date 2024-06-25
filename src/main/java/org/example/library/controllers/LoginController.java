package org.example.library.controllers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.library.BorrowedBook;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private Button loginButton;
    @FXML private ImageView togglePasswordVisibility;

    private boolean isPasswordVisible = false;
    private Map<String, Mahasiswa> studentData;
    private Map<String, Integer> loginData;

    private final String STUDENT_DATA_FILE = "src/main/resources/org/example/library/mahasiswa.json";
    private final String LOGIN_DATA_FILE = "src/main/resources/org/example/library/login_data.json";

    @FXML
    private void initialize() {
        loadStudentData();
        loadLoginData();

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
            String content = new String(Files.readAllBytes(Paths.get(STUDENT_DATA_FILE)));
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Mahasiswa>>() {}.getType();
            List<Mahasiswa> mahasiswaList = gson.fromJson(content, listType);
            if (mahasiswaList != null) {
                for (Mahasiswa mhs : mahasiswaList) {
                    studentData.put(mhs.getNim(), mhs);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load student data.");
        }
    }

    private void loadLoginData() {
        loginData = new HashMap<>();
        try {
            String content = new String(Files.readAllBytes(Paths.get(LOGIN_DATA_FILE)));
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, Integer>>() {}.getType();
            loginData = gson.fromJson(content, mapType);
            if (loginData == null) {
                loginData = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load login data.");
        }
    }

    private void saveLoginData() {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(loginData);
            Files.write(Paths.get(LOGIN_DATA_FILE), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to save login data.");
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
            incrementLoginCount(username);
            loadStudentScene(mahasiswa);
        } else {
            showAlert("Login Failed", "Invalid NIM or PIC.");
        }
    }

    private void incrementLoginCount(String nim) {
        loginData.put(nim, loginData.getOrDefault(nim, 0) + 1);
        saveLoginData();
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
            controller.setLoginCount(loginData.get(mahasiswa.getNim()));

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/org/example/library/styles.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the scene.");
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
    public static class Mahasiswa {
        private String nim;
        private String pic;
        private List<BorrowedBook> borrowedBooks = new ArrayList<>();
        private List<String> returnedBooks = new ArrayList<>();

        public String getNim() { return nim; }
        public void setNim(String nim) { this.nim = nim; }
        public String getPic() { return pic; }
        public void setPic(String pic) { this.pic = pic; }
        public List<BorrowedBook> getBorrowedBooks() { return borrowedBooks; }
        public void setBorrowedBooks(List<BorrowedBook> borrowedBooks) { this.borrowedBooks = borrowedBooks; }
        public List<String> getReturnedBooks() { return returnedBooks; }
        public void setReturnedBooks(List<String> returnedBooks) { this.returnedBooks = returnedBooks; }
    }
}
