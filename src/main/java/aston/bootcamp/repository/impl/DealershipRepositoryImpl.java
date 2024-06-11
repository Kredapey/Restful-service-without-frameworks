package aston.bootcamp.repository.impl;

import aston.bootcamp.db.ConnectionManager;
import aston.bootcamp.db.impl.ConnectionManagerImpl;
import aston.bootcamp.entity.Bike;
import aston.bootcamp.entity.BikeToDealership;
import aston.bootcamp.entity.Dealership;
import aston.bootcamp.exceptions.RepositoryException;
import aston.bootcamp.repository.BikeRepository;
import aston.bootcamp.repository.BikeToDealershipRepository;
import aston.bootcamp.repository.DealershipRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DealershipRepositoryImpl implements DealershipRepository {
    private static final String SAVE_SQL = """
            INSERT INTO dealerships(city, street, house_num)
            VALUES (?, ?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE dealerships
            SET city = ?, street = ?, house_num = ? WHERE id = ?;
            """;
    private static final String DELETE_BY_ID_SQL = """
            DELETE FROM dealerships WHERE id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM dealerships WHERE id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT * FROM dealerships;
            """;
    private static final String EXISTS_BY_ID_SQL = """
            SELECT exists(
            SELECT 1 FROM dealerships WHERE id = ?);
            """;
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static final BikeToDealershipRepository bikeToDealershipRepository = BikeToDealershipRepositoryImpl.getInstance();
    private static final BikeRepository bikeRepository = BikeRepositoryImpl.getInstance();
    private static DealershipRepository instance;

    private DealershipRepositoryImpl() {

    }

    public static synchronized DealershipRepository getInstance() {
        if (instance == null) {
            instance = new DealershipRepositoryImpl();
        }
        return instance;
    }

    private void updateBikeList(Dealership dealership) {
        if (dealership.getBikes() != null && !dealership.getBikes().isEmpty()) {
            List<Long> bikesForUpdate = new ArrayList<>(
                    dealership.getBikes().
                            stream().
                            map(Bike::getId).
                            toList());
            List<BikeToDealership> bikesToDealerships = bikeToDealershipRepository.findAllByDealershipId(dealership.getId());
            for (BikeToDealership bikeToDealership : bikesToDealerships) {
                if (!bikesForUpdate.contains(bikeToDealership.getBike().getId())) {
                    bikeToDealershipRepository.deleteById(bikeToDealership.getId());
                }
                bikesForUpdate.remove(bikeToDealership.getBike().getId());
            }
            for (Long bikeId : bikesForUpdate) {
                if (bikeRepository.existById(bikeId)) {
                    bikeToDealershipRepository.save(
                            new BikeToDealership(null,
                                    dealership,
                                    bikeRepository.findById(bikeId).orElse(null))
                    );
                }
            }
        } else {
            bikeToDealershipRepository.deleteByDealershipId(dealership.getId());
        }
    }

    @Override
    public Dealership save(Dealership dealership) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, dealership.getCity());
            statement.setString(2, dealership.getStreet());
            statement.setLong(3, dealership.getHouseNum());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                dealership = buildDealership(dealership, resultSet);
            }
            updateBikeList(dealership);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return dealership;
    }

    private Dealership buildDealership(Dealership dealership, ResultSet resultSet) throws SQLException {
        return new Dealership(resultSet.getLong("id"),
                dealership.getCity(),
                dealership.getStreet(),
                dealership.getHouseNum(),
                new ArrayList<>());
    }

    @Override
    public void update(Dealership dealership) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, dealership.getCity());
            statement.setString(2, dealership.getStreet());
            statement.setLong(3, dealership.getHouseNum());
            statement.setLong(4, dealership.getId());
            statement.executeUpdate();
            updateBikeList(dealership);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean result;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            bikeToDealershipRepository.deleteByDealershipId(id);
            statement.setLong(1, id);
            result = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return result;
    }

    @Override
    public Optional<Dealership> findById(Long id) {
        Dealership result = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = createDealership(resultSet);
            }
            if (result != null) {
                result.setBikes(bikeToDealershipRepository.findAllBikesByDealershipId(id));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(result);
    }

    @Override
    public List<Dealership> findAll() {
        List<Dealership> result = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createDealership(resultSet));
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

    private Dealership createDealership(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String city = resultSet.getString("city");
        String street = resultSet.getString("street");
        Long houseNum = resultSet.getLong("house_num");
        return new Dealership(id, city, street, houseNum, new ArrayList<>());
    }
}
