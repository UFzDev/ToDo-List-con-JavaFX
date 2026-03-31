package ufzdev.todo_list.controllers;

import java.awt.Desktop;
import java.net.URI;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

public class Controller {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    public void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        System.out.println("Username: " + username + ", Password: " + password);
    }

    public void handleClear() {
        usernameField.clear();
        passwordField.clear();
    }

    @FXML
    public void handleOpenGithub() {
        String githubUrl = "https://github.com/UFzDev/ToDo-List-con-JavaFX";
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(githubUrl));
            } else {
                System.out.println("No se puede abrir el navegador. URL: " + githubUrl);
            }
        } catch (Exception e) {
            System.out.println("Error al abrir GitHub: " + e.getMessage());
        }
    }
}
