package com.SunriseDental.Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton Pattern: ensures only one shared DB connection instance
 * is used across the whole application, avoiding repeated connection
 * creation and giving consistent access to all DAO classes.
 *
 * The connection is validated (and transparently reconnected) on every
 * request, so a temporary MySQL outage does not permanently "poison"
 * the singleton for the lifetime of the web application.
 *
 * Driver: MariaDB Connector/J (org.mariadb.jdbc.Driver). It speaks the
 * same MySQL wire protocol, so it connects to a normal MySQL server
 * just fine using the jdbc:mariadb:// URL scheme below — no separate
 * MariaDB server is required.
 */
public class DBConnection {

    private static DBConnection instance;
    private Connection connection;

    // NOTE: update these to match your local MySQL setup.
    private static final String URL =
            "jdbc:mariadb://localhost:3306/sunrise_dental?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private DBConnection() {
        connect();
    }

    private void connect() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "MariaDB/MySQL JDBC driver not found on the classpath. "
                            + "Make sure mariadb-java-client-*.jar is present in WEB-INF/lib.", e);
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not connect to MySQL at " + URL + " as user '" + DB_USER + "'. "
                            + "Check that: (1) MySQL is running, (2) the 'sunrise_dental' database "
                            + "exists (run database/schema.sql), and (3) DB_USER/DB_PASSWORD "
                            + "in DBConnection.java match your MySQL credentials. "
                            + "Original error: " + e.getMessage(), e);
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Returns a live connection, reconnecting first if the previous
     * connection is null, closed, or no longer valid (e.g. MySQL was
     * restarted or the connection timed out).
     */
    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                connect();
            }
        } catch (SQLException e) {
            connect();
        }
        return connection;
    }
}
