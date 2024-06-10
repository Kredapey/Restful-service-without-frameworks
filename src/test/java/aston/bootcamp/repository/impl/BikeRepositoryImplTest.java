package aston.bootcamp.repository.impl;

import aston.bootcamp.entity.Bike;
import aston.bootcamp.entity.Brand;
import aston.bootcamp.entity.Type;
import aston.bootcamp.repository.BikeRepository;
import aston.bootcamp.repository.BrandRepository;
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
public class BikeRepositoryImplTest {
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

    private static BikeRepository bikeRepository;
    private static TypeRepository typeRepository;
    private static BrandRepository brandRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        bikeRepository = BikeRepositoryImpl.getInstance();
        typeRepository = TypeRepositoryImpl.getInstance();
        brandRepository = BrandRepositoryImpl.getInstance();
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
        Type savingType = typeRepository.findById(1L).get();
        Brand savingBrand = brandRepository.findById(1L).get();
        String savingModel = "modelExample";
        Long savingCost = 900000L;
        Bike bikeForSave = new Bike(null, savingType, savingBrand, savingModel, savingCost, null);
        bikeForSave = bikeRepository.save(bikeForSave);
        Optional<Bike> insertedBike = bikeRepository.findById(bikeForSave.getId());
        Assertions.assertTrue(insertedBike.isPresent());
        Assertions.assertEquals(insertedBike.get().getType(), savingType);
        Assertions.assertEquals(insertedBike.get().getBrand(), savingBrand);
        Assertions.assertEquals(insertedBike.get().getModel(), savingModel);
        Assertions.assertEquals(insertedBike.get().getCost(), savingCost);
    }

    @Test
    void update() {
        Type updatedType = typeRepository.findById(4L).get();
        Brand updatedBrand = brandRepository.findById(5L).get();
        String updatedBikeModel = "DS 650 X";
        Long updatedCost = 800000L;
        Bike bikeForUpdate = bikeRepository.findById(1L).get();
        Type oldType = bikeForUpdate.getType();
        Brand oldBrand = bikeForUpdate.getBrand();
        String oldBikeModel = bikeForUpdate.getModel();
        Long oldCost = bikeForUpdate.getCost();
        bikeForUpdate.setType(updatedType);
        bikeForUpdate.setBrand(updatedBrand);
        bikeForUpdate.setModel(updatedBikeModel);
        bikeForUpdate.setCost(updatedCost);
        bikeRepository.update(bikeForUpdate);
        Bike updatedBike = bikeRepository.findById(1L).get();
        Assertions.assertNotEquals(oldType, updatedBike.getType());
        Assertions.assertNotEquals(oldBrand, updatedBike.getBrand());
        Assertions.assertNotEquals(oldBikeModel, updatedBike.getModel());
        Assertions.assertNotEquals(oldCost, updatedBike.getCost());
        Assertions.assertEquals(updatedType, updatedBike.getType());
        Assertions.assertEquals(updatedBrand, updatedBike.getBrand());
        Assertions.assertEquals(updatedBikeModel, updatedBike.getModel());
        Assertions.assertEquals(updatedCost, updatedBike.getCost());
    }

    @Test
    void deleteById() {
        Boolean expectedResult = true;
        int expectedSize = bikeRepository.findAll().size();
        Type tempType = typeRepository.findById(4L).get();
        Brand tempBrand = brandRepository.findById(5L).get();
        String tempModel = "DS 650 X";
        Long tempCost = 800000L;
        Bike tempBike = new Bike(null, tempType, tempBrand, tempModel, tempCost, null);
        tempBike = bikeRepository.save(tempBike);
        boolean deleteResult = bikeRepository.deleteById(tempBike.getId());
        int bikeSize = bikeRepository.findAll().size();
        Assertions.assertEquals(expectedResult, deleteResult);
        Assertions.assertEquals(expectedSize, bikeSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "50; false"
    }, delimiter = ';')
    void findById(Long id, Boolean expectedResult) {
        Optional<Bike> bike = bikeRepository.findById(id);
        Assertions.assertEquals(expectedResult, bike.isPresent());
        bike.ifPresent(b -> Assertions.assertEquals(id, b.getId()));
    }

    @Test
    void findAll() {
        Long expectedSize = 19L;
        int resultSize = bikeRepository.findAll().size();
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "50; false"
    }, delimiter = ';')
    void existsById(Long id, Boolean expectedResult) {
        Boolean result = bikeRepository.existById(id);
        Assertions.assertEquals(result, expectedResult);
    }
}
