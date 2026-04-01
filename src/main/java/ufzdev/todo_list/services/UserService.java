package ufzdev.todo_list.services;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import ufzdev.todo_list.models.User;
import ufzdev.todo_list.util.AlertUtils;
import ufzdev.todo_list.util.FirebaseConfig;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    public static User autenticate(User user) throws Exception {
        try {
            // Verificamos que el usuario existe en Firebase Auth
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(user.getEmail());
            String uid = userRecord.getUid();

            // Buscamos el documento del usuario en Firestore para validar la contraseña
            Firestore db = FirebaseConfig.getInstance().getFirestore();
            var docRef = db.collection("usuarios").document(uid).get().get();

            if (docRef.exists()) {
                String passDb = docRef.getString("password");

                // Validamos la contraseña que viene en el objeto
                if (user.getPassword().equals(passDb)) {
                    user.setId(uid);
                    user.setName(docRef.getString("nombre"));
                    user.setUsername(docRef.getString("usuario"));

                    // Retornamos el objeto completo para la sesión
                    return user;
                }
            }
            throw new Exception("Credenciales incorrectas");
        }catch (Exception e){
            AlertUtils.showError("Error de autenticación", "No se pudo autenticar. Verifique sus credenciales.");
            System.out.println("Error durante la autenticación: " + e.getMessage());
        }
        return null;
    }

    public static User loginTest() throws Exception {
        User testUser = new User();
        testUser.setEmail("test@test.com");
        testUser.setPassword("123456");
        return autenticate(testUser);
    }

    public static void registerUser(User user) throws Exception {
        // Crear el usuario en Firebase Authentication
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setDisplayName(user.getName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        // Crear tambien en Firestore para que funcionen las validaciones de autenticación
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", user.getName());
        userData.put("usuario", user.getUsername());
        userData.put("correo", user.getEmail());
        userData.put("password", user.getPassword());

        // Guardamos usando el UID como nombre del documento
        db.collection("usuarios").document(uid).set(userData).get();
    }
}
