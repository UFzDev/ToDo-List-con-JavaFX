package ufzdev.todo_list.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ufzdev.todo_list.util.NavigationUtil;

public class TasksController {
    @FXML
    Button btnNewTask;

    public void handleNewTask(){ NavigationUtil.goToNewTask(); }


}
