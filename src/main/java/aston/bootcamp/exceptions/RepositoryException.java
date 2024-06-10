package aston.bootcamp.exceptions;

import java.sql.SQLException;

public class RepositoryException extends RuntimeException {
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(SQLException cause) {
        super(cause);
    }
}
