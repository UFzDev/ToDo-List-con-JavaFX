package ufzdev.todo_list.dao;

import ufzdev.todo_list.models.CategoryModel;

import java.util.List;

public interface CategoryDao {
    List<CategoryModel> findByUserId(String userId) throws Exception;

    void create(CategoryModel categoryModel) throws Exception;

    void deleteByUserIdAndName(String userId, String name) throws Exception;
}
