package aston.bootcamp.exceptions;

import java.sql.SQLException;

public class RepositoryException extends RuntimeException {

    public RepositoryException(SQLException cause) {
        super(cause);
    }
}
