package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ufzdev.todo_list.util.NavigationUtil;

public class NewTaskController {
    @FXML
    private VBox rootVBox;
    @FXML
    private Button btnNewTask;

    @FXML
    public void handleCreateTask() {
    }

    @FXML
    public void handleCancel() { NavigationUtil.closeModal((Stage) rootVBox.getScene().getWindow()); }
}
