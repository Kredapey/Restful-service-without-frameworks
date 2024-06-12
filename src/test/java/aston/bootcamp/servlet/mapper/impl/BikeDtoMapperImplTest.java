package aston.bootcamp.servlet.mapper.impl;

import aston.bootcamp.entity.Bike;
import aston.bootcamp.entity.Brand;
import aston.bootcamp.entity.Type;
import aston.bootcamp.servlet.dto.*;
import aston.bootcamp.servlet.mapper.BikeDtoMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BikeDtoMapperImplTest {
    private static BikeDtoMapper bikeDtoMapper;

    @BeforeAll
    static void beforeAll() {
        bikeDtoMapper = BikeDtoMapperImpl.getInstance();
    }

    @Test
    void mapIncomingDto() {
        BikeIncomingDto bikeIncomingDto = new BikeIncomingDto(
                new Type(1L, "test_type"),
                new Brand(2L, "test_brand"),
                "test_model",
                10L
        );
        Bike bike = bikeDtoMapper.map(bikeIncomingDto);
        Assertions.assertEquals(bikeIncomingDto.getType(), bike.getType());
        Assertions.assertEquals(bikeIncomingDto.getBrand(), bike.getBrand());
        Assertions.assertEquals(bikeIncomingDto.getModel(), bike.getModel());
        Assertions.assertEquals(bikeIncomingDto.getCost(), bike.getCost());
    }

    @Test
    void mapOutgoingDto() {
        Bike bike = new Bike(10L,
                new Type(1L, "test_type"),
                new Brand(2L, "test_brand"),
                "test_model",
                10L,
                List.of());
        BikeOutgoingDto bikeOutgoingDto = bikeDtoMapper.map(bike);
        Assertions.assertEquals(bike.getId(), bikeOutgoingDto.getId());
        Assertions.assertEquals(bike.getType().getType(), bikeOutgoingDto.getType().getType());
        Assertions.assertEquals(bike.getBrand().getBrand(), bikeOutgoingDto.getBrand().getBrand());
        Assertions.assertEquals(bike.getModel(), bikeOutgoingDto.getModel());
        Assertions.assertEquals(bike.getCost(), bikeOutgoingDto.getCost());
        Assertions.assertEquals(bike.getDealerships().size(), bikeOutgoingDto.getDealerships().size());
    }

    @Test
    void mapUpdateDto() {
        BikeUpdateDto bikeUpdateDto = new BikeUpdateDto(10L,
                new TypeUpdateDto(1L, "test_type"),
                new BrandUpdateDto(2L, "test_brand"),
                "test_model",
                10L,
                List.of());
        Bike bike = bikeDtoMapper.map(bikeUpdateDto);
        Assertions.assertEquals(bikeUpdateDto.getId(), bike.getId());
        Assertions.assertEquals(bikeUpdateDto.getType().getType(), bike.getType().getType());
        Assertions.assertEquals(bikeUpdateDto.getBrand().getBrand(), bike.getBrand().getBrand());
        Assertions.assertEquals(bikeUpdateDto.getModel(), bike.getModel());
        Assertions.assertEquals(bikeUpdateDto.getCost(), bike.getCost());
        Assertions.assertEquals(bikeUpdateDto.getDealerships().size(), bike.getDealerships().size());
    }

    @Test
    void mapListBikes() {
        List<Bike> bikes = List.of(new Bike(
                10L,
                new Type(1L, "test_type"),
                new Brand(2L, "test_brand"),
                "test_model",
                10L,
                List.of()
        ), new Bike(20L,
                new Type(1L, "test_type"),
                new Brand(2L, "test_brand"),
                "test_model",
                10L,
                List.of()));
        List<BikeOutgoingDto> bikeOutgoingDto = bikeDtoMapper.map(bikes);
        Assertions.assertEquals(bikes.size(), bikeOutgoingDto.size());
    }

    @Test
    void mapUpdateList() {
        List<BikeUpdateDto> bikeUpdateDtoList = List.of(new BikeUpdateDto(
                10L,
                new TypeUpdateDto(1L, "test_type"),
                new BrandUpdateDto(2L, "test_brand"),
                "test_model",
                10L,
                List.of()
        ), new BikeUpdateDto(20L,
                new TypeUpdateDto(1L, "test_type"),
                new BrandUpdateDto(2L, "test_brand"),
                "test_model",
                10L,
                List.of()));
        List<Bike> bikes = bikeDtoMapper.mapUpdateList(bikeUpdateDtoList);
        Assertions.assertEquals(bikeUpdateDtoList.size(), bikes.size());
    }
}
