package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseConnection - Singleton Pattern
 * Manages MySQL database connection using JDBC
 * Demonstrates OOP: Singleton Design Pattern
 */
public class DatabaseConnection {

    // ──────────────────────────────────────────────
    //  ★  DATABASE CONFIGURATION  ★
    //  Defaults work out of the box; override any value
    //  with an environment variable on another machine
    //  (no code change needed):
    //    DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD
    // ──────────────────────────────────────────────
    private static final String HOST     = envOrDefault("DB_HOST", "localhost");
    private static final String PORT     = envOrDefault("DB_PORT", "3307");
    private static final String DATABASE = envOrDefault("DB_NAME", "expense_manager");
    private static final String USERNAME = envOrDefault("DB_USER", "root");
    private static final String PASSWORD = envOrDefault("DB_PASSWORD", ""); // empty = no password

    private static String envOrDefault(String key, String fallback) {
        String val = System.getenv(key);
        return (val != null && !val.isEmpty()) ? val : fallback;
    }

    private static final String URL =
            "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // Singleton instance
    private static DatabaseConnection instance;
    private Connection connection;

    // Private constructor (Singleton)
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            ensureDatabaseExists();
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("[DB] Connected to MySQL successfully.");
            initializeTables();
        } catch (ClassNotFoundException e) {
            System.err.println("[DB] MySQL JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("MySQL Driver not found. Add mysql-connector-java.jar to classpath.", e);
        } catch (SQLException e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection failed. Check that MySQL is running, "
                + "database '" + DATABASE + "' exists, and DB_HOST/DB_PORT/DB_USER/DB_PASSWORD are correct.", e);
        }
    }

    // Singleton getInstance
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Return the raw JDBC connection
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Reconnection failed: " + e.getMessage());
        }
        return connection;
    }

    /**
     * Creates the database itself if it doesn't exist yet.
     * Needed on fresh machines where only the MySQL server is installed.
     * Tables are created separately in initializeTables().
     */
    private void ensureDatabaseExists() {
        String serverUrl = "jdbc:mysql://" + HOST + ":" + PORT
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try (Connection adminCon = DriverManager.getConnection(serverUrl, USERNAME, PASSWORD);
             Statement stmt = adminCon.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + DATABASE + "`");
            System.out.println("[DB] Database '" + DATABASE + "' is ready.");
        } catch (SQLException e) {
            // Not fatal here — the connect below will surface the real problem.
            System.err.println("[DB] Could not ensure database exists: " + e.getMessage());
        }
    }

    /**
     * Creates all required tables if they don't exist.
     * Run once at application startup.
     */
    private void initializeTables() {
        try (Statement stmt = connection.createStatement()) {

            // ── Users Table ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users (" +
                "  id       INT AUTO_INCREMENT PRIMARY KEY," +
                "  name     VARCHAR(100) NOT NULL," +
                "  email    VARCHAR(150) NOT NULL UNIQUE," +
                "  password VARCHAR(255) NOT NULL," +
                "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );

            // ── Categories Table ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS categories (" +
                "  id          INT AUTO_INCREMENT PRIMARY KEY," +
                "  name        VARCHAR(100) NOT NULL UNIQUE," +
                "  description VARCHAR(255)," +
                "  color_code  VARCHAR(10) DEFAULT '#607D8B'" +
                ")"
            );

            // ── Expenses Table ──
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS expenses (" +
                "  id          INT AUTO_INCREMENT PRIMARY KEY," +
                "  user_id     INT NOT NULL," +
                "  amount      DOUBLE NOT NULL," +
                "  description VARCHAR(255)," +
                "  category_id INT NOT NULL," +
                "  date        DATE NOT NULL," +
                "  shop        VARCHAR(150)," +
                "  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "  FOREIGN KEY (user_id)     REFERENCES users(id)     ON DELETE CASCADE," +
                "  FOREIGN KEY (category_id) REFERENCES categories(id)" +
                ")"
            );

            // ── Seed Default Categories ──
            seedDefaultCategories(stmt);

            System.out.println("[DB] Tables initialized successfully.");

        } catch (SQLException e) {
            System.err.println("[DB] Table initialization error: " + e.getMessage());
        }
    }

    private void seedDefaultCategories(Statement stmt) throws SQLException {
        String[][] defaults = {
            {"Food & Dining",    "Restaurants, groceries, snacks",        "#E53935"},
            {"Transportation",   "Fuel, taxi, bus, train",                "#1E88E5"},
            {"Shopping",         "Clothing, electronics, household items", "#8E24AA"},
            {"Entertainment",    "Movies, games, subscriptions",           "#FB8C00"},
            {"Health & Medical", "Doctor, medicines, gym",                 "#43A047"},
            {"Utilities",        "Electricity, water, internet, gas",      "#00ACC1"},
            {"Education",        "Books, courses, tuition",                "#F4511E"},
            {"Travel",           "Hotels, flights, vacations",             "#3949AB"},
            {"Others",           "Miscellaneous expenses",                 "#607D8B"}
        };

        for (String[] cat : defaults) {
            stmt.executeUpdate(
                "INSERT IGNORE INTO categories (name, description, color_code) VALUES ('"
                + cat[0] + "', '" + cat[1] + "', '" + cat[2] + "')"
            );
        }
    }

    // Close the connection (call on app exit)
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
