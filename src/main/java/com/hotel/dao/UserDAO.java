package com.hotel.dao;

import com.hotel.model.User;
import com.hotel.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UserDAO.java
 *
 * Data Access Object for the "users" table.
 * This is the ONLY class allowed to contain SQL for user authentication.
 * All queries use PreparedStatement to prevent SQL injection.
 *
 * NOTE: This class assumes a database initialization utility already exists
 * (per "Database initialization is complete" in project status) that exposes
 * a static Connection getter. Adjust the import/call in getConnection() below
 * if your existing utility class/method has a different name.
 */
public class UserDAO {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /**
     * Attempts to authenticate a user against the "users" table.
     *
     * @param username the username entered by the user
     * @param password the plain-text password entered by the user
     * @return a populated User object if credentials match, otherwise null
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT id, username, password, role FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapRowToUser(rs);
                    LOGGER.info("Authentication succeeded for username: " + username);
                    return user;
                } else {
                    LOGGER.warning("Authentication failed for username: " + username);
                    return null;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during authenticate() for username: " + username, e);
            return null;
        }
    }

    /**
     * Looks up a user by username only (no password check).
     * Useful for future features such as checking whether a username exists
     * before attempting authentication, or for admin lookups.
     *
     * @param username the username to search for
     * @return the matching User, or null if not found / on error
     */
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password, role FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during findByUsername() for username: " + username, e);
            return null;
        }
    }

    /**
     * Helper to map the current row of a ResultSet into a User object.
     * Keeps mapping logic in one place so it isn't duplicated across methods.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
        );
    }
}
