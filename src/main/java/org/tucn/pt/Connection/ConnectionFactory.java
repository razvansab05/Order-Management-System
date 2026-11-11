package org.tucn.pt.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Singleton factory class responsible for establishing and closing
 * connections to the PostgreSQL database.
 *
 * <p>No credentials are stored in the source code. The connection settings are
 * resolved in the following order:</p>
 * <ol>
 *     <li>Environment variables {@code ORDERSDB_URL}, {@code ORDERSDB_USER}
 *     and {@code ORDERSDB_PASSWORD}</li>
 *     <li>A {@code db.properties} file in the working directory, with the keys
 *     {@code db.url}, {@code db.user} and {@code db.password}
 *     (see {@code db.properties.example}; the real file is git-ignored)</li>
 *     <li>For the URL and the user only: the local defaults
 *     {@code jdbc:postgresql://localhost:5432/ordersdb} and {@code postgres}</li>
 * </ol>
 * The password has no default value and must always be provided.
 */
public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
    private static final String DRIVER = "org.postgresql.Driver";

    private static final String CONFIG_FILE = "db.properties";
    private static final String ENV_URL = "ORDERSDB_URL";
    private static final String ENV_USER = "ORDERSDB_USER";
    private static final String ENV_PASSWORD = "ORDERSDB_PASSWORD";

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/ordersdb";
    private static final String DEFAULT_USER = "postgres";

    private static ConnectionFactory singleInstance = new ConnectionFactory();

    private final String dbUrl;
    private final String user;
    private final String password;

    private ConnectionFactory(){
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Properties fileProperties = loadPropertiesFile();
        this.dbUrl = resolve(ENV_URL, fileProperties.getProperty("db.url"), DEFAULT_URL);
        this.user = resolve(ENV_USER, fileProperties.getProperty("db.user"), DEFAULT_USER);
        this.password = resolve(ENV_PASSWORD, fileProperties.getProperty("db.password"), null);
    }

    /**
     * Reads the optional {@code db.properties} file from the working directory.
     *
     * @return The loaded properties, or an empty set if the file does not exist or cannot be read.
     */
    private static Properties loadPropertiesFile(){
        Properties properties = new Properties();
        Path path = Paths.get(CONFIG_FILE);
        if (Files.isRegularFile(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                properties.load(in);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not read " + CONFIG_FILE + ": " + e.getMessage());
            }
        }
        return properties;
    }

    /**
     * Picks a setting from the environment first, then from the properties file, then the fallback.
     */
    private static String resolve(String envName, String fileValue, String fallback){
        String envValue = System.getenv(envName);
        if (envValue != null) {
            return envValue;
        }
        if (fileValue != null) {
            return fileValue.trim();
        }
        return fallback;
    }

    /**
     * Creates a new active connection to the PostgreSQL database.
     *
     * @return A new Connection object.
     * @throws IllegalStateException if no database password has been configured.
     */
    private Connection createConnection(){
        if (password == null) {
            throw new IllegalStateException(
                    "Database password is not configured. Set the " + ENV_PASSWORD
                            + " environment variable or create a " + CONFIG_FILE
                            + " file (see db.properties.example).");
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, user, password);
        } catch (SQLException e){
            LOGGER.log(Level.WARNING, "An error occurred while trying to connect to the database");
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Retrieves an active database connection from the Singleton instance.
     *
     * @return A Connection object to the database.
     */
    public static Connection getConnection() { return singleInstance.createConnection();}

    /**
     * Safely closes the provided database connection.
     *
     * @param connection The connection to close.
     */
    public static void close(Connection connection){
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e){
                LOGGER.log(Level.WARNING, "An error occurred while trying to close the connection");
            }
        }
    }

    /**
     * Safely closes the provided database Statement.
     *
     * @param statement The SQL statement to close.
     */
    public static void close(Statement statement){
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e){
                LOGGER.log(Level.WARNING, "An error occurred while trying to close the statement");
            }
        }
    }

    /**
     * Safely closes the provided database ResultSet.
     *
     * @param resultSet The SQL result set to close.
     */
    public static void close(ResultSet resultSet){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e){
                LOGGER.log(Level.WARNING, "An error occurred while trying to close the resultSet");
            }
        }
    }
}
