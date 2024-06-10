package aston.bootcamp.repository.impl;

import aston.bootcamp.entity.Bike;
import aston.bootcamp.entity.BikeToDealership;
import aston.bootcamp.entity.Dealership;
import aston.bootcamp.repository.BikeRepository;
import aston.bootcamp.repository.BikeToDealershipRepository;
import aston.bootcamp.repository.DealershipRepository;
import aston.bootcamp.utils.PropertiesUtil;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@Testcontainers
public class BikeToDealershipRepositoryImplTest {
    private static final String CREATE_SQL = "sql/schema.sql";

    private static final int CONTAINER_PORT = 5432;
    private static final int LOCAL_PORT = 5432;
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest").
            withDatabaseName("bikes_db").
            withUsername(PropertiesUtil.getProperties("db.usr")).
            withPassword(PropertiesUtil.getProperties("db.pwd")).
            withExposedPorts(CONTAINER_PORT).
            withCreateContainerCmdModifier(cmd ->
                    cmd.withHostConfig(new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(LOCAL_PORT), new ExposedPort(CONTAINER_PORT))))).
            withInitScript(CREATE_SQL);

    private static BikeToDealershipRepository bikeToDealershipRepository;
    private static BikeRepository bikeRepository;
    private static DealershipRepository dealershipRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        bikeToDealershipRepository = BikeToDealershipRepositoryImpl.getInstance();
        bikeRepository = BikeRepositoryImpl.getInstance();
        dealershipRepository = DealershipRepositoryImpl.getInstance();
        jdbcDatabaseDelegate = new JdbcDatabaseDelegate(container, "");
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void beforeEach() {
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, CREATE_SQL);
    }

    @Test
    void save() {
        Bike savingBike = bikeRepository.findById(16L).get();
        Dealership savingDealership = dealershipRepository.findById(1L).get();
        BikeToDealership bikeToDealershipForSave = new BikeToDealership(null,
                savingDealership, savingBike);
        bikeToDealershipForSave = bikeToDealershipRepository.save(bikeToDealershipForSave);
        Optional<BikeToDealership> insertedBikeToDealership = bikeToDealershipRepository.findById(bikeToDealershipForSave.getId());
        Assertions.assertTrue(insertedBikeToDealership.isPresent());
        Assertions.assertEquals(insertedBikeToDealership.get().getBike(), savingBike);
        Assertions.assertEquals(insertedBikeToDealership.get().getDealership(), savingDealership);
    }

    @Test
    void update() {
        Bike updatedBike = bikeRepository.findById(16L).get();
        Dealership updatedDealership = dealershipRepository.findById(1L).get();
        BikeToDealership bikeToDealershipForUpdate =
                bikeToDealershipRepository.findById(10L).get();
        Bike oldBike = bikeToDealershipForUpdate.getBike();
        Dealership oldDealership = bikeToDealershipForUpdate.getDealership();
        bikeToDealershipForUpdate.setDealership(updatedDealership);
        bikeToDealershipForUpdate.setBike(updatedBike);
        bikeToDealershipRepository.update(bikeToDealershipForUpdate);
        BikeToDealership updatedBikeToDealership = bikeToDealershipRepository.findById(10L).get();
        Assertions.assertNotEquals(oldBike, updatedBikeToDealership.getBike());
        Assertions.assertNotEquals(oldDealership, updatedBikeToDealership.getDealership());
        Assertions.assertEquals(updatedDealership, updatedBikeToDealership.getDealership());
        Assertions.assertEquals(updatedBike, updatedBikeToDealership.getBike());
    }

    @Test
    void deleteById() {
        Boolean expectedResult = true;
        int expectedSize = bikeToDealershipRepository.findAll().size();
        BikeToDealership tempBikeToDealership = new BikeToDealership(null,
                dealershipRepository.findById(1L).get(),
                bikeRepository.findById(16L).get());
        tempBikeToDealership = bikeToDealershipRepository.save(tempBikeToDealership);
        boolean deleteResult = bikeToDealershipRepository.deleteById(tempBikeToDealership.getId());
        int bikeToDealershipSize = bikeToDealershipRepository.findAll().size();
        Assertions.assertEquals(expectedResult, deleteResult);
        Assertions.assertEquals(expectedSize, bikeToDealershipSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "50; false"
    }, delimiter = ';')
    void findById(Long id, Boolean expectedResult) {
        Optional<BikeToDealership> bikeToDealership = bikeToDealershipRepository.findById(id);
        Assertions.assertEquals(expectedResult, bikeToDealership.isPresent());
        bikeToDealership.ifPresent(btd -> Assertions.assertEquals(id, btd.getId()));
    }

    @Test
    void findAll() {
        Long expectedSize = 23L;
        int resultSize = bikeToDealershipRepository.findAll().size();
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; 2",
            "2; 1",
            "3; 2"
    }, delimiter = ';')
    void findAllByBikeId(Long bikeId, long expectedSize) {
        int resultSize = bikeToDealershipRepository.findAllByBikeId(bikeId).size();
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; 4",
            "2; 9",
            "3; 6"
    }, delimiter = ';')
    void findAllByDealershipId(Long dealershipId, long expectedSize) {
        int resultSize = bikeToDealershipRepository.findAllByDealershipId(dealershipId).size();
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; 4",
            "2; 9",
            "3; 6"
    }, delimiter = ';')
    void findAllBikesByDealershipId(Long dealershipId, long expectedSize) {
        int resultSize = bikeToDealershipRepository.findAllBikesByDealershipId(dealershipId).size();
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; 2",
            "2; 1",
            "3; 2"
    }, delimiter = ';')
    void findAllDealershipsByBikeId(Long bikeId, Long expectedSize) {
        int resultSize = bikeToDealershipRepository.findAllDealershipsByBikeId(bikeId).size();
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; 2; 2",
            "1; 10; 3",
            "4; 1; 21"
    }, delimiter = ';')
    void findByBikeIdAndDealershipId(Long dealershipId, Long bikeId, Long expectedId) {
        Long resultId = bikeToDealershipRepository.findByBikeIdAndDealershipId(dealershipId, bikeId).get().getId();
        Assertions.assertEquals(expectedId, resultId);
    }


    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "50; false"
    }, delimiter = ';')
    void existsById(Long id, Boolean expectedResult) {
        Boolean result = bikeToDealershipRepository.existById(id);
        Assertions.assertEquals(result, expectedResult);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; 21",
            "2; 22",
            "3; 21"
    }, delimiter = ';')
    void deleteByBikeId(Long bikeId, Long expectedSize) {
        Boolean expectedResult = true;
        boolean deleteResult = bikeToDealershipRepository.deleteByBikeId(bikeId);
        int bikeToDealershipSize = bikeToDealershipRepository.findAll().size();
        Assertions.assertEquals(expectedResult, deleteResult);
        Assertions.assertEquals(expectedSize, bikeToDealershipSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; 19",
            "2; 14",
            "3; 17"
    }, delimiter = ';')
    void deleteByDealershipId(Long dealershipId, Long expectedSize) {
        Boolean expectedResult = true;
        boolean deleteResult = bikeToDealershipRepository.deleteByDealershipId(dealershipId);
        int bikeToDealershipSize = bikeToDealershipRepository.findAll().size();
        Assertions.assertEquals(expectedResult, deleteResult);
        Assertions.assertEquals(expectedSize, bikeToDealershipSize);
    }
}
