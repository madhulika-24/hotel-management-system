package com.hotel.controller;

import com.hotel.model.User;
import com.hotel.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * LoginController.java
 *
 * JavaFX controller bound to login.fxml.
 * Responsible ONLY for:
 *   - Reading input from the view
 *   - Basic UI-level validation (empty fields)
 *   - Delegating authentication to AuthService
 *   - Showing Alert dialogs with the result
 *
 * Contains NO database/SQL logic — that lives in UserDAO, reached through
 * AuthService.
 *
 * Dashboard loading is intentionally NOT implemented yet, per project scope.
 */
public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button exitButton;

    // Service layer handle — controller talks to service, never to DAO directly.
    private final AuthService authService = new AuthService();

    /**
     * Called automatically by JavaFX after the FXML fields are injected.
     * Currently no setup is needed, but the method is kept as the standard
     * place to initialize UI state if that becomes necessary later.
     */
    @FXML
    public void initialize() {
        LOGGER.info("LoginController initialized.");
    }

    /**
     * Handles the Login button click.
     * Reads the entered credentials, validates them, calls AuthService,
     * and shows an appropriate Alert dialog based on the result.
     */
    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // --- UI-level validation: empty field check ---
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Username and password cannot be empty.");
            return;
        }

        try {
            User user = authService.login(username, password);

            if (user != null) {
                // Login succeeded.
                System.out.println("Login Successful");
                System.out.println("Dashboard will be loaded later.");

                showAlert(Alert.AlertType.INFORMATION, "Login Successful",
                        "Welcome, " + user.getUsername() + " (" + user.getRole() + ").\n"
                                + "Dashboard will be loaded later.");

                // Dashboard loading intentionally NOT implemented yet.
            } else {
                // Either wrong password or unknown username — same message,
                // deliberately, so as not to reveal which one it was.
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }

        } catch (Exception e) {
            // Catch-all safety net so an unexpected error never crashes the UI silently.
            LOGGER.log(Level.SEVERE, "Unexpected error during login attempt.", e);
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred. Please try again.");
        }
    }

    /**
     * Handles the Exit button click — closes the application.
     */
    @FXML
    private void handleExitButtonAction(ActionEvent event) {
        LOGGER.info("Exit button pressed. Shutting down application.");
        System.exit(0);
    }

    /**
     * Small helper to reduce duplication when showing Alert dialogs.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
