package ufzdev.todo_list.dao;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import ufzdev.todo_list.config.FirebaseConfig;
import ufzdev.todo_list.models.StatusModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusFirestoreDao implements StatusDao {
    private static final String COLLECTION = "status";
    private final Firestore db;

    public StatusFirestoreDao() {
        this.db = FirebaseConfig.getInstance().getFirestore();
    }

    @Override
    public List<StatusModel> findAll() throws Exception {
        QuerySnapshot snapshot = db.collection(COLLECTION).get().get();
        List<StatusModel> statuses = new ArrayList<>();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            statuses.add(mapToStatus(doc));
        }

        return statuses;
    }

    @Override
    public void create(StatusModel statusModel) throws Exception {
        if (statusModel == null) {
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", statusModel.getId());
        data.put("nombre", statusModel.getName());

        db.collection(COLLECTION).add(data).get();
    }

    @Override
    public void deleteByDocumentId(String documentId) throws Exception {
        if (documentId == null || documentId.isBlank()) {
            return;
        }

        db.collection(COLLECTION).document(documentId).delete().get();
    }

    @Override
    public void deleteByName(String name) throws Exception {
        if (name == null || name.isBlank()) {
            return;
        }

        QuerySnapshot snapshot = db.collection(COLLECTION)
                .whereEqualTo("nombre", name)
                .get()
                .get();

        for (QueryDocumentSnapshot doc : snapshot.getDocuments()) {
            doc.getReference().delete().get();
        }
    }

    private StatusModel mapToStatus(DocumentSnapshot doc) {
        StatusModel item = new StatusModel();

        String id = doc.getString("id");
        item.setId(id == null || id.isBlank() ? doc.getId() : id);

        String name = firstNonBlank(doc.getString("nombre"), doc.getString("name"));
        item.setName(name == null ? item.getId() : name);
        return item;
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }
}
