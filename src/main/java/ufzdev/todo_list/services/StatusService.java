package ufzdev.todo_list.services;

import ufzdev.todo_list.dao.StatusDao;
import ufzdev.todo_list.dao.StatusFirestoreDao;
import ufzdev.todo_list.models.StatusModel;
import ufzdev.todo_list.util.UserSessionUtil;

import java.util.ArrayList;
import java.util.List;

public class StatusService {
    private final StatusDao statusDao = new StatusFirestoreDao();
    private final UserSessionUtil session = UserSessionUtil.getInstance();

    public List<StatusModel> getSessionStatuses() {
        return new ArrayList<>(session.getStatuses());
    }

    public StatusModel createStatus(String name) throws Exception {
        StatusModel status = new StatusModel();
        status.setName(name);

        statusDao.create(status);
        session.addStatus(status);
        return status;
    }

    public void deleteStatusByName(String name) throws Exception {
        statusDao.deleteByName(name);
        session.removeStatusByName(name);
    }
}

