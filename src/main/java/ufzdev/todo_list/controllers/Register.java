package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ufzdev.todo_list.services.UserService;
import ufzdev.todo_list.util.AlertUtils;
import ufzdev.todo_list.util.TaskExecutor;

public class Register {

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
    private Button btnRegister;

    @FXML
    public void handleRegister() {
        String name = nameField.getText();
        String user = usernameField.getText();
        String email = emailField.getText();
        String pasword = passwordField.getText();

        // Bloqueamos el botón o lanzamos un indicador de carga si lo tienes
        btnRegister.setDisable(true);

        TaskExecutor.execute(
                () -> {
                    UserService.registerUser(name, user, email, pasword);
                    return user;
                },
                resultUser -> {
                    AlertUtils.showSuccess("Registro exitoso", "Bienvenido a ToDo List, " + resultUser + "!");
                    System.out.println("Registro exitoso en Firebase para: " + resultUser);
                    btnRegister.setDisable(false);
                },
                error -> {
                    AlertUtils.showError("Error durante el registro", "Error: " + error.getMessage());
                    System.out.println("Error durante el registro: " + error.getMessage());
                    btnRegister.setDisable(false);
                }
        );
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
