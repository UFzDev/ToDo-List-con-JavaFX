package ufzdev.todo_list.services;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

import java.util.HashMap;
import java.util.Map;

public class AuthService {
    public static boolean autenticate(String email, String password) throws Exception {
        try {
            // Verificamos que el usuario existe en Firebase Auth
            UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);

            // Buscamos el documento del usuario en Firestore para validar la contraseña
            Firestore db = FirestoreClient.getFirestore();
            var docRef = db.collection("usuarios").document(userRecord.getUid()).get().get();

            if (docRef.exists()) {
                String passDb = docRef.getString("password");
                return password.equals(passDb); // True si la contraseña es correcta
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static void registerUser(String name, String username, String email, String password) throws Exception {
        // Crear el usuario en Firebase Authentication
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(name);

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();

        // Crear tambien en Firestore para que funcionen las validaciones de autenticación
        Firestore db = FirestoreClient.getFirestore();
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", name);
        userData.put("usuario", username);
        userData.put("correo", email);
        userData.put("password", password);

        // Guardamos usando el UID como nombre del documento
        db.collection("usuarios").document(uid).set(userData).get();
    }
}
