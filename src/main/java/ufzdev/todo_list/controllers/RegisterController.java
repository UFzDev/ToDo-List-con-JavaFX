package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ufzdev.todo_list.services.UserService;
import ufzdev.todo_list.util.AlertUtils;

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
            UserService.registerUser(name, user, email, pasword);
            AlertUtils.showSuccess("Registro exitoso", "Bienvenido a ToDo List, " + user + "!");
            System.out.println("Registro exitoso en Firebase para: " + user);

        } catch (Exception e) {
            AlertUtils.showError("Error durante el registro", "Error: " + e.getMessage());
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
