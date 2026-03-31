package ufzdev.todo_list;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.google.cloud.firestore.Firestore;
import ufzdev.todo_list.util.FirebaseConfig;

import java.io.IOException;

public class Main extends Application {
    private Firestore firestore;

    @Override
    public void start(Stage stage) throws IOException {
        try {
            firestore = FirebaseConfig.initialize();
            String projectId = firestore.getOptions().getProjectId();
            System.out.println("Project ID: " + projectId);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 700);
        stage.setTitle("Login - ToDo List");
        //stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        if (firestore != null) {
            try {
                firestore.close();
                System.out.println("Firestore cerrado correctamente.");
            } catch (Exception e) {
                System.out.println("No se pudo cerrar Firestore: " + e.getMessage());
            }
        }
    }
}
