package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.util.NavigationUtil;
import ufzdev.todo_list.util.UserSessionUtil;

public class TasksController {
    @FXML
    private Label lblUserName;

    @FXML
    public void initialize() {
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

    @FXML
    public void handleNewTask() {
        NavigationUtil.goToNewTask();
    }

    @FXML
    public void handleOpenSettings() {
        NavigationUtil.goToSettings();
    }
}
