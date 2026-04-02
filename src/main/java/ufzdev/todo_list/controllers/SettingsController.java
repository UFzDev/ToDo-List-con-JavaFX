package ufzdev.todo_list.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ufzdev.todo_list.services.CategoryService;
import ufzdev.todo_list.services.StatusService;
import ufzdev.todo_list.services.UserService;
import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.TaskExecutorUtil;
import ufzdev.todo_list.util.UserSessionUtil;
import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.StatusModel;

import java.util.ArrayList;
import java.util.List;

public class SettingsController {
    @FXML
    private TextField txtCategoryName;

    @FXML
    private TextField txtStatusName;

    @FXML
    private ListView<CategoryModel> listCategories;

    @FXML
    private ListView<StatusModel> listStatuses;

    @FXML
    private Button btnAddCategory;

    @FXML
    private Button btnDeleteCategory;

    @FXML
    private Button btnAddStatus;

    @FXML
    private Button btnDeleteStatus;

    @FXML
    private Button btnContinue;

    private final CategoryService categoryService = new CategoryService();
    private final StatusService statusService = new StatusService();
    private final UserSessionUtil session = UserSessionUtil.getInstance();

    @FXML
    public void initialize() {
        configureLists();
        loadDataFromSession();
    }

    @FXML
    public void handleAddCategory() {
        String name = txtCategoryName.getText() == null ? "" : txtCategoryName.getText().trim();
        if (name.isBlank()) {
            AlertsUtil.showError("Categoria vacia", "Escribe un nombre para la categoria.");
            return;
        }

        setCategoryControlsDisabled(true);
        TaskExecutorUtil.execute(
                () -> categoryService.createCategory(name),
                category -> {
                    txtCategoryName.clear();
                    refreshListsFromSession();
                    AlertsUtil.showSuccess("Categoria agregada", "La categoria se guardo correctamente.");
                    setCategoryControlsDisabled(false);
                },
                error -> {
                    AlertsUtil.showError("Error al guardar", "No se pudo crear la categoria.");
                    System.out.println("Error al crear categoria: " + error.getMessage());
                    setCategoryControlsDisabled(false);
                }
        );
    }

    @FXML
    public void handleDeleteCategory() {
        CategoryModel selected = listCategories.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertsUtil.showError("Sin seleccion", "Selecciona una categoria para eliminar.");
            return;
        }

        String selectedName = selected.getName();
        setCategoryControlsDisabled(true);
        TaskExecutorUtil.execute(
                () -> {
                    categoryService.deleteCategoryByName(selectedName);
                    return selectedName;
                },
                deletedName -> {
                    refreshListsFromSession();
                    AlertsUtil.showSuccess("Categoria eliminada", "La categoria se elimino correctamente.");
                    setCategoryControlsDisabled(false);
                },
                error -> {
                    AlertsUtil.showError("Error al eliminar", "No se pudo eliminar la categoria.");
                    System.out.println("Error al eliminar categoria: " + error.getMessage());
                    setCategoryControlsDisabled(false);
                }
        );
    }

    @FXML
    public void handleAddStatus() {
        String name = txtStatusName.getText() == null ? "" : txtStatusName.getText().trim();
        if (name.isBlank()) {
            AlertsUtil.showError("Estado vacio", "Escribe un nombre para el estado.");
            return;
        }

        setStatusControlsDisabled(true);
        TaskExecutorUtil.execute(
                () -> statusService.createStatus(name),
                status -> {
                    txtStatusName.clear();
                    refreshListsFromSession();
                    AlertsUtil.showSuccess("Estado agregado", "El estado se guardo correctamente.");
                    setStatusControlsDisabled(false);
                },
                error -> {
                    AlertsUtil.showError("Error al guardar", "No se pudo crear el estado.");
                    System.out.println("Error al crear estado: " + error.getMessage());
                    setStatusControlsDisabled(false);
                }
        );
    }

    @FXML
    public void handleDeleteStatus() {
        StatusModel selected = listStatuses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertsUtil.showError("Sin seleccion", "Selecciona un estado para eliminar.");
            return;
        }

        String selectedName = selected.getName();
        setStatusControlsDisabled(true);
        TaskExecutorUtil.execute(
                () -> {
                    statusService.deleteStatusByName(selectedName);
                    return selectedName;
                },
                deletedName -> {
                    refreshListsFromSession();
                    AlertsUtil.showSuccess("Estado eliminado", "El estado se elimino correctamente.");
                    setStatusControlsDisabled(false);
                },
                error -> {
                    AlertsUtil.showError("Error al eliminar", "No se pudo eliminar el estado.");
                    System.out.println("Error al eliminar estado: " + error.getMessage());
                    setStatusControlsDisabled(false);
                }
        );
    }

    @FXML
    public void handleContinue() {
        setContinueDisabled(true);
        TaskExecutorUtil.execute(
                () -> {
                    if (session.getUser() != null) {
                        UserService.completeSettings(session.getUser().getId());
                    }
                    return true;
                },
                ignored -> {
                    if (session.getUser() != null) {
                        session.getUser().setHasSettings(true);
                    }
                    AlertsUtil.showSuccess("Configuracion guardada", "Tus categorias y estados quedaron listos.");
                    NavigationUtil.closeModal((Stage) btnContinue.getScene().getWindow());
                },
                error -> {
                    AlertsUtil.showError("Error", "No se pudo cerrar la configuracion.");
                    System.out.println("Error al continuar desde settings: " + error.getMessage());
                    setContinueDisabled(false);
                }
        );
    }

    private void configureLists() {
        listCategories.setCellFactory(list -> {
            list.getItems();
            return new ListCell<>() {
                @Override
                protected void updateItem(CategoryModel item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getName());
                }
            };
        });

        listStatuses.setCellFactory(list -> {
            list.getItems();
            return new ListCell<>() {
                @Override
                protected void updateItem(StatusModel item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getName());
                }
            };
        });
    }

    private void loadDataFromSession() {
        refreshListsFromSession();
    }

    private void refreshListsFromSession() {
        List<CategoryModel> categories = categoryService.getSessionCategories();
        List<StatusModel> statuses = statusService.getSessionStatuses();
        listCategories.setItems(FXCollections.observableArrayList(categories));
        listStatuses.setItems(FXCollections.observableArrayList(statuses));
    }

    private void setCategoryControlsDisabled(boolean disabled) {
        txtCategoryName.setDisable(disabled);
        btnAddCategory.setDisable(disabled);
        btnDeleteCategory.setDisable(disabled);
    }

    private void setStatusControlsDisabled(boolean disabled) {
        txtStatusName.setDisable(disabled);
        btnAddStatus.setDisable(disabled);
        btnDeleteStatus.setDisable(disabled);
    }

    private void setContinueDisabled(boolean disabled) {
        btnContinue.setDisable(disabled);
    }
}
