package ufzdev.todo_list.services;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

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
}
