package com.hotel.model;

/**
 * User.java
 *
 * Plain model (POJO) representing a row in the "users" table.
 * Holds no business logic and no database code — it is purely a data carrier
 * passed between the DAO, service, and controller layers.
 */
public class User {

    private int id;
    private String username;
    private String password;
    private String role;

    /**
     * No-args constructor, useful when fields are set individually via setters.
     */
    public User() {
    }

    /**
     * Full constructor — typically used by UserDAO when mapping a ResultSet row
     * into a User object.
     */
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ----------------- Getters and Setters -----------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        // Password intentionally excluded from toString() to avoid accidentally
        // leaking credentials into logs.
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}
