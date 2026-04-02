package ufzdev.todo_list.dao;

import ufzdev.todo_list.models.StatusModel;

import java.util.List;

public interface StatusDao {
    List<StatusModel> findByUserId(String userId) throws Exception;

    void create(StatusModel statusModel) throws Exception;

    void deleteByUserIdAndName(String userId, String name) throws Exception;
}
