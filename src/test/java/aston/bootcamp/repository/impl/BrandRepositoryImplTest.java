package aston.bootcamp.repository.impl;

import aston.bootcamp.entity.Brand;
import aston.bootcamp.repository.BrandRepository;
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
public class BrandRepositoryImplTest {
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

    private static BrandRepository brandRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
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
        String expectedBrandName = "Jawa";
        Brand brand = new Brand(null, expectedBrandName);
        brand = brandRepository.save(brand);
        Optional<Brand> insertedBrand = brandRepository.findById(brand.getId());
        Assertions.assertTrue(insertedBrand.isPresent());
        Assertions.assertEquals(expectedBrandName, insertedBrand.get().getBrand());
    }

    @Test
    void update() {
        String expectedBrandName = "Royal Enfield";
        Brand brandForUpdate = brandRepository.findById(5L).get();
        String oldBrand = brandForUpdate.getBrand();
        brandForUpdate.setBrand(expectedBrandName);
        brandRepository.update(brandForUpdate);
        Brand brand = brandRepository.findById(5L).get();
        Assertions.assertNotEquals(expectedBrandName, oldBrand);
        Assertions.assertEquals(expectedBrandName, brand.getBrand());
    }

    @Test
    void deleteById() {
        Boolean expectedResult = true;
        int expectedSize = brandRepository.findAll().size();
        Brand tempBrand = new Brand(null, "GR");
        tempBrand = brandRepository.save(tempBrand);
        boolean deleteResult = brandRepository.deleteById(tempBrand.getId());
        int brandSize = brandRepository.findAll().size();
        Assertions.assertEquals(expectedResult, deleteResult);
        Assertions.assertEquals(expectedSize, brandSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "50; false"
    }, delimiter = ';')
    void findById(Long id, Boolean expectedResult) {
        Optional<Brand> brand = brandRepository.findById(id);
        Assertions.assertEquals(expectedResult, brand.isPresent());
        brand.ifPresent(br -> Assertions.assertEquals(id, br.getId()));
    }

    @Test
    void findAll() {
        Long expectedSize = 8L;
        int resultSize = brandRepository.findAll().size();
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "50; false"
    }, delimiter = ';')
    void existsById(Long id, Boolean expectedResult) {
        Boolean result = brandRepository.existById(id);
        Assertions.assertEquals(result, expectedResult);
    }
}

