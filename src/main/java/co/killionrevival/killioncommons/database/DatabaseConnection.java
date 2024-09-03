package co.killionrevival.killioncommons.database;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import co.killionrevival.killioncommons.database.models.DatabaseCredentials;
import co.killionrevival.killioncommons.database.models.ReturnCode;
import co.killionrevival.killioncommons.util.ConfigUtil;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

/**
 * Contains all of the methods required for connecting to and running queries
 * against the postgres database.
 * 
 * Must be inherited by another class to use!
 */
public abstract class DatabaseConnection {
    private String credentialFilePath;
    private final DatabaseCredentials credentials;
    private final String url;

    private final Gson gson;

    private ConsoleUtil logger;
    private HikariDataSource dataSource;

    /**
     * @param logger Instance of the ConsoleUtil
     */
    protected DatabaseConnection(ConsoleUtil logger) {
        this.logger = logger;
        this.gson = new Gson();
        credentialFilePath = "/home/container/plugins/PostgresCredentials/credentials.json";
        this.credentials = this.getCredentials();
        this.url = String.format("jdbc:postgresql://%s:%d/%s", credentials.getIp(), credentials.getPort(),
                credentials.getDatabase());
        createConnection();
    }

    /**
     * @param logger Instance of the ConsoleUtil
     */
    protected DatabaseConnection(ConsoleUtil logger, Plugin plugin) {
        this.logger = logger;
        this.gson = new Gson();
        if (plugin.getResource("config.json") != null) {
            final ConfigUtil configUtil = new ConfigUtil(plugin);
            credentialFilePath = configUtil.getJsonMember("credentialsFilePath").getAsString();
        }
        this.credentials = this.getCredentials();
        this.url = String.format("jdbc:postgresql://%s:%d/%s", credentials.getIp(), credentials.getPort(),
                credentials.getDatabase());
        createConnection();
    }

    /**
     * Gets the DataSource associated with the current database connection.
     *
     * @return HikariDataSource The data source of the database connection.
     */
    protected HikariDataSource getDataSource() {
        return dataSource;
    }

    /**
     * Gets a Connection from the HikariDataSource.
     *
     * @return Connection The SQL connection object.
     * @throws SQLException if a database access error occurs or this method is called on a closed connection.
     */
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Get database credientials from the credentials file
     *
     * @return DatabaseCredentials
     */
    private DatabaseCredentials getCredentials() {
        if (Files.exists(Paths.get(this.credentialFilePath))) {
            final File credFile = new File(this.credentialFilePath);
            try (FileReader reader = new FileReader(credFile)) {
                return gson.fromJson(reader, DatabaseCredentials.class);
            } catch (IOException e) {
                logger.sendError(e.getMessage());
            }
        } else {
            logger.sendError(
                    "Credentials file does not exist at " + Paths.get(this.credentialFilePath).toAbsolutePath());
            System.out.println("file not found");
        }
        return null;
    }

    /**
     * Creates a connection to the database
     *
     * @return Connection - Connection to the database
     */
    private void createConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            final HikariConfig config = new HikariConfig();
            config.setJdbcUrl(this.url);
            config.setUsername(credentials.getUsername());
            config.setPassword(credentials.getPassword());
            config.setConnectionTestQuery("Select 1");
            dataSource = new HikariDataSource(config);
            logger.sendInfo("Connected to Database!");
        } catch (Exception e) {
            logger.sendError("ERROR: " + e.getMessage());
            logger.sendThrowable(e);
        }
    }

    /**
     * Closes the connection to the database
     */
    public void closeConnection() throws Exception {
        if (this.dataSource != null) {
            dataSource.close();
            logger.sendInfo("Database Connection closed");
        }
    }

    /**
     * Execute a query that doesn't expect any parameters
     * i.e. creating tables, schemas, etc
     *
     * @param query The query to run
     * @throws Exception
     */
    protected void executeQuery(String query) throws Exception {
        try (final PreparedStatement stmt = getConnection().prepareStatement(query)) {
            stmt.execute();
        } catch (SQLException e) {
            logger.sendError(e.getMessage());
            throw new Exception("executeQuery failed!");
        }
    }

    /**
     * Execute a query that expects parameters but does not return data
     * i.e. INSERT, UPDATE, DELETE
     *
     * @param query  The query to run
     * @param params A list of params, '?' will be replaced with the parameters
     * @throws Exception
     */
    protected void executeUpdate(String query, Object... params) throws Exception {
        try (final PreparedStatement p = getConnection().prepareStatement(query)) {
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    p.setObject(i + 1, params[i]);
                }
            }
            p.executeUpdate();
        } catch (SQLException e) {
            logger.sendError(e.getMessage());
            throw new Exception("executeUpdate failed!");
        }
    }

    /**
     * Execute an insert that expects parameters and returns a generated key*
     * @param query  The insert statement to run
     * @param params A list of params, '?' will be replaced with the parameters
     * @throws Exception
     */
    protected ResultSet insertAndReturnKey(String query, Object... params) throws Exception {
        try (final PreparedStatement p = getConnection().prepareStatement(query)) {
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    p.setObject(i + 1, params[i]);
                }
            }
            p.executeQuery();
            return p.getGeneratedKeys();
        } catch (SQLException e) {
            logger.sendError(e.getMessage());
            throw new Exception("executeUpdate failed!");
        }
    }

    /**
     * Execute a query that expects parameters and returns data
     * i.e. SELECT
     *
     * @param query  The query to run
     * @param params A list of params, '?' will be replaced with the parameters
     * @return ResultSet - The results of the query
     * @throws Exception
     */
    protected ResultSet fetchQuery(String query, Object... params) throws Exception {
        try (final PreparedStatement p = getConnection().prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                p.setObject(i + 1, params[i]);
            }
            return p.executeQuery();
        } catch (SQLException e) {
            logger.sendError(e.getMessage());
            throw new Exception("fetchQuery failed!");
        }
    }

    /**
     * Creates a schema if it does not already exist
     *
     * @param schemaName The name of the schema to create
     * @return ReturnCode - The status code of the result of the query
     */
    protected ReturnCode createSchemaIfNotExists(String schemaName) {
        String query = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
        try {
            this.executeQuery(query);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            logger.sendError(e.getMessage());
            logger.sendError("Failed to create schema: " + schemaName);
        }
        return ReturnCode.FAILURE;
    }

    /**
     * Creates an enum if it does not already exist
     *
     * @param schemaName The name of the schema to add the enum to
     * @param name       The name of the enum to crate
     * @param fields     An array of strings that are the contents of the enum
     * @return ReturnCode - The status code of the result of the query
     */
    protected ReturnCode createEnumIfNotExists(String schemaName, String name, String[] fields) {
        String fieldStr = String.join(",", fields);
        String query = "DO $$ BEGIN CREATE TYPE " + schemaName + "." + name + " AS (" + fieldStr
                + "); EXCEPTION WHEN duplicate_object THEN null; END $$;";
        try {
            this.executeQuery(query);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            logger.sendError(e.getMessage());
            logger.sendError("Failed to create enum: " + name);
        }
        return ReturnCode.FAILURE;
    }
}