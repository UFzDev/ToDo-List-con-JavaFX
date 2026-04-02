package ufzdev.todo_list.services;

import ufzdev.todo_list.dao.TaskDao;
import ufzdev.todo_list.dao.TaskFirestoreDao;
import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.StatusModel;
import ufzdev.todo_list.models.TaskModel;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.util.UserSessionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskService {
    private final TaskDao taskDao = new TaskFirestoreDao();
    private final UserSessionUtil session = UserSessionUtil.getInstance();

    public List<TaskModel> getSessionTasks() {
        return new ArrayList<>(session.getTasks());
    }

    public List<CategoryModel> getSessionCategories() {
        return new ArrayList<>(session.getCategories());
    }

    public List<StatusModel> getSessionStatuses() {
        return new ArrayList<>(session.getStatuses());
    }

    public TaskModel getEditingTask() {
        return session.getEditingTask();
    }

    public void setEditingTask(TaskModel task) {
        session.setEditingTask(task);
    }

    public void clearEditingTask() {
        session.clearEditingTask();
    }

    public TaskModel createTask(UserModel user,
                                String name,
                                String description,
                                Date limitDate,
                                String status,
                                List<CategoryModel> categories) throws Exception {
        TaskModel task = new TaskModel();
        task.setUserId(user.getId());
        task.setName(name);
        task.setDescription(description == null ? "" : description);
        task.setStatus(status);
        task.setCreatedAt(new Date());
        task.setLimitDate(limitDate);
        task.setCategory(categories == null ? new ArrayList<>() : new ArrayList<>(categories));

        String id = taskDao.create(task);
        task.setId(id);
        session.addTask(task);
        return task;
    }

    public TaskModel updateTask(TaskModel existingTask,
                                String name,
                                String description,
                                Date limitDate,
                                String status,
                                List<CategoryModel> categories) throws Exception {
        if (existingTask == null || existingTask.getId() == null || existingTask.getId().isBlank()) {
            return null;
        }

        TaskModel updatedTask = new TaskModel();
        updatedTask.setId(existingTask.getId());
        updatedTask.setUserId(existingTask.getUserId());
        updatedTask.setCreatedAt(existingTask.getCreatedAt());
        updatedTask.setName(name);
        updatedTask.setDescription(description == null ? "" : description);
        updatedTask.setStatus(status);
        updatedTask.setLimitDate(limitDate);
        updatedTask.setCategory(categories == null ? new ArrayList<>() : new ArrayList<>(categories));

        taskDao.update(updatedTask);
        session.updateTask(updatedTask);
        return updatedTask;
    }

    public void deleteTask(TaskModel task) throws Exception {
        if (task == null || task.getId() == null || task.getId().isBlank()) {
            return;
        }
        taskDao.deleteById(task.getId());
        session.removeTaskById(task.getId());
    }

    public List<TaskModel> filterTasks(String query, String categoryFilter, String statusFilter) {
        List<TaskModel> filtered = new ArrayList<>();

        String q = query == null ? "" : query.trim().toLowerCase();
        String category = categoryFilter == null ? "Todas" : categoryFilter;
        String status = statusFilter == null ? "Todos" : statusFilter;

        for (TaskModel task : session.getTasks()) {
            if (!matchesQuery(task, q)) {
                continue;
            }
            if (!matchesCategory(task, category)) {
                continue;
            }
            if (!matchesStatus(task, status)) {
                continue;
            }
            filtered.add(task);
        }

        return filtered;
    }

    private boolean matchesQuery(TaskModel task, String query) {
        if (query.isBlank()) {
            return true;
        }
        String name = task.getName() == null ? "" : task.getName().toLowerCase();
        String description = task.getDescription() == null ? "" : task.getDescription().toLowerCase();
        return name.contains(query) || description.contains(query);
    }

    private boolean matchesCategory(TaskModel task, String categoryFilter) {
        if ("Todas".equalsIgnoreCase(categoryFilter)) {
            return true;
        }
        if (task.getCategory() == null) {
            return false;
        }
        for (CategoryModel category : task.getCategory()) {
            if (category != null && categoryFilter.equalsIgnoreCase(category.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesStatus(TaskModel task, String statusFilter) {
        if ("Todos".equalsIgnoreCase(statusFilter)) {
            return true;
        }
        return statusFilter.equalsIgnoreCase(task.getStatus());
    }
}
