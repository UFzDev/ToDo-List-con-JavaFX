package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import ufzdev.todo_list.util.NavigationUtil;

public class TasksController {
    @FXML
    public void handleNewTask() {
        NavigationUtil.goToNewTask();
    }

    @FXML
    public void handleOpenSettings() {
        NavigationUtil.goToSettings();
    }
}
