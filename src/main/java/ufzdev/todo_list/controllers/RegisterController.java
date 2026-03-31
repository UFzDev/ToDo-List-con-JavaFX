package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ufzdev.todo_list.services.AuthService;

public class RegisterController {

    @FXML
    private HBox rootPane;
    @FXML
    private TextField nameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleRegister() {
        String name = nameField.getText();
        String user = usernameField.getText();
        String email = emailField.getText();
        String pasword = passwordField.getText();

        try {
            AuthService.registerUser(name, user, email, pasword);

            System.out.println("Registro exitoso en Firebase para: " + email);

        } catch (Exception e) {
            System.out.println("Error durante el registro: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}
