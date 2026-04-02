package ufzdev.todo_list.dao;

import ufzdev.todo_list.models.TaskModel;

import java.util.List;

public interface TaskDao {
    List<TaskModel> findByUserId(String userId) throws Exception;

    String create(TaskModel taskModel) throws Exception;

    void update(TaskModel taskModel) throws Exception;

    void updateStatus(String taskId, String newStatus) throws Exception;

    void deleteById(String taskId) throws Exception;
}
