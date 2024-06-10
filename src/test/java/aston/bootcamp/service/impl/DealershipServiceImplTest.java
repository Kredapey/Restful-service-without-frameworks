package aston.bootcamp.service.impl;

import aston.bootcamp.entity.*;
import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.repository.BikeRepository;
import aston.bootcamp.repository.BikeToDealershipRepository;
import aston.bootcamp.repository.DealershipRepository;
import aston.bootcamp.repository.impl.BikeRepositoryImpl;
import aston.bootcamp.repository.impl.BikeToDealershipRepositoryImpl;
import aston.bootcamp.repository.impl.DealershipRepositoryImpl;
import aston.bootcamp.service.DealershipService;
import aston.bootcamp.servlet.dto.DealershipIncomingDto;
import aston.bootcamp.servlet.dto.DealershipOutgoingDto;
import aston.bootcamp.servlet.dto.DealershipUpdateDto;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class DealershipServiceImplTest {
    private static DealershipService dealershipService;
    private static DealershipRepository mockDealershipRepository;
    private static BikeRepository mockBikeRepository;
    private static BikeToDealershipRepository mockBikeToDealershipRepository;
    private static DealershipRepositoryImpl oldDealershipRepositoryInstance;
    private static BikeRepositoryImpl oldBikeRepositoryInstance;
    private static BikeToDealershipRepositoryImpl oldBikeToDealershipRepositoryInstance;

    private static void setMock(DealershipRepository mock) {
        try {
            Field instance = DealershipRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldDealershipRepositoryInstance = (DealershipRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

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

    private static void setMock(BikeToDealershipRepository mock) {
        try {
            Field instance = BikeToDealershipRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldBikeToDealershipRepositoryInstance = (BikeToDealershipRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockDealershipRepository = Mockito.mock(DealershipRepository.class);
        setMock(mockDealershipRepository);
        mockBikeRepository = Mockito.mock(BikeRepository.class);
        setMock(mockBikeRepository);
        mockBikeToDealershipRepository = Mockito.mock(BikeToDealershipRepository.class);
        setMock(mockBikeToDealershipRepository);
        dealershipService = DealershipServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field dealerInstance = DealershipRepositoryImpl.class.getDeclaredField("instance");
        dealerInstance.setAccessible(true);
        dealerInstance.set(dealerInstance, oldDealershipRepositoryInstance);
        Field bikeInstance = BikeRepositoryImpl.class.getDeclaredField("instance");
        bikeInstance.setAccessible(true);
        bikeInstance.set(bikeInstance, oldBikeRepositoryInstance);
        Field bikeToDealershipInstance = BikeToDealershipRepositoryImpl.class.getDeclaredField("instance");
        bikeToDealershipInstance.setAccessible(true);
        bikeToDealershipInstance.set(bikeToDealershipInstance, oldBikeToDealershipRepositoryInstance);
    }

    @BeforeEach
    void beforeEach() {
        Mockito.reset(mockBikeRepository);
        Mockito.reset(mockDealershipRepository);
        Mockito.reset(mockBikeToDealershipRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;
        DealershipIncomingDto dealershipIncomingDto = new DealershipIncomingDto("city", "street", 10L);
        Dealership dealership = new Dealership(expectedId, "city", "street", 10L, List.of());
        Mockito.doReturn(dealership).when(mockDealershipRepository).save(Mockito.any(Dealership.class));
        DealershipOutgoingDto result = dealershipService.save(dealershipIncomingDto);
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;
        DealershipUpdateDto dealershipUpdateDto = new DealershipUpdateDto(expectedId, "city", "street", 10L, List.of());
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any());
        dealershipService.update(dealershipUpdateDto);
        ArgumentCaptor<Dealership> argumentCaptor = ArgumentCaptor.forClass(Dealership.class);
        Mockito.verify(mockDealershipRepository).update(argumentCaptor.capture());
        Assertions.assertEquals(expectedId, argumentCaptor.getValue().getId());
    }

    @Test
    void updateNotFoundException() {
        DealershipUpdateDto dealershipUpdateDto = new DealershipUpdateDto(1L, "city", "street", 10L, List.of());
        Mockito.doReturn(false).when(mockDealershipRepository).existById(Mockito.any());
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> dealershipService.update(dealershipUpdateDto));
        Assertions.assertEquals("Dealership not found", ex.getMessage());
    }

    @Test
    void updateIllegalArgumentException() {
        DealershipUpdateDto dealershipUpdateDto = new DealershipUpdateDto(null, "city", "street", 10L, List.of());
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> dealershipService.update(dealershipUpdateDto));
        Assertions.assertEquals("Incorrect dealership params", ex.getMessage());
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 1L;
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any());
        dealershipService.delete(expectedId);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockDealershipRepository).deleteById(argumentCaptor.capture());
        Assertions.assertEquals(expectedId, argumentCaptor.getValue());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;
        Optional<Dealership> expectedResult = Optional.of(
                new Dealership(expectedId, "city", "street", 10L, List.of())
        );
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any());
        Mockito.doReturn(expectedResult).when(mockDealershipRepository).findById(Mockito.any(Long.class));
        DealershipOutgoingDto typeOutgoingDto = dealershipService.findById(expectedId);
        Assertions.assertEquals(expectedId, typeOutgoingDto.getId());
    }

    @Test
    void findByIdNotFoundException() {
        Long id = 100L;
        Optional<Dealership> nullOptional = Optional.empty();
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any());
        Mockito.doReturn(nullOptional).when(mockDealershipRepository).findById(Mockito.any(Long.class));
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> dealershipService.findById(id));
        Assertions.assertEquals("Dealership not found", ex.getMessage());
    }

    @Test
    void findAll() {
        List<Dealership> expectedResult = List.of(
                new Dealership(1L, "city", "street", 10L, List.of()),
                new Dealership(2L, "city", "street", 10L, List.of())
        );
        Mockito.doReturn(expectedResult).when(mockDealershipRepository).findAll();
        List<DealershipOutgoingDto> result = dealershipService.findAll();
        Assertions.assertEquals(expectedResult.size(), result.size());
    }

    @Test
    void deleteBikeFromDealership() throws NotFoundException {
        Long dealershipId = 1L;
        Long bikeId = 1L;
        Optional<BikeToDealership> bikeToDealership = Optional.of(
                new BikeToDealership(1L,
                        new Dealership(1L, "test", "test", 10L, List.of()),
                        new Bike(1L, new Type(1L, "test"), new Brand(1L, "test"), "test", 10L, List.of()))
        );
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(true).when(mockBikeRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(bikeToDealership).when(mockBikeToDealershipRepository).findByBikeIdAndDealershipId(Mockito.any(Long.class), Mockito.any(Long.class));
        dealershipService.deleteBikeFromDealership(dealershipId, bikeId);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockBikeToDealershipRepository).deleteById(captor.capture());
        Assertions.assertEquals(bikeId, captor.getValue());
    }

    @Test
    void deleteBikeFromDealershipBikeNotExists() {
        Long bikeId = 1L;
        Long dealershipId = 1L;
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(false).when(mockBikeRepository).existById(Mockito.any(Long.class));
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> dealershipService.deleteBikeFromDealership(dealershipId, bikeId));
        Assertions.assertEquals("Bike not found", ex.getMessage());
    }

    @Test
    void deleteBikeFromDealershipNoLink() {
        Long bikeId = 1L;
        Long dealershipId = 1L;
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(true).when(mockBikeRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(Optional.empty()).when(mockBikeToDealershipRepository).findByBikeIdAndDealershipId(Mockito.any(Long.class), Mockito.any(Long.class));
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> dealershipService.deleteBikeFromDealership(dealershipId, bikeId));
        Assertions.assertEquals("Not found link between dealership and bike", ex.getMessage());
    }

    @Test
    void addBikeToDealership() throws NotFoundException {
        Long bikeId = 1L;
        Long dealershipId = 1L;
        Optional<Dealership> dealership = Optional.of(new Dealership(1L, "test", "test", 10L, List.of()));
        Optional<Bike> bike = Optional.of(new Bike(1L, new Type(1L, "test"), new Brand(1L, "test"), "test", 10L, List.of()));
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(true).when(mockBikeRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(dealership).when(mockDealershipRepository).findById(Mockito.any());
        Mockito.doReturn(bike).when(mockBikeRepository).findById(Mockito.any());
        dealershipService.addBikeToDealership(dealershipId, bikeId);
        ArgumentCaptor<BikeToDealership> captor = ArgumentCaptor.forClass(BikeToDealership.class);
        Mockito.verify(mockBikeToDealershipRepository).save(captor.capture());
        Assertions.assertEquals(bikeId, captor.getValue().getBike().getId());
        Assertions.assertEquals(dealershipId, captor.getValue().getDealership().getId());
    }

    @Test
    void addBikeToDealershipNoDealership() {
        Long bikeId = 1L;
        Long dealershipId = 1L;
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(true).when(mockBikeRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(Optional.empty()).when(mockDealershipRepository).findById(Mockito.any());
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> dealershipService.addBikeToDealership(dealershipId, bikeId));
        Assertions.assertEquals("Can't find dealership", ex.getMessage());
    }

    @Test
    void addBikeToDealershipNoBike() {
        Long bikeId = 1L;
        Long dealershipId = 1L;
        Optional<Dealership> dealership = Optional.of(new Dealership(1L, "test", "test", 10L, List.of()));
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(true).when(mockBikeRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(dealership).when(mockDealershipRepository).findById(Mockito.any());
        Mockito.doReturn(Optional.empty()).when(mockBikeRepository).findById(Mockito.any());
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> dealershipService.addBikeToDealership(dealershipId, bikeId));
        Assertions.assertEquals("Can't find bike", ex.getMessage());
    }

    @Test
    void addBikeToDealershipNotFound() {
        Long bikeId = 1L;
        Long dealershipId = 1L;
        Mockito.doReturn(true).when(mockDealershipRepository).existById(Mockito.any(Long.class));
        Mockito.doReturn(false).when(mockBikeRepository).existById(Mockito.any(Long.class));
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> dealershipService.addBikeToDealership(dealershipId, bikeId));
        Assertions.assertEquals("Bike not found", ex.getMessage());
    }
}
