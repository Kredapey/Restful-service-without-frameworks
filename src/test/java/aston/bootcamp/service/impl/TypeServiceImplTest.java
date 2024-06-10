package aston.bootcamp.service.impl;

import aston.bootcamp.entity.Type;
import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.repository.TypeRepository;
import aston.bootcamp.repository.impl.TypeRepositoryImpl;
import aston.bootcamp.service.TypeService;
import aston.bootcamp.servlet.dto.TypeIncomingDto;
import aston.bootcamp.servlet.dto.TypeOutgoingDto;
import aston.bootcamp.servlet.dto.TypeUpdateDto;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class TypeServiceImplTest {
    private static TypeService typeService;
    private static TypeRepository mockTypeRepository;
    private static TypeRepositoryImpl oldtypeRepositoryInstance;

    private static void setMock(TypeRepository mock) {
        try {
            Field instance = TypeRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldtypeRepositoryInstance = (TypeRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockTypeRepository = Mockito.mock(TypeRepository.class);
        setMock(mockTypeRepository);
        typeService = TypeServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = TypeRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldtypeRepositoryInstance);
    }

    @BeforeEach
    void beforeEach() {
        Mockito.reset(mockTypeRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;
        TypeIncomingDto typeIncomingDto = new TypeIncomingDto("test_brand");
        Type type = new Type(expectedId, "test_brand");
        Mockito.doReturn(type).when(mockTypeRepository).save(Mockito.any(Type.class));
        TypeOutgoingDto result = typeService.save(typeIncomingDto);
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;
        TypeUpdateDto typeUpdateDto = new TypeUpdateDto(expectedId, "test");
        Mockito.doReturn(true).when(mockTypeRepository).existById(Mockito.any());
        typeService.update(typeUpdateDto);
        ArgumentCaptor<Type> argumentCaptor = ArgumentCaptor.forClass(Type.class);
        Mockito.verify(mockTypeRepository).update(argumentCaptor.capture());
        Assertions.assertEquals(expectedId, argumentCaptor.getValue().getId());
    }

    @Test
    void updateNotFoundException() {
        TypeUpdateDto typeUpdateDto = new TypeUpdateDto(1L, "test");
        Mockito.doReturn(false).when(mockTypeRepository).existById(Mockito.any());
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> typeService.update(typeUpdateDto));
        Assertions.assertEquals("Type not found", ex.getMessage());
    }

    @Test
    void updateIllegalArgumentException() {
        TypeUpdateDto typeUpdateDto = new TypeUpdateDto(null, "test");
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> typeService.update(typeUpdateDto));
        Assertions.assertEquals("Incorrect type params", ex.getMessage());
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 1L;
        Mockito.doReturn(true).when(mockTypeRepository).existById(Mockito.any());
        typeService.delete(expectedId);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockTypeRepository).deleteById(argumentCaptor.capture());
        Assertions.assertEquals(expectedId, argumentCaptor.getValue());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;
        Optional<Type> expectedResult = Optional.of(
                new Type(expectedId, "test")
        );
        Mockito.doReturn(true).when(mockTypeRepository).existById(Mockito.any());
        Mockito.doReturn(expectedResult).when(mockTypeRepository).findById(Mockito.any(Long.class));
        TypeOutgoingDto typeOutgoingDto = typeService.findById(expectedId);
        Assertions.assertEquals(expectedId, typeOutgoingDto.getId());
    }

    @Test
    void findByIdNotFoundException() {
        Long id = 100L;
        Optional<Type> nullOptional = Optional.empty();
        Mockito.doReturn(true).when(mockTypeRepository).existById(Mockito.any());
        Mockito.doReturn(nullOptional).when(mockTypeRepository).findById(Mockito.any(Long.class));
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> typeService.findById(id));
        Assertions.assertEquals("Type not found", ex.getMessage());
    }

    @Test
    void findAll() {
        List<Type> expectedResult = List.of(
                new Type(1L, "test"),
                new Type(2L, "test")
        );
        Mockito.doReturn(expectedResult).when(mockTypeRepository).findAll();
        List<TypeOutgoingDto> result = typeService.findAll();
        Assertions.assertEquals(expectedResult.size(), result.size());
    }
}
