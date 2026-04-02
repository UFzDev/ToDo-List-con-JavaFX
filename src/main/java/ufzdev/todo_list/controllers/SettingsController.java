package ufzdev.todo_list.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import ufzdev.todo_list.dao.CategoryDao;
import ufzdev.todo_list.dao.CategoryFirestoreDao;
import ufzdev.todo_list.dao.StatusDao;
import ufzdev.todo_list.dao.StatusFirestoreDao;
import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.StatusModel;
import ufzdev.todo_list.services.UserService;
import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.UserSessionUtil;

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
    private Button btnContinue;

    private final CategoryDao categoryDao = new CategoryFirestoreDao();
    private final StatusDao statusDao = new StatusFirestoreDao();
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

        try {
            CategoryModel category = new CategoryModel();
            category.setName(name);
            category.setDescription("");
            categoryDao.create(category);
            session.addCategory(category);
            txtCategoryName.clear();
            refreshListsFromSession();
            AlertsUtil.showSuccess("Categoria agregada", "La categoria se guardo correctamente.");
        } catch (Exception e) {
            AlertsUtil.showError("Error al guardar", "No se pudo crear la categoria.");
            System.out.println("Error al crear categoria: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteCategory() {
        CategoryModel selected = listCategories.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertsUtil.showError("Sin seleccion", "Selecciona una categoria para eliminar.");
            return;
        }

        try {
            categoryDao.deleteByName(selected.getName());
            session.removeCategoryByName(selected.getName());
            refreshListsFromSession();
            AlertsUtil.showSuccess("Categoria eliminada", "La categoria se elimino correctamente.");
        } catch (Exception e) {
            AlertsUtil.showError("Error al eliminar", "No se pudo eliminar la categoria.");
            System.out.println("Error al eliminar categoria: " + e.getMessage());
        }
    }

    @FXML
    public void handleAddStatus() {
        String name = txtStatusName.getText() == null ? "" : txtStatusName.getText().trim();
        if (name.isBlank()) {
            AlertsUtil.showError("Estado vacio", "Escribe un nombre para el estado.");
            return;
        }

        try {
            StatusModel status = new StatusModel();
            status.setName(name);
            statusDao.create(status);
            session.addStatus(status);
            txtStatusName.clear();
            refreshListsFromSession();
            AlertsUtil.showSuccess("Estado agregado", "El estado se guardo correctamente.");
        } catch (Exception e) {
            AlertsUtil.showError("Error al guardar", "No se pudo crear el estado.");
            System.out.println("Error al crear estado: " + e.getMessage());
        }
    }

    @FXML
    public void handleDeleteStatus() {
        StatusModel selected = listStatuses.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertsUtil.showError("Sin seleccion", "Selecciona un estado para eliminar.");
            return;
        }

        try {
            statusDao.deleteByName(selected.getName());
            session.removeStatusByName(selected.getName());
            refreshListsFromSession();
            AlertsUtil.showSuccess("Estado eliminado", "El estado se elimino correctamente.");
        } catch (Exception e) {
            AlertsUtil.showError("Error al eliminar", "No se pudo eliminar el estado.");
            System.out.println("Error al eliminar estado: " + e.getMessage());
        }
    }

    @FXML
    public void handleContinue() {
        try {
            if (session.getUser() != null) {
                UserService.completeSettings(session.getUser().getId());
                session.getUser().setHasSettings(true);
            }
            AlertsUtil.showSuccess("Configuracion guardada", "Tus categorias y estados quedaron listos.");
            NavigationUtil.closeModal((Stage) btnContinue.getScene().getWindow());
        } catch (Exception e) {
            AlertsUtil.showError("Error", "No se pudo cerrar la configuracion.");
            System.out.println("Error al continuar desde settings: " + e.getMessage());
        }
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
        List<CategoryModel> categories = new ArrayList<>(session.getCategories());
        List<StatusModel> statuses = new ArrayList<>(session.getStatuses());
        listCategories.setItems(FXCollections.observableArrayList(categories));
        listStatuses.setItems(FXCollections.observableArrayList(statuses));
    }
}
