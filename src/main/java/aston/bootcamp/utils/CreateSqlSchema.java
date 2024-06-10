package aston.bootcamp.utils;

import aston.bootcamp.db.ConnectionManager;
import aston.bootcamp.exceptions.RepositoryException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateSqlSchema {
    private static final String SCHEMA = "sql/schema.sql";
    private static String schemaSql;

    static {
        initializeSQL();
    }

    private  CreateSqlSchema() {

    }

    public static void initSchema(ConnectionManager connectionManager) {
        try (Connection connection = connectionManager.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(schemaSql);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    private static void initializeSQL() {
        try (InputStream inputStream = CreateSqlSchema.class.getClassLoader()
                .getResourceAsStream(SCHEMA)) {
            schemaSql = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
