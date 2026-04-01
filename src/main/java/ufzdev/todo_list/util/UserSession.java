package ufzdev.todo_list.util;

import ufzdev.todo_list.models.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // Guarda al usuario cuando hace login exitoso
    public void setUser(User user) {
        this.currentUser = user;
    }

    // Devuelve al usuario desde cualquier pantalla
    public User getUser() {
        return currentUser;
    }

    // Para cuando el usuario presione "Cerrar Sesión"
    public void cleanSession() {
        currentUser = null;
    }
}