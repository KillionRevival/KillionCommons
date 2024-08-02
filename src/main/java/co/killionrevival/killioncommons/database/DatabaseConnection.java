package co.killionrevival.killioncommons.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.killionrevival.killioncommons.database.models.DatabaseCredentials;
import co.killionrevival.killioncommons.database.models.ReturnCode;
import co.killionrevival.killioncommons.util.ConsoleUtil;

/**
 * Contains all of the methods required for connecting to and running queries
 * against the postgres database.
 * 
 * Must be inherited by another class to use!
 */
public abstract class DatabaseConnection {
    private final String credentialFilePath = "/home/container/plugins/PostgresCredentials/credentials.json";
    private final DatabaseCredentials credentials;
    private final String url;

    private final ObjectMapper objectMapper;

    private Connection connection = null;
    private ConsoleUtil logger;

    /**
     * @param logger Instance of the ConsoleUtil
     */
    protected DatabaseConnection(ConsoleUtil logger) {
        this.logger = logger;
        this.objectMapper = new ObjectMapper();

        this.credentials = this.getCredentials();
        this.url = String.format("jdbc:postgresql://%s:%d/%s", credentials.getIp(), credentials.getPort(),
                credentials.getDatabase());
        this.connection = this.createConnection();
        if (this.connection == null) {
            logger.sendDebug("Connection is null");
        }
    }

    /**
     * Get database credientials from the credentials file
     * 
     * @return DatabaseCredentials
     */
    private DatabaseCredentials getCredentials() {
        if (Files.exists(Paths.get(this.credentialFilePath))) {
            try {
                return objectMapper.readValue(new File(this.credentialFilePath), DatabaseCredentials.class);
            } catch (IOException e) {
                logger.sendError(e.getMessage());
            }
        } else {
            logger.sendError("Credentials file does not exist at " + this.credentialFilePath);
            System.out.println("file not found");
        }
        return null;
    }

    /**
     * Creates a connection to the database
     * 
     * @return Connection - Connection to the database
     */
    private Connection createConnection() {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(this.url, this.credentials.getUsername(),
                        this.credentials.getPassword());
                logger.sendInfo("Connected to Database!");
            } catch (SQLException e) {
                logger.sendError("ERROR: " + e.getMessage());
                throw new RuntimeException("Error connecting to the database", e);
            } catch (ClassNotFoundException e) {
                logger.sendError("ERROR: " + e.getMessage());
                throw new RuntimeException("Failed to find postgres driver", e);
            } catch (Exception e) {
                logger.sendError("ERROR: " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Closes the connection to the database
     * 
     * @throws Exception
     */
    public void closeConnection() throws Exception {
        try {
            if (this.connection != null) {
                this.connection.close();
                logger.sendInfo("Database Connection closed");
            }
        } catch (SQLException e) {
            throw new Exception("Failed to close DB connection");
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
        try {
            Statement stmt = this.connection.createStatement();
            stmt.execute(query);
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
        try {
            PreparedStatement pstmt = this.connection.prepareStatement(query);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    pstmt.setObject(i + 1, params[i]);
                }
            }
            pstmt.executeUpdate();
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
        ResultSet rs = null;
        try {
            PreparedStatement pstmt = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            logger.sendError(e.getMessage());
            throw new Exception("fetchQuery failed!");
        }
        return rs;
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