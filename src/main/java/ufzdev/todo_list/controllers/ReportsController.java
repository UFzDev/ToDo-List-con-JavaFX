package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.StatusModel;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.TaskExecutorUtil;
import ufzdev.todo_list.util.UserSessionUtil;

import java.util.ArrayList;
import java.util.List;

public class ReportsController {
    @FXML
    private BorderPane rootPane;

    @FXML
    private Label lblUserName;

    @FXML
    private VBox categoriesBox;

    @FXML
    private VBox statusesBox;

    @FXML
    public void initialize() {
        loadUserName();
        loadFilterLists();
    }

    @FXML
    public void handleGoToTasks() {
        Stage stage = getCurrentStage();
        if (stage != null) {
            NavigationUtil.goToTasks(stage);
        }
    }

    @FXML
    public void handleOpenSettings() {
        NavigationUtil.goToSettings();
    }

    @FXML
    public void handleLogout() {
        TaskExecutorUtil.execute(
                () -> {
                    UserSessionUtil.getInstance().cleanSession();
                    return true;
                },
                ignored -> {
                    AlertsUtil.showSuccess("Sesión cerrada", "Has cerrado sesión correctamente.");
                    Stage stage = getCurrentStage();
                    if (stage != null) {
                        NavigationUtil.goToLogin(stage);
                    }
                },
                error -> {
                    AlertsUtil.showError("Error al cerrar sesión", "No se pudo cerrar la sesión.");
                    System.out.println("Error cerrando sesión: " + error.getMessage());
                }
        );
    }

    @FXML
    public void handleCreateExcelReport() {
        System.out.println("Crear reporte en Excel: pendiente de implementación.");
    }

    @FXML
    public void handleCreatePdfReport() {
        System.out.println("Crear reporte en PDF: pendiente de implementación.");
    }

    private void loadUserName() {
        UserModel user = UserSessionUtil.getInstance().getUser();
        if (user == null) {
            lblUserName.setText("Usuario");
            return;
        }

        String name = user.getName();
        if (name == null || name.isBlank()) {
            name = user.getUsername();
        }
        if (name == null || name.isBlank()) {
            name = "Usuario";
        }

        lblUserName.setText(name);
    }

    private void loadFilterLists() {
        renderCategoryChecks(new ArrayList<>(UserSessionUtil.getInstance().getCategories()));
        renderStatusChecks(new ArrayList<>(UserSessionUtil.getInstance().getStatuses()));
    }

    private void renderCategoryChecks(List<CategoryModel> categories) {
        categoriesBox.getChildren().clear();

        if (categories == null || categories.isEmpty()) {
            addEmptyMessage(categoriesBox, "No hay categorías cargadas.");
            return;
        }

        for (CategoryModel category : categories) {
            if (category == null || category.getName() == null || category.getName().isBlank()) {
                continue;
            }
            categoriesBox.getChildren().add(createCheckBox(category.getName(), category));
        }

        if (categoriesBox.getChildren().isEmpty()) {
            addEmptyMessage(categoriesBox, "No hay categorías válidas para mostrar.");
        }
    }

    private void renderStatusChecks(List<StatusModel> statuses) {
        statusesBox.getChildren().clear();

        if (statuses == null || statuses.isEmpty()) {
            addEmptyMessage(statusesBox, "No hay estatus cargados.");
            return;
        }

        for (StatusModel status : statuses) {
            if (status == null || status.getName() == null || status.getName().isBlank()) {
                continue;
            }
            statusesBox.getChildren().add(createCheckBox(status.getName(), status));
        }

        if (statusesBox.getChildren().isEmpty()) {
            addEmptyMessage(statusesBox, "No hay estatus válidos para mostrar.");
        }
    }

    private CheckBox createCheckBox(String text, Object value) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setUserData(value);
        checkBox.setWrapText(true);
        checkBox.setMaxWidth(Double.MAX_VALUE);
        checkBox.getStyleClass().add("app-filter-check");
        return checkBox;
    }

    private void addEmptyMessage(VBox container, String message) {
        Label label = new Label(message);
        label.getStyleClass().add("report-empty");
        container.getChildren().add(label);
    }

    private Stage getCurrentStage() {
        if (rootPane == null || rootPane.getScene() == null || rootPane.getScene().getWindow() == null) {
            return null;
        }
        return (Stage) rootPane.getScene().getWindow();
    }
}
