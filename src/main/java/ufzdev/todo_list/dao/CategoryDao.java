package ufzdev.todo_list.dao;

import ufzdev.todo_list.models.CategoryModel;

import java.util.List;

public interface CategoryDao {
    List<CategoryModel> findAll() throws Exception;

    void create(CategoryModel categoryModel) throws Exception;

    void deleteByDocumentId(String documentId) throws Exception;
}
