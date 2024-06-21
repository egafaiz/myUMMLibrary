package org.example.library.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudentController {
    @FXML private Label nimLabel;
    @FXML private Label loginCountLabel;
    // Other UI components for showing student data

    private LoginController.Mahasiswa mahasiswa;

    public void setMahasiswa(LoginController.Mahasiswa mahasiswa) {
        this.mahasiswa = mahasiswa;
        updateUI();
    }

    private void updateUI() {
        nimLabel.setText("NIM: " + mahasiswa.getNim());
        loginCountLabel.setText("Login Count: " + mahasiswa.getLoginCount());
        // Update other UI components with mahasiswa data
    }
}
