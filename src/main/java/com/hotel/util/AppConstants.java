package com.hotel.util;

/**
 * Centralized, immutable application-wide constants.
 *
 * Why this class exists:
 * Hardcoding strings like "hotel.db" or magic numbers like window
 * dimensions directly inside controllers/services is a common beginner
 * mistake. It leads to:
 *   - Typos causing silent bugs (e.g. "Hotel.db" vs "hotel.db")
 *   - Painful, error-prone find-and-replace refactors
 *   - No single place to see "what does this app depend on?"
 *
 * By centralizing these values here, every class that needs a shared
 * constant imports THIS class instead of redefining or hardcoding the
 * value locally.
 *
 * This class holds only simple, static, unchanging values. It is NOT a
 * configuration system (no reading of external .properties/.yaml files
 * yet) and it is NOT a place for business logic.
 */
public final class AppConstants {

    // Private constructor: this is a static-only utility class and
    // should never be instantiated.
    private AppConstants() {
    }

    // ---------------------------------------------------------------
    // Application metadata
    // ---------------------------------------------------------------
    public static final String APP_NAME = "Hotel Management System";
    public static final String APP_VERSION = "1.0.0-SNAPSHOT";

    // ---------------------------------------------------------------
    // Window defaults
    // ---------------------------------------------------------------
    public static final double DEFAULT_WINDOW_WIDTH = 1024;
    public static final double DEFAULT_WINDOW_HEIGHT = 720;

    // ---------------------------------------------------------------
    // Database
    // ---------------------------------------------------------------
    // The database file lives under src/main/resources/database during
    // development so it is easy to find, version-control-ignore, and
    // reset. See DatabaseConnection.java for how this path is resolved
    // at runtime and why it will change for a packaged/installed app.
    public static final String DATABASE_FILE_NAME = "hotel.db";
    public static final String DATABASE_FOLDER = "database";
    public static final String JDBC_URL_PREFIX = "jdbc:sqlite:";
}
