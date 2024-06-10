package aston.bootcamp.repository.impl;

import aston.bootcamp.db.ConnectionManager;
import aston.bootcamp.db.impl.ConnectionManagerImpl;
import aston.bootcamp.entity.*;
import aston.bootcamp.exceptions.RepositoryException;
import aston.bootcamp.repository.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BikeToDealershipRepositoryImpl implements BikeToDealershipRepository {
    private static final String SAVE_SQL = """
            INSERT INTO dealerships_bikes(dealership_id, bike_id)
            VALUES (?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE dealerships_bikes
            SET dealership_id = ?, bike_id = ?
            WHERE id = ?;
            """;
    private static final String DELETE_BY_ID_SQL = """
            DELETE FROM dealerships_bikes
            WHERE id = ?;
            """;
    private static final String DELETE_BY_BIKE_ID_SQL = """
            DELETE FROM dealerships_bikes
            WHERE bike_id = ?;
            """;
    private static final String DELETE_BY_DEALERSHIP_ID_SQL = """
            DELETE FROM dealerships_bikes
            WHERE dealership_id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM dealerships_bikes
            WHERE id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT * FROM dealerships_bikes;
            """;
    private static final String FIND_ALL_BY_BIKE_ID_SQL = """
            SELECT * FROM dealerships_bikes
            WHERE bike_id = ?;
            """;
    private static final String FIND_ALL_BY_DEALERSHIP_ID_SQL = """
            SELECT * FROM dealerships_bikes
            WHERE dealership_id = ?;
            """;
    private static final String FIND_ALL_BIKES_BY_DEALERSHIP_ID_SQL = """
            SELECT bike_id FROM dealerships_bikes
            WHERE dealership_id = ?;
            """;
    private static final String FIND_ALL_DEALERSHIPS_BY_BIKE_ID_SQL = """
            SELECT dealership_id FROM dealerships_bikes
            WHERE bike_id = ?;
            """;
    private static final String FIND_BY_BIKE_ID_AND_DEPARTMENT_ID_SQL = """
            SELECT * FROM dealerships_bikes
            WHERE bike_id = ? AND dealership_id = ?;
            """;
    private static final String EXISTS_BY_ID = """
            SELECT exists(
            SELECT 1 FROM dealerships_bikes
            WHERE id = ?);
            """;

    private static BikeToDealershipRepository instance;
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static final DealershipRepository dealershipRepository = DealershipRepositoryImpl.getInstance();
    private static final BikeRepository bikeRepository = BikeRepositoryImpl.getInstance();
    private BikeToDealershipRepositoryImpl() {
    }

    public static synchronized BikeToDealershipRepository getInstance() {
        if (instance == null) {
            instance = new BikeToDealershipRepositoryImpl();
        }
        return instance;
    }

    @Override
    public BikeToDealership save(BikeToDealership bikeToDealership) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setBikeToDealershipParams(bikeToDealership, statement);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                bikeToDealership = buildBikeToDealership(bikeToDealership, resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return bikeToDealership;
    }

    private BikeToDealership buildBikeToDealership(BikeToDealership bikeToDealership, ResultSet resultSet) throws SQLException {
        return new BikeToDealership(resultSet.getLong("id"),
                bikeToDealership.getDealership(),
                bikeToDealership.getBike());
    }

    @Override
    public void update(BikeToDealership bikeToDealership) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            setBikeToDealershipParams(bikeToDealership, statement);
            statement.setLong(3, bikeToDealership.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    private void setBikeToDealershipParams(BikeToDealership bikeToDealership, PreparedStatement statement) throws SQLException {
        if (bikeToDealership.getDealership() == null) {
            statement.setNull(1, Types.NULL);
        } else {
            statement.setLong(1, bikeToDealership.getDealership().getId());
        }
        if (bikeToDealership.getBike() == null) {
            statement.setNull(2, Types.NULL);
        } else {
            statement.setLong(2, bikeToDealership.getBike().getId());
        }
    }

    @Override
    public boolean deleteById(Long id) {
        return deleteLogic(id, DELETE_BY_ID_SQL);
    }

    @Override
    public Optional<BikeToDealership> findById(Long id) {
        BikeToDealership bikeToDealership = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
        statement.setLong(1, id);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            bikeToDealership = createBikeToDealership(resultSet);
        }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(bikeToDealership);
    }

    private BikeToDealership createBikeToDealership(ResultSet resultSet) throws SQLException {
        return new BikeToDealership(resultSet.getLong("id"),
                dealershipRepository.findById(resultSet.getLong("dealership_id")).orElse(null),
                bikeRepository.findById(resultSet.getLong("bike_id")).orElse(null));
    }

    @Override
    public List<BikeToDealership> findAll() {
        List<BikeToDealership> result = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createBikeToDealership(resultSet));
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
             PreparedStatement statement = connection.prepareStatement(EXISTS_BY_ID)) {
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

    @Override
    public List<BikeToDealership> findAllByBikeId(Long bikeId) {
        return findLogic(bikeId, FIND_ALL_BY_BIKE_ID_SQL);
    }

    @Override
    public List<BikeToDealership> findAllByDealershipId(Long dealershipId) {
        return findLogic(dealershipId, FIND_ALL_BY_DEALERSHIP_ID_SQL);
    }

    private List<BikeToDealership> findLogic(Long id, String sql) {
        List<BikeToDealership> result = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createBikeToDealership(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return result;
    }

    @Override
    public List<Bike> findAllBikesByDealershipId(Long dealershipId) {
        List<Bike> result = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_BIKES_BY_DEALERSHIP_ID_SQL)) {
            statement.setLong(1, dealershipId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                bikeRepository.findAll().stream().filter((x) -> {
                    try {
                        return x.getId() == resultSet.getLong("bike_id");
                    } catch (SQLException e) {
                        throw new RepositoryException(e);
                    }
                }).findFirst().ifPresent(result::add);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return result;
    }

    @Override
    public List<Dealership> findAllDealershipsByBikeId(Long bikeId) {
        List<Dealership> result = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_DEALERSHIPS_BY_BIKE_ID_SQL)) {
            statement.setLong(1, bikeId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dealershipRepository.findAll().stream().filter((x) -> {
                    try {
                        return x.getId() == resultSet.getLong("dealership_id");
                    } catch (SQLException e) {
                        throw new RepositoryException(e);
                    }
                }).findFirst().ifPresent(result::add);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return result;
    }

    @Override
    public Optional<BikeToDealership> findByBikeIdAndDealershipId(Long dealershipId, Long bikeId) {
        BikeToDealership bikeToDealership = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_BIKE_ID_AND_DEPARTMENT_ID_SQL)) {
            statement.setLong(1, bikeId);
            statement.setLong(2, dealershipId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                bikeToDealership = createBikeToDealership(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(bikeToDealership);
    }

    @Override
    public boolean deleteByBikeId(Long bikeId) {
        return deleteLogic(bikeId, DELETE_BY_BIKE_ID_SQL);
    }

    @Override
    public boolean deleteByDealershipId(Long dealershipId) {
        return deleteLogic(dealershipId, DELETE_BY_DEALERSHIP_ID_SQL);
    }

    private boolean deleteLogic(Long id, String sql) {
        boolean result;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return result;
    }
}
