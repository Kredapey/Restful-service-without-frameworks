package aston.bootcamp.repository.impl;

import aston.bootcamp.db.ConnectionManager;
import aston.bootcamp.db.impl.ConnectionManagerImpl;
import aston.bootcamp.entity.Brand;
import aston.bootcamp.entity.Type;
import aston.bootcamp.exceptions.RepositoryException;
import aston.bootcamp.repository.DealershipRepository;
import aston.bootcamp.repository.TypeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeRepositoryImpl implements TypeRepository {
    private static final String SAVE_SQL = """
            INSERT INTO types(type)
            VALUES (?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE types SET type = ? WHERE id = ?;
            """;
    private static final String DELETE_BY_ID_SQL = """
            DELETE FROM types WHERE id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM types WHERE id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT * FROM types;
            """;
    private static final String EXISTS_BY_ID_SQL = """
            SELECT exists(
            SELECT 1 FROM types WHERE id = ?);
            """;

    private static TypeRepository instance;
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private TypeRepositoryImpl() {

    }
    public static synchronized TypeRepository getInstance() {
        if (instance == null) {
            instance = new TypeRepositoryImpl();
        }
        return instance;
    }
    private Type buildType(ResultSet resultSet, Type type) throws SQLException {
        return new Type(resultSet.getLong("id"), type.getType());
    }

    @Override
    public Type save(Type type) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, type.getType());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                type = buildType(resultSet, type);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return type;
    }

    @Override
    public void update(Type type) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, type.getType());
            statement.setLong(2, type.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            statement.setLong(1, id);
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return  result;
    }

    @Override
    public Optional<Type> findById(Long id) {
        Type result = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = createType(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Type> findAll() {
        List<Type> result = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createType(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return result;
    }

    @Override
    public boolean existById(Long id) {
        boolean result = false;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return result;
    }

    private Type createType(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String type = resultSet.getString("type");
        return new Type(id, type);
    }
}
