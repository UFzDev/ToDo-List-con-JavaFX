package ufzdev.todo_list.controllers;

import java.awt.Desktop;
import java.net.URI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.services.UserService;

import javafx.scene.input.MouseEvent;
import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.TaskExecutorUtil;
import ufzdev.todo_list.util.UserSessionUtil;

public class LoginController {
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
        UserModel userModel = new UserModel();
        userModel.setEmail(emailField.getText());
        userModel.setPassword(passwordField.getText());
        btnLogin.setDisable(true);
        TaskExecutorUtil.execute(
                () -> UserService.autenticate(userModel),
                userModelAuthenticated -> {
                    AlertsUtil.showSuccess("Login Exitoso", "Bienvenido, " + userModelAuthenticated.getUsername() + "!");
                    System.out.println("Login exitoso para el usuario: " + userModelAuthenticated.getUsername());
                    btnLogin.setDisable(false);

                    UserSessionUtil.getInstance().setUser(userModelAuthenticated);

                    // Navegar a la vista principal
                    Stage stage = (Stage) btnLogin.getScene().getWindow();
                    NavigationUtil.goToTasks(stage);
                },
                error -> {
                    AlertsUtil.showError("Error durante la autenticación", "Error: " + error.getMessage());
                    System.out.println("Error durante la autenticación: " + error.getMessage());
                    btnLogin.setDisable(false);
                }
        );
    }

    @FXML
    public void handleLoginTest() {
        btnTest.setDisable(true);
        TaskExecutorUtil.execute(
                () -> UserService.loginTest(),
                isAuthenticated -> {
                    AlertsUtil.showSuccess("Inicio de sesión de prueba exitoso",
                            "Se ha autenticado correctamente con el usuario de prueba.");
                    System.out.println("Login exitoso con el usuario de prueba.");
                    btnTest.setDisable(false);
                    // Navegar a la vista principal
                    Stage stage = (Stage) btnTest.getScene().getWindow();
                    NavigationUtil.goToTasks(stage);
                },
                error -> {
                    AlertsUtil.showError("Error en el inicio de sesión de prueba",
                            "No se pudo autenticar con el usuario de prueba. Error: " + error.getMessage());
                    System.out.println("Error durante el inicio de sesión de prueba: " + error.getMessage());
                    btnTest.setDisable(false);
                }
        );
    }

    @FXML
    private void handleOpenRegister(MouseEvent event) {
        NavigationUtil.goToRegister();
    }


    @FXML
    public void handleOpenGithub() {
        btnGit.setDisable(true);
        String githubUrl = "https://github.com/UFzDev/ToDo-List-con-JavaFX";
        TaskExecutorUtil.execute(
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
                        AlertsUtil.showSuccess("GitHub abierto", "Se ha abierto el repositorio de GitHub en tu navegador.");
                        System.out.println("GitHub abierto correctamente. URL: " + githubUrl);
                    } else {
                        AlertsUtil.showError("Navegador no soportado", "Tu sistema no soporta abrir el navegador automáticamente. Por favor, visita: " + githubUrl);
                        System.out.println("Navegador no soportado para abrir GitHub. URL: " + githubUrl);
                    }
                    btnGit.setDisable(false);
                },
                error -> {
                    AlertsUtil.showError("Error al abrir GitHub", "No se pudo abrir GitHub. Inténtalo de nuevo.");
                    System.out.println("Error al abrir GitHub: " + error.getMessage());
                    btnGit.setDisable(false);
                }
        );

    }
}
