package ufzdev.todo_list.util;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import java.io.InputStream;

public class FirebaseConfig {
    public static Firestore initialize() {
        if (FirebaseApp.getApps().isEmpty()) {
            try (InputStream serviceAccount = FirebaseConfig.class.getClassLoader()
                    .getResourceAsStream("firebase-key.json")) {
                if (serviceAccount == null) {
                    throw new IllegalStateException("No se encontro firebase-key.json");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
            }catch (Exception e) {
                System.out.println("Error al inicializar Firebase: " + e.getMessage());
            }
        }

        return FirestoreClient.getFirestore();
    }
}
