package com.hotel.service;

import com.hotel.dao.UserDAO;
import com.hotel.model.User;

import java.util.logging.Logger;

/**
 * AuthService.java
 *
 * Business/service layer for authentication.
 * This class contains NO SQL — it delegates all persistence work to UserDAO
 * and focuses purely on business rules (e.g. rejecting blank input before
 * ever touching the database).
 *
 * The controller talks to this class, never directly to UserDAO.
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Validates input and attempts to log the user in.
     *
     * @param username entered username
     * @param password entered password
     * @return the authenticated User, or null if login fails or input is invalid
     */
    public User login(String username, String password) {
        // Basic business-rule validation lives here, not in the controller or DAO.
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            LOGGER.warning("Login attempt rejected: empty username or password.");
            return null;
        }

        User authenticatedUser = userDAO.authenticate(username.trim(), password);

        if (authenticatedUser != null) {
            LOGGER.info("User '" + username + "' logged in successfully with role: " + authenticatedUser.getRole());
        }

        return authenticatedUser;
    }
}
