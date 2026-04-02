package ufzdev.todo_list.dao;

import ufzdev.todo_list.models.StatusModel;

import java.util.List;

public interface StatusDao {
    List<StatusModel> findAll() throws Exception;

    void create(StatusModel statusModel) throws Exception;

    void deleteByDocumentId(String documentId) throws Exception;
}
