package ufzdev.todo_list.util;

import ufzdev.todo_list.dao.CategoryDao;
import ufzdev.todo_list.dao.CategoryFirestoreDao;
import ufzdev.todo_list.dao.StatusDao;
import ufzdev.todo_list.dao.StatusFirestoreDao;
import ufzdev.todo_list.dao.TaskDao;
import ufzdev.todo_list.dao.TaskFirestoreDao;
import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.StatusModel;
import ufzdev.todo_list.models.TaskModel;
import ufzdev.todo_list.models.UserModel;

import java.util.ArrayList;
import java.util.List;

public class UserSessionUtil {
    private static UserSessionUtil instance;
    private UserModel currentUserModel;
    private List<CategoryModel> categories = new ArrayList<>();
    private List<StatusModel> statuses = new ArrayList<>();
    private List<TaskModel> tasks = new ArrayList<>();

    private final CategoryDao categoryDao;
    private final StatusDao statusDao;
    private final TaskDao taskDao;

    private UserSessionUtil() {
        this.categoryDao = new CategoryFirestoreDao();
        this.statusDao = new StatusFirestoreDao();
        this.taskDao = new TaskFirestoreDao();
    }

    public static synchronized UserSessionUtil getInstance() {
        if (instance == null) {
            instance = new UserSessionUtil();
        }
        return instance;
    }

    // Guarda todo el snapshot de sesion de una sola vez.
    public synchronized void setSessionData(UserModel userModel,
                                            List<CategoryModel> categories,
                                            List<StatusModel> statuses,
                                            List<TaskModel> tasks) {
        this.currentUserModel = userModel;
        this.categories = categories == null ? new ArrayList<>() : new ArrayList<>(categories);
        this.statuses = statuses == null ? new ArrayList<>() : new ArrayList<>(statuses);
        this.tasks = tasks == null ? new ArrayList<>() : new ArrayList<>(tasks);
    }

    // Carga catalogos y tareas una sola vez al iniciar sesion.
    public synchronized void loadSessionData(UserModel userModel) throws Exception {
        List<CategoryModel> loadedCategories = categoryDao.findAll();
        List<StatusModel> loadedStatuses = statusDao.findAll();
        List<TaskModel> loadedTasks = userModel == null ? new ArrayList<>() : taskDao.findByUserId(userModel.getId());
        setSessionData(userModel, loadedCategories, loadedStatuses, loadedTasks);
    }

    public synchronized void setUser(UserModel userModel) {
        this.currentUserModel = userModel;
    }

    public synchronized UserModel getUser() {
        return currentUserModel;
    }

    public synchronized List<CategoryModel> getCategories() {
        return categories;
    }

    public synchronized List<StatusModel> getStatuses() {
        return statuses;
    }

    public synchronized List<TaskModel> getTasks() {
        return tasks;
    }

    public synchronized void addCategory(CategoryModel category) {
        if (category != null) {
            categories.add(category);
        }
    }

    public synchronized void addTask(TaskModel task) {
        if (task != null) {
            tasks.add(task);
        }
    }

    public synchronized boolean updateTaskStatus(String taskId, String newStatus) {
        if (taskId == null || taskId.isBlank()) {
            return false;
        }

        for (TaskModel task : tasks) {
            if (taskId.equals(task.getId())) {
                task.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    public synchronized void cleanSession() {
        currentUserModel = null;
        categories = new ArrayList<>();
        statuses = new ArrayList<>();
        tasks = new ArrayList<>();
    }
}