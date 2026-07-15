package com.hotel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.hotel.util.AppConstants;
import com.hotel.database.DatabaseInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application entry point.
 *
 * At this stage, App is intentionally a bare-bones JavaFX launcher.
 * It exists only to prove the foundation (build, dependencies, package
 * structure) works end-to-end. It does NOT load FXML and does NOT
 * contain any business/UI logic. Real screens will be wired in through
 * controllers once feature development begins.
 */
public class App extends Application {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage primaryStage) {
        DatabaseInitializer.initializeDatabase();
        logger.info("Starting {}", AppConstants.APP_NAME);

        Label placeholder = new Label(AppConstants.APP_NAME + " - foundation build OK");
        StackPane root = new StackPane(placeholder);

        Scene scene = new Scene(root, AppConstants.DEFAULT_WINDOW_WIDTH, AppConstants.DEFAULT_WINDOW_HEIGHT);

        primaryStage.setTitle(AppConstants.APP_NAME);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
