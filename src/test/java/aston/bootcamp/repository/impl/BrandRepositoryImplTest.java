package aston.bootcamp.repository.impl;

import aston.bootcamp.db.connectionPool.ConnectionPool;
import aston.bootcamp.entity.Brand;
import aston.bootcamp.repository.BrandRepository;
import aston.bootcamp.utils.PropertiesUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.reflect.Field;
import java.util.Optional;

@Testcontainers
public class BrandRepositoryImplTest {
    private static final String CREATE_SQL = "sql/schema.sql";

    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest").
            withInitScript(CREATE_SQL);

    private static BrandRepository brandRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() throws Exception {
        container.start();
        HikariConfig testHikariConfig = new HikariConfig();
        testHikariConfig.setDriverClassName(PropertiesUtil.getProperties("db.driver-class-name"));
        testHikariConfig.setJdbcUrl(container.getJdbcUrl());
        testHikariConfig.setUsername(container.getUsername());
        testHikariConfig.setPassword(container.getPassword());
        testHikariConfig.setMaximumPoolSize(Integer.parseInt(PropertiesUtil.getProperties("hikari.max-pool-size")));
        testHikariConfig.setMinimumIdle(Integer.parseInt(PropertiesUtil.getProperties("hikari.min-idle")));
        testHikariConfig.setAutoCommit(Boolean.parseBoolean(PropertiesUtil.getProperties("hikari.set-autocommit")));
        testHikariConfig.setDriverClassName(PropertiesUtil.getProperties("db.driver-class-name"));
        HikariDataSource testDataSource = new HikariDataSource(testHikariConfig);
        Field connectionPool = ConnectionPool.class.getDeclaredField("DATA_SOURCE");
        connectionPool.setAccessible(true);
        connectionPool.set(connectionPool, testDataSource);
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

