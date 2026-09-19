package com.example.projectoop.ui;

import com.example.projectoop.services.UserManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        UserManager userManager = new UserManager();
        AuthScreen authScreen   = new AuthScreen(primaryStage, userManager);

        Scene scene = new Scene(authScreen.getRoot(), 1100, 700);
        primaryStage.setTitle("Code Quality Fingerprinting Tool");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
