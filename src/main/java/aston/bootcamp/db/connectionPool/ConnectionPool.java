package aston.bootcamp.db.connectionPool;


import aston.bootcamp.utils.PropertiesUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static final String POOL_NAME_KEY = "hikari.pool-name";
    private static final String URL_KEY = "db.url";
    private static final String MAX_POOL_SIZE_KEY = "hikari.max-pool-size";
    private static final String MIN_IDLE_KEY = "hikari.min-idle";
    private static final String USERNAME_KEY = "db.usr";
    private static final String PASSWORD_KEY = "db.pwd";
    private static final String SET_AUTOCOMMIT_KEY = "hikari.set-autocommit";
    private static final String DRIVER_KEY = "db.driver-class-name";

    private static final HikariConfig HIKARI_CONFIG = new HikariConfig();
    private static final HikariDataSource DATA_SOURCE;

    static {
        HIKARI_CONFIG.setJdbcUrl(PropertiesUtil.getProperties(URL_KEY));
        HIKARI_CONFIG.setPoolName(PropertiesUtil.getProperties(POOL_NAME_KEY));
        HIKARI_CONFIG.setUsername(PropertiesUtil.getProperties(USERNAME_KEY));
        HIKARI_CONFIG.setPassword(PropertiesUtil.getProperties(PASSWORD_KEY));
        HIKARI_CONFIG.setMaximumPoolSize(Integer.parseInt(PropertiesUtil.getProperties(MAX_POOL_SIZE_KEY)));
        HIKARI_CONFIG.setMinimumIdle(Integer.parseInt(PropertiesUtil.getProperties(MIN_IDLE_KEY)));
        HIKARI_CONFIG.setAutoCommit(Boolean.parseBoolean(PropertiesUtil.getProperties(SET_AUTOCOMMIT_KEY)));
        HIKARI_CONFIG.setDriverClassName(PropertiesUtil.getProperties(DRIVER_KEY));
        DATA_SOURCE = new HikariDataSource(HIKARI_CONFIG);
    }

    private ConnectionPool() {
    }


    public static Connection get() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}
