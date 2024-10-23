package co.killionrevival.killioncommons.database;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

import co.killionrevival.killioncommons.database.models.DatabaseCredentials;
import co.killionrevival.killioncommons.util.ConfigUtil;
import co.killionrevival.killioncommons.util.IOUtil;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;

import com.google.gson.Gson;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
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

    @Getter
    private final ConsoleUtil logger;
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
        if (IOUtil.getPluginFile(plugin, "config.json") != null) {
            final ConfigUtil configUtil = new ConfigUtil(plugin);
            credentialFilePath = configUtil.getJsonMember("credentialsFilePath").getAsString();
        } else {
            credentialFilePath = "/home/container/plugins/PostgresCredentials/credentials.json";
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
            config.setMaximumPoolSize(10);
            config.setConnectionTimeout(30000);
            dataSource = new HikariDataSource(config);
            logger.sendInfo("Connected to Database!");
        } catch (Exception e) {
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
}