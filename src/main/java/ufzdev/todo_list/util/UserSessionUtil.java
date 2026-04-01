package ufzdev.todo_list.util;

import ufzdev.todo_list.models.UserModel;

public class UserSessionUtil {
    private static UserSessionUtil instance;
    private UserModel currentUserModel;

    private UserSessionUtil() {}

    public static synchronized UserSessionUtil getInstance() {
        if (instance == null) {
            instance = new UserSessionUtil();
        }
        return instance;
    }

    // Guarda al usuario cuando hace login exitoso
    public void setUser(UserModel userModel) {
        this.currentUserModel = userModel;
    }

    // Devuelve al usuario desde cualquier pantalla
    public UserModel getUser() {
        return currentUserModel;
    }

    // Para cuando el usuario presione "Cerrar Sesión"
    public void cleanSession() {
        currentUserModel = null;
    }
}