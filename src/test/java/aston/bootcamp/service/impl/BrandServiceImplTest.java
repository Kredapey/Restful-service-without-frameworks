package aston.bootcamp.service.impl;

import aston.bootcamp.entity.Brand;
import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.repository.BrandRepository;
import aston.bootcamp.repository.impl.BrandRepositoryImpl;
import aston.bootcamp.service.BrandService;
import aston.bootcamp.servlet.dto.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class BrandServiceImplTest {
    private static BrandService brandService;
    private static BrandRepository mockBrandRepository;
    private static BrandRepositoryImpl oldBrandRepositoryInstance;

    private static void setMock(BrandRepository mock) {
        try {
            Field instance = BrandRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldBrandRepositoryInstance = (BrandRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockBrandRepository = Mockito.mock(BrandRepository.class);
        setMock(mockBrandRepository);
        brandService = BrandServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = BrandRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldBrandRepositoryInstance);
    }

    @BeforeEach
    void beforeEach() {
        Mockito.reset(mockBrandRepository);
    }

    @Test
    void save() {
        Long expectedId = 1L;
        BrandIncomingDto brandIncomingDto = new BrandIncomingDto("test_brand");
        Brand brand = new Brand(expectedId, "test_brand");
        Mockito.doReturn(brand).when(mockBrandRepository).save(Mockito.any(Brand.class));
        BrandOutgoingDto result = brandService.save(brandIncomingDto);
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;
        BrandUpdateDto brandUpdateDto = new BrandUpdateDto(expectedId, "test");
        Mockito.doReturn(true).when(mockBrandRepository).existById(Mockito.any());
        brandService.update(brandUpdateDto);
        ArgumentCaptor<Brand> argumentCaptor = ArgumentCaptor.forClass(Brand.class);
        Mockito.verify(mockBrandRepository).update(argumentCaptor.capture());
        Assertions.assertEquals(expectedId, argumentCaptor.getValue().getId());
    }

    @Test
    void updateNotFoundException() {
        BrandUpdateDto brandUpdateDto = new BrandUpdateDto(1L, "test");
        Mockito.doReturn(false).when(mockBrandRepository).existById(Mockito.any());
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> brandService.update(brandUpdateDto));
        Assertions.assertEquals("Brand not found", ex.getMessage());
    }

    @Test
    void updateIllegalArgumentException() {
        BrandUpdateDto brandUpdateDto = new BrandUpdateDto(null, "test");
        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class,
                () -> brandService.update(brandUpdateDto));
        Assertions.assertEquals("Incorrect brand params", ex.getMessage());
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 1L;
        Mockito.doReturn(true).when(mockBrandRepository).existById(Mockito.any());
        brandService.delete(expectedId);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockBrandRepository).deleteById(argumentCaptor.capture());
        Assertions.assertEquals(expectedId, argumentCaptor.getValue());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;
        Optional<Brand> expectedResult = Optional.of(
                new Brand(expectedId, "test")
        );
        Mockito.doReturn(true).when(mockBrandRepository).existById(Mockito.any());
        Mockito.doReturn(expectedResult).when(mockBrandRepository).findById(Mockito.any(Long.class));
        BrandOutgoingDto brandOutgoingDto = brandService.findById(expectedId);
        Assertions.assertEquals(expectedId, brandOutgoingDto.getId());
    }

    @Test
    void findByIdNotFoundException() {
        Long id = 100L;
        Optional<Brand> nullOptional = Optional.empty();
        Mockito.doReturn(true).when(mockBrandRepository).existById(Mockito.any());
        Mockito.doReturn(nullOptional).when(mockBrandRepository).findById(Mockito.any(Long.class));
        NotFoundException ex = Assertions.assertThrows(NotFoundException.class,
                () -> brandService.findById(id));
        Assertions.assertEquals("Brand not found", ex.getMessage());
    }

    @Test
    void findAll() {
        List<Brand> expectedResult = List.of(
                new Brand(1L, "test"),
                new Brand(2L, "test")
        );
        Mockito.doReturn(expectedResult).when(mockBrandRepository).findAll();
        List<BrandOutgoingDto> result = brandService.findAll();
        Assertions.assertEquals(expectedResult.size(), result.size());
    }
}
