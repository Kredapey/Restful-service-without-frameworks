package aston.bootcamp.repository.impl;

import aston.bootcamp.db.connectionPool.ConnectionPool;
import aston.bootcamp.entity.Type;
import aston.bootcamp.repository.TypeRepository;
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
public class TypeRepositoryImplTest {
    private static final String CREATE_SQL = "sql/schema.sql";
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:latest").
            withInitScript(CREATE_SQL);

    private static TypeRepository typeRepository;
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
        ConnectionPool connectionPool = ConnectionPool.getInstance();
        Field dataSource = connectionPool.getClass().getDeclaredField("dataSource");
        dataSource.setAccessible(true);
        dataSource.set(connectionPool, testDataSource);
        typeRepository = TypeRepositoryImpl.getInstance();
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
        String savingTypeName = "custom";
        Type typeForSave = new Type(null, savingTypeName);
        typeForSave = typeRepository.save(typeForSave);
        Optional<Type> insertedType = typeRepository.findById(typeForSave.getId());
        Assertions.assertTrue(insertedType.isPresent());
        Assertions.assertEquals(insertedType.get().getType(), savingTypeName);
    }

    @Test
    void update() {
        String updatedTypeName = "cruiser";
        Type typeForUpdate = typeRepository.findById(1L).get();
        String oldTypeName = typeForUpdate.getType();
        typeForUpdate.setType(updatedTypeName);
        typeRepository.update(typeForUpdate);
        Type updatedType = typeRepository.findById(1L).get();
        Assertions.assertNotEquals(oldTypeName, updatedType.getType());
        Assertions.assertEquals(updatedTypeName, updatedType.getType());
    }

    @Test
    void deleteById() {
        Boolean expectedResult = true;
        int expectedSize = typeRepository.findAll().size();
        Type tempType = new Type(null, "custom");
        tempType = typeRepository.save(tempType);
        boolean deleteResult = typeRepository.deleteById(tempType.getId());
        int typeSize = typeRepository.findAll().size();
        Assertions.assertEquals(expectedResult, deleteResult);
        Assertions.assertEquals(expectedSize, typeSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "50; false"
    }, delimiter = ';')
    void findById(Long id, Boolean expectedResult) {
        Optional<Type> type = typeRepository.findById(id);
        Assertions.assertEquals(expectedResult, type.isPresent());
        type.ifPresent(ty -> Assertions.assertEquals(id, ty.getId()));
    }

    @Test
    void findAll() {
        Long expectedSize = 6L;
        int resultSize = typeRepository.findAll().size();
        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "2; true",
            "50; false"
    }, delimiter = ';')
    void existsById(Long id, Boolean expectedResult) {
        Boolean result = typeRepository.existById(id);
        Assertions.assertEquals(result, expectedResult);
    }
}
