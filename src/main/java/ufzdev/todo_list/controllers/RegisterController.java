package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

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
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        System.out.println("REGISTER | Name: " + name + " | Username: " + username + " | Email: " + email + " | Pass length: " + password.length());
        closeModal();
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
