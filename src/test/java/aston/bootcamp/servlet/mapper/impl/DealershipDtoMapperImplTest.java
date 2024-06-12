package aston.bootcamp.servlet.mapper.impl;

import aston.bootcamp.entity.Dealership;
import aston.bootcamp.servlet.dto.DealershipIncomingDto;
import aston.bootcamp.servlet.dto.DealershipOutgoingDto;
import aston.bootcamp.servlet.dto.DealershipUpdateDto;
import aston.bootcamp.servlet.mapper.DealershipDtoMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DealershipDtoMapperImplTest {
    private static DealershipDtoMapper dealershipDtoMapper;

    @BeforeAll
    static void beforeAll() {
        dealershipDtoMapper = DealershipDtoMapperImpl.getInstance();
    }

    @Test
    void mapIncomingDto() {
        DealershipIncomingDto dealershipIncomingDto = new DealershipIncomingDto("test_city",
                "test_street", 1L);
        Dealership dealership = dealershipDtoMapper.map(dealershipIncomingDto);
        Assertions.assertEquals(dealershipIncomingDto.getCity(), dealership.getCity());
        Assertions.assertEquals(dealershipIncomingDto.getStreet(), dealership.getStreet());
        Assertions.assertEquals(dealershipIncomingDto.getHouseNum(), dealership.getHouseNum());
    }

    @Test
    void mapOutgoingDto() {
        Dealership dealership = new Dealership(1L, "test_city",
                "test_street", 1L, List.of());
        DealershipOutgoingDto dealershipOutgoingDto = dealershipDtoMapper.map(dealership);
        Assertions.assertEquals(dealership.getId(), dealershipOutgoingDto.getId());
        Assertions.assertEquals(dealership.getBikes().size(), dealershipOutgoingDto.getBikes().size());
        Assertions.assertEquals(dealership.getCity(), dealershipOutgoingDto.getCity());
        Assertions.assertEquals(dealership.getStreet(), dealershipOutgoingDto.getStreet());
        Assertions.assertEquals(dealership.getHouseNum(), dealershipOutgoingDto.getHouseNum());
    }

    @Test
    void mapUpdateDto() {
        DealershipUpdateDto dealershipUpdateDto = new DealershipUpdateDto(1L, "test_city",
                "test_street", 1L, List.of());
        Dealership dealership = dealershipDtoMapper.map(dealershipUpdateDto);
        Assertions.assertEquals(dealershipUpdateDto.getId(), dealership.getId());
        Assertions.assertEquals(dealershipUpdateDto.getCity(), dealership.getCity());
        Assertions.assertEquals(dealershipUpdateDto.getStreet(), dealership.getStreet());
        Assertions.assertEquals(dealershipUpdateDto.getHouseNum(), dealership.getHouseNum());
        Assertions.assertEquals(dealershipUpdateDto.getBikes().size(), dealership.getBikes().size());
    }

    @Test
    void mapListDealerships() {
        List<Dealership> dealerships = List.of(new Dealership(1L, "test_city",
                        "test_street", 1L, List.of()),
                new Dealership(2L, "test_city",
                        "test_street", 1L, List.of()));
        List<DealershipOutgoingDto> dealershipOutgoingDtos = dealershipDtoMapper.map(dealerships);
        Assertions.assertEquals(dealerships.size(), dealershipOutgoingDtos.size());
    }

    @Test
    void mapUpdateList() {
        List<DealershipUpdateDto> dealerships = List.of(new DealershipUpdateDto(1L, "test_city",
                        "test_street", 1L, List.of()),
                new DealershipUpdateDto(2L, "test_city",
                        "test_street", 1L, List.of()));
        List<Dealership> dealershipOutgoingDtos = dealershipDtoMapper.mapUpdateList(dealerships);
        Assertions.assertEquals(dealerships.size(), dealershipOutgoingDtos.size());
    }
}
