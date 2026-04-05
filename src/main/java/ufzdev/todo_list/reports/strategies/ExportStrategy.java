package ufzdev.todo_list.reports.strategies;

import ufzdev.todo_list.models.TaskModel;

import java.nio.file.Path;
import java.util.List;

public interface ExportStrategy {
    Path export(List<TaskModel> data) throws Exception;
}

