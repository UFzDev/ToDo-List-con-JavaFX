package ufzdev.todo_list.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import ufzdev.todo_list.dao.UserDao;
import ufzdev.todo_list.dao.UserFirestoreDao;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.util.AlertsUtil;

public class UserService {
    private static final UserDao USER_DAO = new UserFirestoreDao();

    public static UserModel autenticate(UserModel userModel) {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userModel.getEmail());
            String uid = userRecord.getUid();

            UserModel userInDb = USER_DAO.findById(uid);
            if (userInDb == null) {
                throw new Exception("Credenciales incorrectas");
            }

            if (userModel.getPassword().equals(userInDb.getPassword())) {
                userInDb.setEmail(userModel.getEmail());
                return userInDb;
            }

            throw new Exception("Credenciales incorrectas");
        } catch (Exception e) {
            AlertsUtil.showError("Error de autenticacion", "No se pudo autenticar. Verifique sus credenciales.");
            System.out.println("Error durante la autenticacion: " + e.getMessage());
        }
        return null;
    }

    public static UserModel loginTest() {
        UserModel testUserModel = new UserModel();
        testUserModel.setEmail("test@test.com");
        testUserModel.setPassword("123456");
        testUserModel.setHasSettings(false);
        return autenticate(testUserModel);
    }

    public static void completeSettings(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }
        try {
            USER_DAO.updateHasSettings(userId, true);
        } catch (Exception e) {
            AlertsUtil.showError("Error al guardar configuracion", "No se pudo actualizar el estado de configuracion.");
            System.out.println("Error al completar settings: " + e.getMessage());
        }
    }

    public static void registerUser(UserModel userModel) throws Exception {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(userModel.getEmail())
                .setPassword(userModel.getPassword())
                .setDisplayName(userModel.getName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        USER_DAO.create(uid, userModel);
    }
}
