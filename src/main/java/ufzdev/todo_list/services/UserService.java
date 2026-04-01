package ufzdev.todo_list.services;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import ufzdev.todo_list.models.UserModel;
import ufzdev.todo_list.util.AlertsUtil;
import ufzdev.todo_list.config.FirebaseConfig;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    public static UserModel autenticate(UserModel userModel) throws Exception {
        try {
            // Verificamos que el usuario existe en Firebase Auth
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userModel.getEmail());
            String uid = userRecord.getUid();

            // Buscamos el documento del usuario en Firestore para validar la contraseña
            Firestore db = FirebaseConfig.getInstance().getFirestore();
            var docRef = db.collection("usuarios").document(uid).get().get();

            if (docRef.exists()) {
                String passDb = docRef.getString("password");

                // Validamos la contraseña que viene en el objeto
                if (userModel.getPassword().equals(passDb)) {
                    userModel.setId(uid);
                    userModel.setName(docRef.getString("nombre"));
                    userModel.setUsername(docRef.getString("usuario"));

                    // Retornamos el objeto completo para la sesión
                    return userModel;
                }
            }
            throw new Exception("Credenciales incorrectas");
        }catch (Exception e){
            AlertsUtil.showError("Error de autenticación", "No se pudo autenticar. Verifique sus credenciales.");
            System.out.println("Error durante la autenticación: " + e.getMessage());
        }
        return null;
    }

    public static UserModel loginTest() throws Exception {
        UserModel testUserModel = new UserModel();
        testUserModel.setEmail("test@test.com");
        testUserModel.setPassword("123456");
        return autenticate(testUserModel);
    }

    public static void registerUser(UserModel userModel) throws Exception {
        // Crear el usuario en Firebase Authentication
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(userModel.getEmail())
                .setPassword(userModel.getPassword())
                .setDisplayName(userModel.getName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        // Crear tambien en Firestore para que funcionen las validaciones de autenticación
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", userModel.getName());
        userData.put("usuario", userModel.getUsername());
        userData.put("correo", userModel.getEmail());
        userData.put("password", userModel.getPassword());

        // Guardamos usando el UID como nombre del documento
        db.collection("usuarios").document(uid).set(userData).get();
    }
}
