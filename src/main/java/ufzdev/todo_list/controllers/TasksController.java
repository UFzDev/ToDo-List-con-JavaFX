package ufzdev.todo_list.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.TaskModel;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.services.TaskService;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.UserSessionUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TasksController {
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

    private final TaskService taskService = new TaskService();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

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
        NavigationUtil.goToNewTask();
    }

    @FXML
    public void handleOpenSettings() {
        NavigationUtil.goToSettings();
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

    @SuppressWarnings("unused")
    private void setupRefreshOnWindowFocus() {
        lblUserName.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                return;
            }

            Platform.runLater(() -> {
                if (newScene.getWindow() instanceof Stage stage) {
                    stage.focusedProperty().addListener((focusObs, wasFocused, isFocused) -> {
                        if (isFocused) {
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

    private String safe(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
