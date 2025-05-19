package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class HelpController {

    @FXML
    private Button closeButton;

    @FXML
    public void initialize() {
        // Help screen initialization logic if needed
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
