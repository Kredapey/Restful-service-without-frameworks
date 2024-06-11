package aston.bootcamp.db.impl;

import aston.bootcamp.db.ConnectionManager;
import aston.bootcamp.db.connectionPool.ConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionManagerImpl implements ConnectionManager {
    private static final ConnectionPool connectionPool = ConnectionPool.getInstance();
    private static ConnectionManagerImpl instance;

    private ConnectionManagerImpl() {
    }

    public static synchronized ConnectionManagerImpl getInstance() {
        if (instance == null) {
            instance = new ConnectionManagerImpl();
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionPool.get();
    }
}
