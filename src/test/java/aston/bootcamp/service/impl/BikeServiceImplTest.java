package aston.bootcamp.service.impl;

import aston.bootcamp.entity.Bike;
import aston.bootcamp.entity.Brand;
import aston.bootcamp.entity.Type;
import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.repository.BikeRepository;
import aston.bootcamp.repository.impl.BikeRepositoryImpl;
import aston.bootcamp.service.BikeService;
import aston.bootcamp.servlet.dto.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class BikeServiceImplTest {
    private static BikeService bikeService;
    private static BikeRepository mockBikeRepository;
    private static Type type;
    private static Brand brand;
    private static BikeRepositoryImpl oldBikeRepositoryInstance;

    private static void setMock(BikeRepository mock) {
        try {
            Field instance = BikeRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldBikeRepositoryInstance = (BikeRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @BeforeAll
    static void beforeAll() {
        type = new Type(1L, "test type");
        brand = new Brand(1L, "test brand");
        mockBikeRepository = Mockito.mock(BikeRepository.class);
        setMock(mockBikeRepository);
        bikeService = BikeServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = BikeRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldBikeRepositoryInstance);
    }

    @BeforeEach
    void beforeEach() {
        Mockito.reset(mockBikeRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;
        BikeIncomingDto bikeIncomingDto = new BikeIncomingDto(type, brand, "test_model", 100L);
        Bike bike = new Bike(expectedId, type, brand, "test_model", 100L, List.of());
        Mockito.doReturn(bike).when(mockBikeRepository).save(Mockito.any(Bike.class));
        BikeOutgoingDto result = bikeService.save(bikeIncomingDto);
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;
        BikeUpdateDto bikeUpdateDto = new BikeUpdateDto(expectedId, new TypeUpdateDto(1L, type.getType()),
                new BrandUpdateDto(1L, brand.getBrand()),
                "test_model",
                100L,
                List.of());
        Mockito.doReturn(true).when(mockBikeRepository).existById(Mockito.any());
        bikeService.update(bikeUpdateDto);
        ArgumentCaptor<Bike> argumentCaptor = ArgumentCaptor.forClass(Bike.class);
        Mockito.verify(mockBikeRepository).update(argumentCaptor.capture());
        Assertions.assertEquals(expectedId, argumentCaptor.getValue().getId());
    }

    @Test
    void updateNotFoundException() {
        BikeUpdateDto bikeUpdateDto = new BikeUpdateDto(1L, null, null, "test_model", 100L, null);
        Mockito.doReturn(false).when(mockBikeRepository).existById(Mockito.any());
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> bikeService.update(bikeUpdateDto));
        Assertions.assertEquals("Bike not found", ex.getMessage());
    }

    @Test
    void updateIllegalArgumentException() {
        BikeUpdateDto bikeUpdateDto = new BikeUpdateDto(null, null, null, "test_model", 100L, null);
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> bikeService.update(bikeUpdateDto));
        Assertions.assertEquals("Incorrect bike params", ex.getMessage());
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 1L;
        Mockito.doReturn(true).when(mockBikeRepository).existById(Mockito.any());
        bikeService.delete(expectedId);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockBikeRepository).deleteById(argumentCaptor.capture());
        Assertions.assertEquals(expectedId, argumentCaptor.getValue());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;
        Optional<Bike> expectedResult = Optional.of(
                new Bike(expectedId, type, brand, "test_model",
                        100L, List.of()));
        Mockito.doReturn(true).when(mockBikeRepository).existById(Mockito.any());
        Mockito.doReturn(expectedResult).when(mockBikeRepository).findById(Mockito.any(Long.class));
        BikeOutgoingDto bikeOutgoingDto = bikeService.findById(expectedId);
        Assertions.assertEquals(expectedId, bikeOutgoingDto.getId());
    }

    @Test
    void findByIdNotFoundException() {
        Long id = 100L;
        Optional<Bike> nullOptional = Optional.empty();
        Mockito.doReturn(true).when(mockBikeRepository).existById(Mockito.any());
        Mockito.doReturn(nullOptional).when(mockBikeRepository).findById(Mockito.any(Long.class));
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> bikeService.findById(id));
        Assertions.assertEquals("Bike not found", ex.getMessage());
    }

    @Test
    void findAll() {
        List<Bike> expectedResult = List.of(
                new Bike(1L, type, brand,
                        "test_model",
                        100L,
                        List.of()),
                new Bike(2L, type, brand,
                        "test_model",
                        100L,
                        List.of())
        );
        Mockito.doReturn(expectedResult).when(mockBikeRepository).findAll();
        List<BikeOutgoingDto> result = bikeService.findAll();
        Assertions.assertEquals(expectedResult.size(), result.size());
    }
}
