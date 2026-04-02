package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class SettingsController {
    @FXML
    private TextField txtCategoryName;

    @FXML
    private TextField txtStatusName;

    @FXML
    private ListView<String> listCategories;

    @FXML
    private ListView<String> listStatuses;

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

    @FXML
    public void handleContinue() {

    }
}
