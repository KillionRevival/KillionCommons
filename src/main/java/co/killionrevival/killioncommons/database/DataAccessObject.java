package co.killionrevival.killioncommons.database;

import co.killionrevival.killioncommons.database.models.ReturnCode;
import co.killionrevival.killioncommons.util.console.ConsoleUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class DataAccessObject<T> {
    private final DatabaseConnection db;
    private final ConsoleUtil logger;

    public DataAccessObject(
            DatabaseConnection connection
    ) {
        this.db = connection;
        this.logger = db.getLogger();
    }

    /**
     * Execute a query that doesn't expect any parameters
     * i.e. creating tables, schemas, etc
     *
     * @param query The query to run
     * @throws Exception
     */
    protected void executeQuery(String query) throws Exception {
        try(final Connection connection = db.getConnection()) {
            connection.setAutoCommit(false);
            try(final PreparedStatement statement = connection.prepareStatement(query)) {
                statement.execute();
                connection.commit();
            }
            catch (SQLException e) {
                connection.rollback();
                logger.sendThrowable(e);
                throw new Exception("executeQuery failed!");
            }
        } catch (SQLException e) {
            logger.sendThrowable(e);
            throw new Exception("Connecting to database failed!");
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
        try(final Connection connection = db.getConnection()) {
            connection.setAutoCommit(false);
            try(final PreparedStatement statement = connection.prepareStatement(query)) {
                if (params != null && params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        statement.setObject(i + 1, params[i]);
                    }
                }
                statement.executeUpdate();
                connection.commit();
            }
            catch (SQLException e) {
                connection.rollback();
                logger.sendThrowable(e);
                throw new Exception("executeUpdate failed!");
            }
        } catch (SQLException e) {
            logger.sendThrowable(e);
            throw new Exception("Connecting to database failed!");
        }
    }

    /**
     * Execute a batch update against the given query, supply this function with a mapper that should provide
     * the prepared statement with all of the parameters and call addBatch.
     * @param query Query to execute
     * @param mapper Function to prepare the statement
     * @throws Exception
     */
    protected void executeBatchUpdate(String query, Consumer<PreparedStatement> mapper) throws Exception {
        try(final Connection connection = db.getConnection()) {
            connection.setAutoCommit(false);
            try(final PreparedStatement statement = connection.prepareStatement(query)) {
                mapper.accept(statement);
                statement.executeBatch();
                connection.commit();
            }
            catch (SQLException e) {
                connection.rollback();
                logger.sendThrowable(e);
                throw new Exception("executeBatchUpdate failed!");
            }
        }
        catch (SQLException e) {
            logger.sendThrowable(e);
            throw new Exception("Connecting to database failed!");
        }
    }

    /**
     * Execute an insert that expects parameters and returns a generated key
     * @param query  The insert statement to run
     * @param params A list of params, '?' will be replaced with the parameters
     * @throws Exception
     */
    protected <X> X insertAndReturnKey(String query, Class<X> keyClass, Object... params) throws Exception {
        try(final Connection connection = db.getConnection()) {
            connection.setAutoCommit(false);
            connection.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
            try(final PreparedStatement statement = connection.prepareStatement(query)) {
                if (params != null && params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        statement.setObject(i + 1, params[i]);
                    }
                }
                statement.executeQuery();
                connection.commit();
                return parseKeyResultSet(statement.getResultSet(), keyClass);
            }
            catch (SQLException e) {
                connection.rollback();
                logger.sendThrowable(e);
                throw new Exception("executeUpdate failed!");
            }
        } catch (SQLException e) {
            logger.sendThrowable(e);
            throw new Exception("Connecting to database failed!");
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
    protected List<T> fetchQuery(String query, Object... params) throws Exception {
        try(final Connection connection = db.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(query)) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
                return parse(statement.executeQuery());
            }
            catch (SQLException e) {
                logger.sendThrowable(e);
                throw new Exception("fetchQuery failed!");
            }
        } catch (SQLException e) {
            logger.sendThrowable(e);
            throw new Exception("Connecting to database failed!");
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
        logger.sendDebug("Create schema query: " + query);
        try {
            this.executeQuery(query);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            logger.sendError("Failed to create schema: " + schemaName, e);
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
        String fieldStr = Arrays.stream(fields).map(field -> "'" + field + "'").collect(Collectors.joining(","));
        String query = "DO $$ BEGIN CREATE TYPE " + schemaName + "." + name + " AS ENUM (" + fieldStr
                + "); EXCEPTION WHEN duplicate_object THEN NULL; END $$;";
        logger.sendDebug("Create enum query: " + query);
        try {
            this.executeQuery(query);
            return ReturnCode.SUCCESS;
        } catch (Exception e) {
            logger.sendError("Failed to create enum: " + name, e);
        }
        return ReturnCode.FAILURE;
    }

    /**
     * Closes a result set if it is not null.
     *
     * @param resultSet The result set to close
     */
    protected void closeResultSet(ResultSet resultSet) {
        if (resultSet == null) {
            logger.sendWarning("Result set is null, cannot close.");
            return;
        }

        try {
            resultSet.close();
        } catch (SQLException e) {
            logger.sendError("Failed to close result set", e);
        }
    }

    protected <G> G parseKeyResultSet(final ResultSet resultSet, final Class<G> keyClass) throws SQLException {
        if (resultSet != null && resultSet.next()) {
            return resultSet.getObject(1, keyClass);
        }
        
        return null;
    }

    abstract public List<T> parse(final ResultSet resultSet) throws SQLException;
}
