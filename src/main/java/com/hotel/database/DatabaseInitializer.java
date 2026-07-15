package com.hotel.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseInitializer is responsible for setting up the SQLite database
 * schema on application startup. It is idempotent: running it multiple
 * times (e.g., every app launch) will never duplicate tables or data.
 *
 * It relies on the existing DatabaseConnection class to obtain a JDBC
 * Connection to the SQLite database file.
 */
public class DatabaseInitializer {

    // SLF4J logger for this class. Used to record initialization events
    // and errors without hardcoding System.out.println calls.
    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);

    // Default admin credentials, inserted only once.
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    private static final String DEFAULT_ADMIN_ROLE = "ADMIN";

    /**
     * Entry point for database initialization. Call this once when the
     * application starts (e.g., from App.java).
     *
     * Behavior:
     * - If the database file does not exist yet, SQLite will create it
     *   automatically the moment a connection is opened (this happens
     *   inside DatabaseConnection). We simply log whether the file was
     *   present before we connected, for informational purposes.
     * - Tables are created only if they don't already exist (CREATE TABLE
     *   IF NOT EXISTS), so re-running this is always safe.
     * - The default admin user is inserted only if no admin row already
     *   exists (checked via a SELECT before the INSERT).
     */
    public static void initializeDatabase() {
        // We check for the file's existence BEFORE connecting, because
        // opening a Connection to a SQLite JDBC URL will silently create
        // the file if it's missing. Checking first lets us log accurately.
        File dbFile = new File("hotel.db");
        boolean dbAlreadyExisted = dbFile.exists();

        try (Connection connection = DatabaseConnection.getConnection()) {

            if (!dbAlreadyExisted) {
                logger.info("Database created: hotel.db did not exist and has been created.");
            } else {
                logger.info("Database already exists: hotel.db found, skipping creation step.");
            }

            // Create all required tables. Each method uses
            // CREATE TABLE IF NOT EXISTS, so calling them repeatedly
            // is harmless.
            createUsersTable(connection);
            createRoomsTable(connection);
            createCustomersTable(connection);
            createBookingsTable(connection);
            createPaymentsTable(connection);

            logger.info("Tables created (or already present).");

            // Insert the default admin account, but only if it doesn't
            // already exist.
            insertDefaultAdminIfMissing(connection);

        } catch (SQLException e) {
            // Any failure during initialization is logged with full
            // stack trace so it can be diagnosed. We do not rethrow as
            // a checked exception here because the caller (App.java)
            // just wants a best-effort startup step; the error is
            // still visible in the logs.
            logger.error("Error during database initialization: {}", e.getMessage(), e);
        }
    }

    /**
     * Creates the 'users' table if it does not already exist.
     *
     * Schema:
     * - id: primary key, auto-incremented by SQLite.
     * - username: must be unique and non-null (used for login lookups).
     * - password: stored as plain text per current spec (hashing can be
     *   added later in the Login module).
     * - role: e.g. "ADMIN", used for authorization checks later.
     */
    static void createUsersTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL" +
                ")";
        // Statement (not PreparedStatement) is used here because CREATE
        // TABLE statements contain no user-supplied values — there is
        // nothing to parameterize, and DDL statements are not typically
        // run through PreparedStatement.
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /**
     * Creates the 'rooms' table if it does not already exist.
     *
     * Schema:
     * - id: primary key, auto-incremented.
     * - room_number: unique identifier for the physical room (e.g. "101").
     * - room_type: e.g. "Single", "Double", "Suite".
     * - price: nightly rate, stored as REAL (floating point).
     * - status: e.g. "AVAILABLE", "OCCUPIED", "MAINTENANCE".
     */
    static void createRoomsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS rooms (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "room_number TEXT UNIQUE NOT NULL, " +
                "room_type TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "status TEXT NOT NULL" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /**
     * Creates the 'customers' table if it does not already exist.
     *
     * Schema:
     * - id: primary key, auto-incremented.
     * - name: required, the customer's full name.
     * - phone, email, address: optional contact details (nullable).
     */
    static void createCustomersTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS customers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "phone TEXT, " +
                "email TEXT, " +
                "address TEXT" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /**
     * Creates the 'bookings' table if it does not already exist.
     *
     * Schema:
     * - id: primary key, auto-incremented.
     * - customer_id: references customers(id) — who made the booking.
     * - room_id: references rooms(id) — which room was booked.
     * - check_in / check_out: booking date range (stored as DATE/TEXT
     *   in SQLite, which has no native date type).
     * - status: e.g. "CONFIRMED", "CANCELLED", "COMPLETED".
     *
     * Foreign keys are declared for referential documentation. Note:
     * SQLite does not enforce foreign keys unless
     * "PRAGMA foreign_keys = ON" is set on the connection; enforcement
     * can be added later if required.
     */
    static void createBookingsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER NOT NULL, " +
                "room_id INTEGER NOT NULL, " +
                "check_in DATE, " +
                "check_out DATE, " +
                "status TEXT, " +
                "FOREIGN KEY(customer_id) REFERENCES customers(id), " +
                "FOREIGN KEY(room_id) REFERENCES rooms(id)" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /**
     * Creates the 'payments' table if it does not already exist.
     *
     * Schema:
     * - id: primary key, auto-incremented.
     * - booking_id: references bookings(id) — which booking was paid for.
     * - amount: payment amount (REAL).
     * - payment_method: e.g. "CASH", "CARD", "UPI".
     * - payment_date: date the payment was made.
     */
    static void createPaymentsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS payments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "booking_id INTEGER NOT NULL, " +
                "amount REAL NOT NULL, " +
                "payment_method TEXT, " +
                "payment_date DATE, " +
                "FOREIGN KEY(booking_id) REFERENCES bookings(id)" +
                ")";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    /**
     * Inserts the default admin user, but only if a user with that
     * username does not already exist. This makes the operation safe
     * to run every time the application starts.
     *
     * Steps:
     * 1. SELECT COUNT(*) FROM users WHERE username = ? — checks
     *    existence using a PreparedStatement (never string
     *    concatenation) to avoid SQL injection.
     * 2. If count == 0, INSERT the admin row using another
     *    PreparedStatement with bound parameters.
     */
    private static void insertDefaultAdminIfMissing(Connection connection) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
            checkStatement.setString(1, DEFAULT_ADMIN_USERNAME);

            try (ResultSet resultSet = checkStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // Admin already exists — nothing to do.
                    logger.info("Already initialized: default admin user already present.");
                    return;
                }
            }
        }

        // No existing admin found — insert one now.
        String insertSql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
            insertStatement.setString(1, DEFAULT_ADMIN_USERNAME);
            insertStatement.setString(2, DEFAULT_ADMIN_PASSWORD);
            insertStatement.setString(3, DEFAULT_ADMIN_ROLE);
            insertStatement.executeUpdate();
            logger.info("Admin inserted: default admin user created (username='admin').");
        }
    }
}