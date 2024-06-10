package aston.bootcamp.repository.impl;

import aston.bootcamp.db.ConnectionManager;
import aston.bootcamp.db.impl.ConnectionManagerImpl;
import aston.bootcamp.entity.Brand;
import aston.bootcamp.exceptions.RepositoryException;
import aston.bootcamp.repository.BrandRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BrandRepositoryImpl implements BrandRepository {
    private static final String SAVE_SQL = """
            INSERT INTO brands(brand)
            VALUES (?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE brands SET brand = ? WHERE id = ?;
            """;
    private static final String DELETE_BY_ID_SQL = """
            DELETE FROM brands WHERE id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM brands WHERE id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT * FROM brands;
            """;
    private static final String EXISTS_BY_ID_SQL = """
            SELECT exists(
            SELECT 1 FROM brands WHERE id = ?);
            """;
    private static BrandRepository instance;
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private BrandRepositoryImpl() {

    }

    public static synchronized BrandRepository getInstance() {
        if (instance == null) {
            instance = new BrandRepositoryImpl();
        }
        return instance;
    }

    private Brand buildBrand(ResultSet resultSet, Brand brand) throws SQLException {
        return new Brand(resultSet.getLong("id"), brand.getBrand());
    }

    @Override
    public Brand save(Brand brand) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, brand.getBrand());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                brand = buildBrand(resultSet, brand);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return brand;
    }

    @Override
    public void update(Brand brand) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, brand.getBrand());
            statement.setLong(2, brand.getId());
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
    public Optional<Brand> findById(Long id) {
        Brand result = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = createBrand(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Brand> findAll() {
        List<Brand> result = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createBrand(resultSet));
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

    private Brand createBrand(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String brand = resultSet.getString("brand");
        return new Brand(id, brand);
    }
}
