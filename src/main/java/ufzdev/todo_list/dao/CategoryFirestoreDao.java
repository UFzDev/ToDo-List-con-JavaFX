package ufzdev.todo_list.dao;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import ufzdev.todo_list.config.FirebaseConfig;
import ufzdev.todo_list.models.CategoryModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryFirestoreDao implements CategoryDao {
    private static final String COLLECTION = "categorias";
    private final Firestore db;

    public CategoryFirestoreDao() {
        this.db = FirebaseConfig.getInstance().getFirestore();
    }

    @Override
    public List<CategoryModel> findAll() throws Exception {
        QuerySnapshot snapshot = db.collection(COLLECTION).get().get();
        List<CategoryModel> categories = new ArrayList<>();

        for (DocumentSnapshot doc : snapshot.getDocuments()) {
            categories.add(mapToCategory(doc));
        }

        return categories;
    }

    @Override
    public void create(CategoryModel categoryModel) throws Exception {
        if (categoryModel == null) {
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", categoryModel.getId());
        data.put("nombre", categoryModel.getName());
        data.put("descripcion", categoryModel.getDescription());

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

    private CategoryModel mapToCategory(DocumentSnapshot doc) {
        CategoryModel item = new CategoryModel();
        Long id = doc.getLong("id");
        item.setId(id == null ? 0 : id.intValue());

        String name = firstNonBlank(doc.getString("nombre"), doc.getString("name"));
        item.setName(name == null ? doc.getId() : name);

        String description = firstNonBlank(doc.getString("descripcion"), doc.getString("description"));
        item.setDescription(description == null ? "" : description);
        return item;
    }

    private String firstNonBlank(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback;
    }
}
