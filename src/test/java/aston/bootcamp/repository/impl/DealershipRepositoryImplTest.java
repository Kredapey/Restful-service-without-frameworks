package aston.bootcamp.repository.impl;

import aston.bootcamp.entity.Dealership;
import aston.bootcamp.entity.Type;
import aston.bootcamp.repository.DealershipRepository;
import aston.bootcamp.repository.TypeRepository;
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
public class DealershipRepositoryImplTest {
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

    private static DealershipRepository dealershipRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
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
        String savingCity = "Omsk";
        String savingStreet = "Lenina";
        Long savingNum = 25L;
        Dealership dealershipForSave = new Dealership(null, savingCity, savingStreet, savingNum, null);
        dealershipForSave = dealershipRepository.save(dealershipForSave);
        Optional<Dealership> insertedDealership = dealershipRepository.findById(dealershipForSave.getId());
        Assertions.assertTrue(insertedDealership.isPresent());
        Assertions.assertEquals(insertedDealership.get().getCity(), savingCity);
        Assertions.assertEquals(insertedDealership.get().getStreet(), savingStreet);
        Assertions.assertEquals(insertedDealership.get().getHouseNum(), savingNum);
    }

    @Test
    void update() {
        String updatedCity = "Omsk";
        String updatedStreet = "Lenina";
        Long updatedNum = 25L;
        Dealership dealershipForUpdate = dealershipRepository.findById(1L).get();
        String oldCity = dealershipForUpdate.getCity();
        String oldStreet = dealershipForUpdate.getStreet();
        Long oldNum = dealershipForUpdate.getHouseNum();
        dealershipForUpdate.setCity(updatedCity);
        dealershipForUpdate.setStreet(updatedStreet);
        dealershipForUpdate.setHouseNum(updatedNum);
        dealershipRepository.update(dealershipForUpdate);
        Dealership updatedDealership = dealershipRepository.findById(1L).get();
        Assertions.assertNotEquals(oldCity, updatedDealership.getCity());
        Assertions.assertNotEquals(oldStreet, updatedDealership.getStreet());
        Assertions.assertNotEquals(oldNum, updatedDealership.getHouseNum());
        Assertions.assertEquals(updatedCity, updatedDealership.getCity());
        Assertions.assertEquals(updatedStreet, updatedDealership.getStreet());
        Assertions.assertEquals(updatedNum, updatedDealership.getHouseNum());
    }

    @Test
    void deleteById() {
        Boolean expectedResult = true;
        int expectedSize = dealershipRepository.findAll().size();
        Dealership tempDealership = new Dealership(null, "Omsk", "Lenina", 25L, null);
        tempDealership = dealershipRepository.save(tempDealership);
        boolean deleteResult = dealershipRepository.deleteById(tempDealership.getId());
        int dealershipSize = dealershipRepository.findAll().size();
        Assertions.assertEquals(expectedResult, deleteResult);
        Assertions.assertEquals(expectedSize, dealershipSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "50; false"
    }, delimiter = ';')
    void findById(Long id, Boolean expectedResult) {
        Optional<Dealership> dealership = dealershipRepository.findById(id);
        Assertions.assertEquals(expectedResult, dealership.isPresent());
        dealership.ifPresent(d -> Assertions.assertEquals(id, d.getId()));
    }

    @Test
    void findAll() {
        Long expectedSize = 4L;
        int resultSize = dealershipRepository.findAll().size();
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "50; false"
    }, delimiter = ';')
    void existsById(Long id, Boolean expectedResult) {
        Boolean result = dealershipRepository.existById(id);
        Assertions.assertEquals(result, expectedResult);
    }
}
