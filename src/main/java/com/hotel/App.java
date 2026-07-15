package com.hotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * App.java
 *
 * JavaFX entry point.
 *
 * MODIFIED: start() now loads login.fxml as the initial screen instead of
 * whatever placeholder/previous scene it may have loaded before. Your
 * existing database initialization logic (called before/inside start, or in
 * main()) should be left exactly as-is — only the scene-loading portion
 * shown below needs to change in your real App.java.
 */
public class App extends Application {

    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            // NOTE: keep any existing DB initialization calls here, above this line,
            // exactly as they already are in your project.

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/hotel/view/login.fxml"));
            Parent root = loader.load();

            primaryStage.setTitle("Hotel Management System - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.setResizable(false);
            primaryStage.show();

            LOGGER.info("Application started, login.fxml loaded.");

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load login.fxml", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
