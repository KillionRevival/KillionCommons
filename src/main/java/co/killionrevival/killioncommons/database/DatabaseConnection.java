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

import co.killionrevival.killioncommons.KillionCommons;
import co.killionrevival.killioncommons.database.models.DatabaseCredentials;
import co.killionrevival.killioncommons.util.ConsoleUtil;

public abstract class DatabaseConnection {
    private final String credentialFilePath = "/home/container/plugins/PostgresCredentials/credentials.json";
    private final DatabaseCredentials credentials;
    private final String url;

    private final ObjectMapper objectMapper;

    private Connection connection = null;
    private ConsoleUtil logger;

    protected DatabaseConnection() {
        this.logger = KillionCommons.getApi().getConsoleUtil();
        this.objectMapper = new ObjectMapper();

        this.credentials = this.getCredentials();
        this.url = String.format("jdbc:postgresql://%s:%d/%s", credentials.getIp(), credentials.getPort(),
                credentials.getDatabase());
        this.connection = this.createConnection();
    }

    private DatabaseCredentials getCredentials() {
        if (Files.exists(Paths.get(this.credentialFilePath))) {
            try {
                return objectMapper.readValue(new File(this.credentialFilePath), DatabaseCredentials.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.sendError("Credentials file does not exist at " + this.credentialFilePath);
            System.out.println("file not found");
        }
        return null;
    }

    private Connection createConnection() {
        if (connection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(this.url, this.credentials.getUsername(),
                        this.credentials.getPassword());
                logger.sendInfo("Connected to Database!");
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error connecting to the database", e);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

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

    protected void executeQuery(String query) throws Exception {
        try {
            Statement stmt = this.connection.createStatement();
            stmt.execute(query);
        } catch (SQLException e) {
            throw new Exception("executeQuery failed!");
        }
    }

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
            throw new Exception("executeUpdate failed!");
        }
    }

    protected ResultSet fetchQuery(String query, Object... params) throws Exception {
        ResultSet rs = null;
        try {
            PreparedStatement pstmt = this.connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            rs = pstmt.executeQuery();
        } catch (SQLException e) {
            throw new Exception("fetchQuery failed!");
        }
        return rs;
    }

    protected boolean createSchemaIfNotExists(String schemaName) {
        String query = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
        try {
            this.executeQuery(query);
            return true;
        } catch (Exception e) {
            logger.sendError("Failed to create schema: " + schemaName);
        }
        return false;
    }
}