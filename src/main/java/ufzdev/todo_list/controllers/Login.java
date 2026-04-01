package ufzdev.todo_list.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import ufzdev.todo_list.models.User;
import ufzdev.todo_list.services.UserService;

import javafx.scene.input.MouseEvent;
import ufzdev.todo_list.util.AlertUtils;
import ufzdev.todo_list.util.NavigationUtils;
import ufzdev.todo_list.util.TaskExecutor;
import ufzdev.todo_list.util.UserSession;

public class Login {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnGit;
    @FXML
    private Button btnTest;

    @FXML
    public void handleLogin() {
        User user = new User();
        user.setEmail(emailField.getText());
        user.setPassword(passwordField.getText());
        btnLogin.setDisable(true);
        TaskExecutor.execute(
                () -> UserService.autenticate(user),
                userAuthenticated -> {
                    AlertUtils.showSuccess("Login Exitoso", "Bienvenido, " + userAuthenticated.getUsername() + "!");
                    System.out.println("Login exitoso para el usuario: " + userAuthenticated.getUsername());
                    btnLogin.setDisable(false);

                    UserSession.getInstance().setUser(userAuthenticated);

                    // Navegar a la vista principal
                    Stage stage = (Stage) btnLogin.getScene().getWindow();
                    NavigationUtils.goToTasks(stage);
                },
                error -> {
                    AlertUtils.showError("Error durante la autenticación", "Error: " + error.getMessage());
                    System.out.println("Error durante la autenticación: " + error.getMessage());
                    btnLogin.setDisable(false);
                }
        );
    }

    @FXML
    public void handleLoginTest() {
        btnTest.setDisable(true);
        TaskExecutor.execute(
                () -> UserService.loginTest(),
                isAuthenticated -> {
                    AlertUtils.showSuccess("Inicio de sesión de prueba exitoso",
                            "Se ha autenticado correctamente con el usuario de prueba.");
                    System.out.println("Login exitoso con el usuario de prueba.");
                    
                    // Navegar a la vista principal
                    Stage stage = (Stage) btnTest.getScene().getWindow();
                    NavigationUtils.goToTasks(stage);
                },
                error -> {
                    AlertUtils.showError("Error en el inicio de sesión de prueba",
                            "No se pudo autenticar con el usuario de prueba. Error: " + error.getMessage());
                    System.out.println("Error durante el inicio de sesión de prueba: " + error.getMessage());
                    btnTest.setDisable(false);
                }
        );
    }

    @FXML
    private void handleOpenRegister(MouseEvent event) {
        NavigationUtils.goToRegister();
    }


    @FXML
    public void handleOpenGithub() {
        btnGit.setDisable(true);
        String githubUrl = "https://github.com/UFzDev/ToDo-List-con-JavaFX";
        TaskExecutor.execute(
                () -> {
                     if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                        Desktop.getDesktop().browse(new URI(githubUrl));
                        return true;
                    } else {
                        return false;
                    }
                },
                success -> {
                    if (success) {
                        AlertUtils.showSuccess("GitHub abierto", "Se ha abierto el repositorio de GitHub en tu navegador.");
                        System.out.println("GitHub abierto correctamente. URL: " + githubUrl);
                    } else {
                        AlertUtils.showError("Navegador no soportado", "Tu sistema no soporta abrir el navegador automáticamente. Por favor, visita: " + githubUrl);
                        System.out.println("Navegador no soportado para abrir GitHub. URL: " + githubUrl);
                    }
                    btnGit.setDisable(false);
                },
                error -> {
                    AlertUtils.showError("Error al abrir GitHub", "No se pudo abrir GitHub. Inténtalo de nuevo.");
                    System.out.println("Error al abrir GitHub: " + error.getMessage());
                    btnGit.setDisable(false);
                }
        );

    }
}
