package ufzdev.todo_list.services;

import ufzdev.todo_list.dao.CategoryDao;
import ufzdev.todo_list.dao.CategoryFirestoreDao;
import ufzdev.todo_list.models.CategoryModel;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.util.UserSessionUtil;

import java.util.ArrayList;
import java.util.List;

public class CategoryService {
    private final CategoryDao categoryDao = new CategoryFirestoreDao();
    private final UserSessionUtil session = UserSessionUtil.getInstance();

    public List<CategoryModel> getSessionCategories() {
        return new ArrayList<>(session.getCategories());
    }

    public CategoryModel createCategory(String name) throws Exception {
        UserModel user = session.getUser();
        if (user == null) {
            throw new Exception("Usuario no autenticado");
        }

        CategoryModel category = new CategoryModel();
        category.setName(name);
        category.setDescription("");
        category.setUserId(user.getId());

        categoryDao.create(category);
        session.addCategory(category);
        return category;
    }

    public void deleteCategoryByName(String name) throws Exception {
        UserModel user = session.getUser();
        if (user == null) {
            throw new Exception("Usuario no autenticado");
        }

        categoryDao.deleteByUserIdAndName(user.getId(), name);
        session.removeCategoryByName(name);
    }
}
