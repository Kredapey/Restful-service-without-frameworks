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

public class BikeRepositoryImpl implements BikeRepository {
    private static final String SAVE_SQL = """
            INSERT INTO bikes(type_id, brand_id, model, cost)
            VALUES (?, ?, ?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE bikes
            SET type_id = ?,
                brand_id = ?,
                model = ?,
                cost = ?
            WHERE id = ?;
            """;
    private static final String DELETE_BY_ID_SQL = """
            DELETE FROM bikes
            WHERE id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT *
            FROM bikes WHERE id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT *
            FROM bikes;
            """;
    private static final String EXISTS_BY_ID_SQL = """
            SELECT exists(
            SELECT 1 FROM bikes WHERE id = ?);
            """;
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static final TypeRepository typeRepository = TypeRepositoryImpl.getInstance();
    private static final BrandRepository brandRepository = BrandRepositoryImpl.getInstance();
    private static final DealershipRepository dealershipRepository = DealershipRepositoryImpl.getInstance();
    private static final BikeToDealershipRepository bikeToDealershipRepository = BikeToDealershipRepositoryImpl.getInstance();
    private static BikeRepository instance;


    private BikeRepositoryImpl() {

    }

    public static synchronized BikeRepository getInstance() {
        if (instance == null) {
            instance = new BikeRepositoryImpl();
        }
        return instance;
    }

    private Bike buildBike(Bike bike, ResultSet resultSet) throws SQLException {
        return new Bike(resultSet.getLong("id"),
                bike.getType(),
                bike.getBrand(),
                bike.getModel(),
                bike.getCost(),
                new ArrayList<>());
    }

    private void updateDealershipList(Bike bike) {
        if (bike.getDealerships() != null && !bike.getDealerships().isEmpty()) {
            List<Long> dealershipsForUpdate = new ArrayList<>(
                    bike.getDealerships()
                            .stream().
                            map(Dealership::getId)
                            .toList());
            List<BikeToDealership> bikeToDealerships = bikeToDealershipRepository.findAllByBikeId(bike.getId());
            for (BikeToDealership bikeToDealership : bikeToDealerships) {
                if (!dealershipsForUpdate.contains(bikeToDealership.getDealership().getId())) {
                    bikeToDealershipRepository.deleteById(bikeToDealership.getId());
                }
                dealershipsForUpdate.remove(bikeToDealership.getDealership().getId());
            }
            for (Long dealershipId : dealershipsForUpdate) {
                if (dealershipRepository.existById(dealershipId)) {
                    bikeToDealershipRepository.save(
                            new BikeToDealership(null,
                                    dealershipRepository.findById(dealershipId).orElse(null),
                                    bike)
                    );
                }
            }
        } else {
            bikeToDealershipRepository.deleteByBikeId(bike.getId());
        }
    }

    @Override
    public Bike save(Bike bike) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setBikeParams(bike, statement);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                bike = buildBike(bike, resultSet);
            }
            updateDealershipList(bike);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return bike;
    }

    @Override
    public void update(Bike bike) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            setBikeParams(bike, statement);
            statement.setLong(5, bike.getId());
            statement.executeUpdate();
            updateDealershipList(bike);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    private void setBikeParams(Bike bike, PreparedStatement statement) throws SQLException {
        if (bike.getType() == null) {
            statement.setNull(1, Types.NULL);
        } else {
            statement.setLong(1, bike.getType().getId());
        }
        if (bike.getBrand() == null) {
            statement.setNull(2, Types.NULL);
        } else {
            statement.setLong(2, bike.getBrand().getId());
        }
        statement.setString(3, bike.getModel());
        statement.setLong(4, bike.getCost());
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result = true;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            bikeToDealershipRepository.deleteByBikeId(id);
            statement.setLong(1, id);
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return result;
    }

    @Override
    public Optional<Bike> findById(Long id) {
        Bike result = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = createBike(resultSet);
            }
            if (result != null) {
                result.setDealerships(bikeToDealershipRepository.findAllDealershipsByBikeId(id));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(result);
    }

    private Bike createBike(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Type type = typeRepository.findById(resultSet.getLong("type_id")).orElse(null);
        Brand brand = brandRepository.findById(resultSet.getLong("brand_id")).orElse(null);
        String model = resultSet.getString("model");
        Long cost = resultSet.getLong("cost");
        return new Bike(id, type, brand, model, cost, new ArrayList<>());
    }


    @Override
    public List<Bike> findAll() {
        List<Bike> result = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createBike(resultSet));
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

}
