package ufzdev.todo_list.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ufzdev.todo_list.services.AuthService;

import javafx.scene.input.MouseEvent;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void handleLogin() {
        String username = emailField.getText();
        String password = passwordField.getText();
        try {
            if (AuthService.autenticate(username, password)) {
                System.out.println("Login exitoso para el usuario: " + username);
                // Futura vista principal de la aplicación
            } else {
                System.out.println("Credenciales inválidas para el usuario: " + username);
            }
        } catch (Exception e) {
            System.out.println("Error durante la autenticación: " + e.getMessage());
        }
    }
    @FXML
    public void handleLoginAnonimus() {

    }

    @FXML
    private void handleOpenRegister(MouseEvent event) {
        try {
            // FXMLLoader busca el archivo en la carpeta de recursos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ufzdev/todo_list/register-view.fxml"));
            Parent root = loader.load();

            // Configuración del modal para el registro
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea el Login mientras esta esté abierta
            stage.setTitle("Crear Cuenta - ToDo List");
            stage.setScene(new Scene(root));

            stage.show();

        } catch (IOException e) {
            System.out.println("Error al abrir la ventana de registro: " + e.getMessage());
        }
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
