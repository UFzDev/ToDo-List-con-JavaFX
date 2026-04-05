package ufzdev.todo_list.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.TaskModel;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.services.TaskService;
import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.TaskExecutorUtil;
import ufzdev.todo_list.util.UserSessionUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TasksController {
    @FXML
    private BorderPane rootPane;

    @FXML
    private Label lblUserName;

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cmbCategoryFilter;

    @FXML
    private ComboBox<String> cmbStatusFilter;

    @FXML
    private TableView<TaskModel> tasksTable;

    @FXML
    private TableColumn<TaskModel, String> colTaskName;

    @FXML
    private TableColumn<TaskModel, String> colDescription;

    @FXML
    private TableColumn<TaskModel, String> colCategory;

    @FXML
    private TableColumn<TaskModel, String> colStatus;

    @FXML
    private TableColumn<TaskModel, String> colDueDate;

    @FXML
    private TableColumn<TaskModel, String> colCreatedAt;

    private final TaskService taskService = new TaskService();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @FXML
    public void handleGoToReports(){
        Stage stage = getCurrentStage();
        if (stage != null) {
            NavigationUtil.goToReports(stage);
        }
    }

    @FXML
    public void handleGoToStats() {
        Stage stage = getCurrentStage();
        if (stage != null) {
            NavigationUtil.goToStats(stage);
        }
    }

    @FXML
    public void initialize() {
        loadUserName();
        configureTable();
        loadFilters();
        applyFilters();
        setupRefreshOnWindowFocus();
    }

    @FXML
    public void handleNewTask() {
        taskService.clearEditingTask();
        NavigationUtil.goToNewTask();
    }

    @FXML
    public void handleEditTask() {
        TaskModel selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertsUtil.showError("Sin selección",
                    "Selecciona una tarea para editar.");
            return;
        }

        taskService.setEditingTask(selected);
        NavigationUtil.goToNewTask();
    }

    @FXML
    public void handleDeleteTask() {
        TaskModel selected = tasksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertsUtil.showError("Sin selección",
                    "Selecciona una tarea para eliminar.");
            return;
        }

        TaskExecutorUtil.execute(
                () -> {
                    taskService.deleteTask(selected);
                    return true;
                },
                ignored -> {
                    AlertsUtil.showSuccess("Tarea eliminada", "La tarea se eliminó correctamente.");
                    refreshAfterModal();
                },
                error -> {
                    AlertsUtil.showError("Error al eliminar", "No se pudo eliminar la tarea.");
                    System.out.println("Error eliminando tarea: " + error.getMessage());
                }
        );
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
                    Stage stage = (Stage) lblUserName.getScene().getWindow();
                    NavigationUtil.goToLogin(stage);
                },
                error -> {
                    AlertsUtil.showError("Error al cerrar sesión", "No se pudo cerrar la sesión.");
                    System.out.println("Error cerrando sesión: " + error.getMessage());
                }
        );
    }

    @FXML
    public void handleFilterChanged() {
        applyFilters();
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

    private void configureTable() {
        colTaskName.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().getName())));
        colDescription.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().getDescription())));
        colCategory.setCellValueFactory(cell -> new SimpleStringProperty(joinCategories(cell.getValue().getCategory())));
        colStatus.setCellValueFactory(cell -> new SimpleStringProperty(safe(cell.getValue().getStatus())));
        colCreatedAt.setCellValueFactory(cell -> new SimpleStringProperty(formatCreatedAt(cell.getValue())));
        colDueDate.setCellValueFactory(cell -> new SimpleStringProperty(formatDate(cell.getValue())));
    }

    private void loadFilters() {
        List<String> categories = new ArrayList<>();
        categories.add("Todas");
        for (CategoryModel category : taskService.getSessionCategories()) {
            if (category != null && category.getName() != null && !category.getName().isBlank()
                    && !categories.contains(category.getName())) {
                categories.add(category.getName());
            }
        }

        List<String> statuses = new ArrayList<>();
        statuses.add("Todos");
        taskService.getSessionStatuses().forEach(status -> {
            if (status != null && status.getName() != null && !status.getName().isBlank()
                    && !statuses.contains(status.getName())) {
                statuses.add(status.getName());
            }
        });

        cmbCategoryFilter.getItems().setAll(categories);
        cmbStatusFilter.getItems().setAll(statuses);

        if (cmbCategoryFilter.getValue() == null) {
            cmbCategoryFilter.setValue("Todas");
        }
        if (cmbStatusFilter.getValue() == null) {
            cmbStatusFilter.setValue("Todos");
        }
    }

    private void applyFilters() {
        String query = txtSearch == null ? "" : txtSearch.getText();
        String category = cmbCategoryFilter == null ? "Todas" : cmbCategoryFilter.getValue();
        String status = cmbStatusFilter == null ? "Todos" : cmbStatusFilter.getValue();

        List<TaskModel> filtered = taskService.filterTasks(query, category, status);
        tasksTable.getItems().setAll(filtered);
    }

    private void refreshAfterModal() {
        loadFilters();
        applyFilters();
    }

    private void setupRefreshOnWindowFocus() {
        lblUserName.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (newScene == null || oldScene == newScene || observableScene == null) {
                return;
            }

            Platform.runLater(() -> {
                if (newScene.getWindow() instanceof Stage stage) {
                    stage.focusedProperty().addListener((observableFocus, wasFocused, isFocused) -> {
                        if (observableFocus != null && !wasFocused && isFocused) {
                            refreshAfterModal();
                        }
                    });
                }
            });
        });
    }

    private String joinCategories(List<CategoryModel> categories) {
        if (categories == null || categories.isEmpty()) {
            return "-";
        }

        StringBuilder builder = new StringBuilder();
        for (CategoryModel category : categories) {
            if (category == null || category.getName() == null || category.getName().isBlank()) {
                continue;
            }
            if (!builder.isEmpty()) {
                builder.append(", ");
            }
            builder.append(category.getName());
        }
        return builder.isEmpty() ? "-" : builder.toString();
    }

    private String formatDate(TaskModel task) {
        if (task.getLimitDate() == null) {
            return "Sin fecha";
        }
        return dateFormat.format(task.getLimitDate());
    }

    private String formatCreatedAt(TaskModel task) {
        if (task.getCreatedAt() == null) {
            return "-";
        }
        return dateFormat.format(task.getCreatedAt());
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private Stage getCurrentStage() {
        if (rootPane == null || rootPane.getScene() == null || rootPane.getScene().getWindow() == null) {
            return null;
        }
        return (Stage) rootPane.getScene().getWindow();
    }
}
