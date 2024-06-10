package aston.bootcamp.repository.impl;

import aston.bootcamp.entity.Type;
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
public class TypeRepositoryImplTest {
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

    private static TypeRepository typeRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
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
