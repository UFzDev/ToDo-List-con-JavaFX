package ufzdev.todo_list.dao;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import ufzdev.todo_list.config.FirebaseConfig;
import ufzdev.todo_list.models.TaskModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskFirestoreDao implements TaskDao {
    private static final String COLLECTION = "tareas";
    private final Firestore db;

    public TaskFirestoreDao() {
        this.db = FirebaseConfig.getInstance().getFirestore();
    }

    @Override
    public List<TaskModel> findByUserId(String userId) throws Exception {
        if (userId == null || userId.isBlank()) {
            return new ArrayList<>();
        }

        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .get();

        List<TaskModel> tasks = new ArrayList<>();
        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            tasks.add(mapToTask(doc, userId));
        }
        return tasks;
    }

    @Override
    public String create(TaskModel taskModel) throws Exception {
        if (taskModel == null) {
            return null;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("userId", taskModel.getUserId());
        data.put("nombre", taskModel.getName());
        data.put("descripcion", taskModel.getDescription());
        data.put("estado", taskModel.getStatus());
        data.put("createdAt", taskModel.getCreatedAt() == null ? new Date() : taskModel.getCreatedAt());
        data.put("limitDate", taskModel.getLimitDate());

        return db.collection(COLLECTION).add(data).get().getId();
    }

    @Override
    public void updateStatus(String taskId, String newStatus) throws Exception {
        if (taskId == null || taskId.isBlank()) {
            return;
        }

        db.collection(COLLECTION).document(taskId).update("estado", newStatus, "status", newStatus).get();
    }

    @Override
    public void deleteById(String taskId) throws Exception {
        if (taskId == null || taskId.isBlank()) {
            return;
        }

        db.collection(COLLECTION).document(taskId).delete().get();
    }

    private TaskModel mapToTask(DocumentSnapshot doc, String userId) {
        TaskModel task = new TaskModel();
        task.setId(doc.getId());
        task.setUserId(userId);

        String name = firstNonBlank(doc.getString("nombre"), doc.getString("name"));
        task.setName(name == null ? "" : name);

        String description = firstNonBlank(doc.getString("descripcion"), doc.getString("description"));
        task.setDescription(description == null ? "" : description);

        String status = firstNonBlank(doc.getString("estado"), doc.getString("status"));
        task.setStatus(status == null ? "" : status);

        Date createdAt = doc.getDate("createdAt");
        Date limitDate = firstDate(doc.getDate("limitDate"), doc.getDate("fechaLimite"));
        task.setCreatedAt(createdAt);
        task.setLimitDate(limitDate);
        return task;
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }

    private Date firstDate(Date primary, Date fallback) {
        return primary != null ? primary : fallback;
    }
}
