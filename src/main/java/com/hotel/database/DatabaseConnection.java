package com.hotel.database;

import com.hotel.util.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Owns the single JDBC connection string / connection lifecycle for the
 * application's SQLite database.
 *
 * Responsibilities of this class (and ONLY this class):
 *   1. Know WHERE the .db file lives on disk.
 *   2. Know HOW to open a JDBC Connection to it.
 *   3. Provide that connection to DAO classes on request.
 *
 * This class deliberately does NOT:
 *   - Create tables (that belongs to a future schema/migration step)
 *   - Run queries (that is the DAO layer's job)
 *   - Contain business logic (that is the Service layer's job)
 *
 * Design choice: connection-per-request vs. a single shared connection.
 * SQLite is a file-based, single-process database and its JDBC driver
 * is not designed for a long-lived connection to be shared safely
 * across many threads. Opening a short-lived connection per DAO
 * operation (try-with-resources) is the safer, more standard pattern
 * for a desktop app and is what this class supports.
 */
public final class DatabaseConnection {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private DatabaseConnection() {
        // static-only utility class
    }

    /**
     * Resolves the absolute path to the SQLite database file.
     *
     * DEVELOPMENT vs PRODUCTION path strategy:
     * - During development (running via `mvn javafx:run` from the project
     *   root), we resolve the path relative to the current working
     *   directory into src/main/resources/database/hotel.db. This keeps
     *   the .db file visible and easy to inspect/reset while coding.
     * - In a PACKAGED application (a jar handed to an end user), the
     *   resources folder is bundled *inside* the jar and is not a
     *   writable filesystem location. At that point this method should
     *   be updated to resolve a path under the user's home/application
     *   data directory instead, e.g.:
     *       System.getProperty("user.home") + "/.hotelms/hotel.db"
     *   That change is intentionally NOT made yet — it belongs to a
     *   later "packaging/distribution" milestone, not the foundation.
     */
    public static Path resolveDatabasePath() {
        Path devPath = Path.of(
                "src", "main", "resources",
                AppConstants.DATABASE_FOLDER,
                AppConstants.DATABASE_FILE_NAME
        ).toAbsolutePath();

        File parentDir = devPath.getParent().toFile();
        if (!parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            logger.debug("Database directory created: {} -> {}", parentDir, created);
        }

        return devPath;
    }

    /**
     * Opens and returns a new JDBC connection to the SQLite database.
     * Callers are responsible for closing it (use try-with-resources).
     *
     * Example usage from a future DAO class:
     * <pre>
     *     try (Connection conn = DatabaseConnection.connect()) {
     *         // run a query
     *     }
     * </pre>
     */
    public static Connection connect() throws SQLException {
        String url = AppConstants.JDBC_URL_PREFIX + resolveDatabasePath();
        logger.debug("Opening SQLite connection: {}", url);
        return DriverManager.getConnection(url);
    }
}
