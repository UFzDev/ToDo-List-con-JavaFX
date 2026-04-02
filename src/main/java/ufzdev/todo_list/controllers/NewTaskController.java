package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import ufzdev.todo_list.dao.CategoryDao;
import ufzdev.todo_list.dao.CategoryFirestoreDao;
import ufzdev.todo_list.dao.StatusDao;
import ufzdev.todo_list.dao.StatusFirestoreDao;
import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.StatusModel;
import ufzdev.todo_list.models.TaskModel;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.services.TaskService;
import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.TaskExecutorUtil;
import ufzdev.todo_list.util.UserSessionUtil;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewTaskController {
    @FXML
    private VBox rootVBox;
    @FXML
    private Label lblModalKicker;
    @FXML
    private Label lblModalTitle;
    @FXML
    private Label lblModalSubtitle;
    @FXML
    private TextField txtTaskName;
    @FXML
    private TextArea txtTaskDescription;
    @FXML
    private DatePicker dpLimitDate;
    @FXML
    private ComboBox<StatusModel> cmbStatus;
    @FXML
    private FlowPane categoriesPane;
    @FXML
    private Button btnSaveTask;
    @FXML
    private Button btnCancelTask;

    private final CategoryDao categoryDao = new CategoryFirestoreDao();
    private final StatusDao statusDao = new StatusFirestoreDao();
    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        configureStatusCombo();
        loadCatalogs();
        configureMode();
    }

    @FXML
    public void handleCreateTask() {
        btnSaveTask.setDisable(true);
        btnCancelTask.setDisable(true);

        UserSessionUtil session = UserSessionUtil.getInstance();
        UserModel user = session.getUser();
        TaskModel editingTask = taskService.getEditingTask();

        if (user == null) {
            AlertsUtil.showError("Sesion no disponible", "Inicia sesion nuevamente para guardar la tarea.");
            restoreButtons();
            return;
        }

        String name = txtTaskName.getText() == null ? "" : txtTaskName.getText().trim();
        if (name.isBlank()) {
            AlertsUtil.showError("Nombre requerido", "Escribe un nombre para la tarea.");
            restoreButtons();
            return;
        }

        StatusModel selectedStatus = cmbStatus.getValue();
        if (selectedStatus == null) {
            AlertsUtil.showError("Estado requerido", "Selecciona un estado para la tarea.");
            restoreButtons();
            return;
        }

        String description = txtTaskDescription.getText() == null ? "" : txtTaskDescription.getText().trim();
        Date limitDate = dpLimitDate.getValue() == null
                ? null
                : Date.from(dpLimitDate.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<CategoryModel> selectedCategories = readSelectedCategories();

        if (editingTask != null) {
            TaskExecutorUtil.execute(
                    () -> taskService.updateTask(
                            editingTask,
                            name,
                            description,
                            limitDate,
                            selectedStatus.getName(),
                            selectedCategories
                    ),
                    updatedTask -> {
                        taskService.clearEditingTask();
                        AlertsUtil.showSuccess("Tarea actualizada", "La tarea se editó correctamente.");
                        NavigationUtil.closeModal((Stage) rootVBox.getScene().getWindow());
                    },
                    error -> {
                        AlertsUtil.showError("Error al actualizar", "No se pudo editar la tarea.");
                        System.out.println("Error actualizando tarea: " + error.getMessage());
                        restoreButtons();
                    }
            );
            return;
        }

        TaskExecutorUtil.execute(
                () -> taskService.createTask(
                        user,
                        name,
                        description,
                        limitDate,
                        selectedStatus.getName(),
                        selectedCategories
                ),
                createdTask -> {
                    taskService.clearEditingTask();
                    AlertsUtil.showSuccess("Tarea guardada", "La tarea se creó correctamente.");
                    NavigationUtil.closeModal((Stage) rootVBox.getScene().getWindow());
                },
                error -> {
                    AlertsUtil.showError("Error al guardar", "No se pudo crear la tarea.");
                    System.out.println("Error guardando tarea: " + error.getMessage());
                    restoreButtons();
                }
        );
    }

    @FXML
    public void handleCancel() {
        taskService.clearEditingTask();
        NavigationUtil.closeModal((Stage) rootVBox.getScene().getWindow());
    }

    private void configureStatusCombo() {
        cmbStatus.setConverter(new StringConverter<>() {
            @Override
            public String toString(StatusModel statusModel) {
                return statusModel == null ? "" : statusModel.getName();
            }

            @Override
            public StatusModel fromString(String string) {
                return null;
            }
        });

        cmbStatus.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(StatusModel item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getName());
            }
        });
    }

    private void loadCatalogs() {
        UserSessionUtil session = UserSessionUtil.getInstance();

        try {
            List<StatusModel> statuses = new ArrayList<>(session.getStatuses());
            if (statuses.isEmpty()) {
                statuses = statusDao.findAll();
                session.getStatuses().clear();
                session.getStatuses().addAll(statuses);
            }

            List<CategoryModel> categories = new ArrayList<>(session.getCategories());
            if (categories.isEmpty()) {
                categories = categoryDao.findAll();
                session.getCategories().clear();
                session.getCategories().addAll(categories);
            }

            cmbStatus.getItems().setAll(statuses);
            renderCategoryChecks(categories);
        } catch (Exception e) {
            AlertsUtil.showError("Error de carga", "No se pudieron cargar categorias y estados.");
            System.out.println("Error cargando catalogos de nueva tarea: " + e.getMessage());
        }
    }

    private void renderCategoryChecks(List<CategoryModel> categories) {
        categoriesPane.getChildren().clear();

        for (CategoryModel category : categories) {
            CheckBox option = new CheckBox(category.getName());
            option.setUserData(category);
            option.getStyleClass().add("task-category-check");
            categoriesPane.getChildren().add(option);
        }
    }

    private List<CategoryModel> readSelectedCategories() {
        List<CategoryModel> selected = new ArrayList<>();
        for (javafx.scene.Node node : categoriesPane.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected() && checkBox.getUserData() instanceof CategoryModel category) {
                selected.add(category);
            }
        }
        return selected;
    }

    private void configureMode() {
        TaskModel editingTask = taskService.getEditingTask();
        if (editingTask == null) {
            lblModalKicker.setText("NUEVA TAREA");
            lblModalTitle.setText("Crea un pendiente claro");
            lblModalSubtitle.setText("Llena los datos principales de la tarea para dejarla lista dentro de tu flujo de trabajo.");
            btnSaveTask.setText("Guardar tarea");
            return;
        }

        lblModalKicker.setText("EDITAR TAREA");
        lblModalTitle.setText("Actualiza tu pendiente");
        lblModalSubtitle.setText("Modifica los datos necesarios y guarda los cambios de la tarea.");
        btnSaveTask.setText("Guardar cambios");

        txtTaskName.setText(editingTask.getName());
        txtTaskDescription.setText(editingTask.getDescription());
        if (editingTask.getLimitDate() != null) {
            dpLimitDate.setValue(editingTask.getLimitDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }

        for (StatusModel status : cmbStatus.getItems()) {
            if (status != null && status.getName() != null && status.getName().equalsIgnoreCase(editingTask.getStatus())) {
                cmbStatus.setValue(status);
                break;
            }
        }

        applySelectedCategories(editingTask.getCategory());
    }

    private void applySelectedCategories(List<CategoryModel> selectedCategories) {
        if (selectedCategories == null || selectedCategories.isEmpty()) {
            return;
        }

        for (javafx.scene.Node node : categoriesPane.getChildren()) {
            if (!(node instanceof CheckBox checkBox)) {
                continue;
            }
            Object data = checkBox.getUserData();
            if (!(data instanceof CategoryModel categoryData) || categoryData.getName() == null) {
                continue;
            }

            for (CategoryModel selected : selectedCategories) {
                if (selected != null && selected.getName() != null
                        && categoryData.getName().equalsIgnoreCase(selected.getName())) {
                    checkBox.setSelected(true);
                    break;
                }
            }
        }
    }

    private void restoreButtons() {
        btnSaveTask.setDisable(false);
        btnCancelTask.setDisable(false);
    }
}
