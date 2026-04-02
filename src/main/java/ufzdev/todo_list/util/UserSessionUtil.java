package ufzdev.todo_list.util;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import ufzdev.todo_list.config.FirebaseConfig;
import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.StatusModel;
import ufzdev.todo_list.models.TaskModel;
import ufzdev.todo_list.models.UserModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserSessionUtil {
    private static UserSessionUtil instance;
    private UserModel currentUserModel;
    private List<CategoryModel> categories = new ArrayList<>();
    private List<StatusModel> statuses = new ArrayList<>();
    private List<TaskModel> tasks = new ArrayList<>();

    private UserSessionUtil() {}

    public static synchronized UserSessionUtil getInstance() {
        if (instance == null) {
            instance = new UserSessionUtil();
        }
        return instance;
    }

    // Guarda todo el snapshot de sesión de una sola vez.
    public synchronized void setSessionData(UserModel userModel,
                                            List<CategoryModel> categories,
                                            List<StatusModel> statuses,
                                            List<TaskModel> tasks) {
        this.currentUserModel = userModel;
        this.categories = categories == null ? new ArrayList<>() : new ArrayList<>(categories);
        this.statuses = statuses == null ? new ArrayList<>() : new ArrayList<>(statuses);
        this.tasks = tasks == null ? new ArrayList<>() : new ArrayList<>(tasks);
    }

    // Carga catálogos y tareas una sola vez al iniciar sesión.
    public synchronized void loadSessionData(UserModel userModel) throws Exception {
        Firestore db = FirebaseConfig.getInstance().getFirestore();
        List<CategoryModel> loadedCategories = readCategories(db);
        List<StatusModel> loadedStatuses = readStatuses(db);
        List<TaskModel> loadedTasks = readTasks(db, userModel);
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

    private List<CategoryModel> readCategories(Firestore db) throws Exception {
        for (String collection : new String[] {"categorias", "categories"}) {
            QuerySnapshot snapshot = db.collection(collection).get().get();
            if (!snapshot.isEmpty()) {
                List<CategoryModel> result = new ArrayList<>();
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    CategoryModel item = new CategoryModel();
                    Long id = doc.getLong("id");
                    item.setId(id == null ? 0 : id.intValue());
                    String name = doc.getString("nombre");
                    if (name == null || name.isBlank()) {
                        name = doc.getString("name");
                    }
                    item.setName(name == null ? doc.getId() : name);
                    String desc = doc.getString("descripcion");
                    if (desc == null || desc.isBlank()) {
                        desc = doc.getString("description");
                    }
                    item.setDescription(desc == null ? "" : desc);
                    result.add(item);
                }
                return result;
            }
        }
        return new ArrayList<>();
    }

    private List<StatusModel> readStatuses(Firestore db) throws Exception {
        for (String collection : new String[] {"status", "statuses", "estados"}) {
            QuerySnapshot snapshot = db.collection(collection).get().get();
            if (!snapshot.isEmpty()) {
                List<StatusModel> result = new ArrayList<>();
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    StatusModel item = new StatusModel();
                    String id = doc.getString("id");
                    item.setId(id == null || id.isBlank() ? doc.getId() : id);
                    String name = doc.getString("nombre");
                    if (name == null || name.isBlank()) {
                        name = doc.getString("name");
                    }
                    item.setName(name == null ? item.getId() : name);
                    result.add(item);
                }
                return result;
            }
        }
        return new ArrayList<>();
    }

    private List<TaskModel> readTasks(Firestore db, UserModel userModel) throws Exception {
        if (userModel == null || userModel.getId() == null || userModel.getId().isBlank()) {
            return new ArrayList<>();
        }

        for (String collection : new String[] {"tareas", "tasks"}) {
            QuerySnapshot snapshot = db.collection(collection)
                    .whereEqualTo("userId", userModel.getId())
                    .get()
                    .get();

            if (!snapshot.isEmpty()) {
                List<TaskModel> result = new ArrayList<>();
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    TaskModel task = new TaskModel();
                    task.setId(doc.getId());
                    task.setUserId(userModel.getId());

                    String name = doc.getString("nombre");
                    if (name == null || name.isBlank()) {
                        name = doc.getString("name");
                    }
                    task.setName(name == null ? "" : name);

                    String description = doc.getString("descripcion");
                    if (description == null || description.isBlank()) {
                        description = doc.getString("description");
                    }
                    task.setDescription(description == null ? "" : description);

                    String status = doc.getString("estado");
                    if (status == null || status.isBlank()) {
                        status = doc.getString("status");
                    }
                    task.setStatus(status == null ? "" : status);

                    Date createdAt = doc.getDate("createdAt");
                    Date limitDate = doc.getDate("limitDate");
                    if (limitDate == null) {
                        limitDate = doc.getDate("fechaLimite");
                    }
                    task.setCreatedAt(createdAt);
                    task.setLimitDate(limitDate);
                    result.add(task);
                }
                return result;
            }
        }
        return new ArrayList<>();
    }
}