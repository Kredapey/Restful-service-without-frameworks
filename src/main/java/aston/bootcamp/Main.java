package aston.bootcamp;

import aston.bootcamp.db.ConnectionManager;
import aston.bootcamp.db.impl.ConnectionManagerImpl;
import aston.bootcamp.utils.CreateSqlSchema;

public class Main {
    public static void main(String[] args) {
        ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
        CreateSqlSchema.initSchema(connectionManager);
    }
}
