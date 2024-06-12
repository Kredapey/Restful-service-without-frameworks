package aston.bootcamp.servlet.mapper.impl;

import aston.bootcamp.entity.Brand;
import aston.bootcamp.servlet.dto.*;
import aston.bootcamp.servlet.mapper.BrandDtoMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BrandDtoMapperImplTest {
    private static BrandDtoMapper brandDtoMapper;

    @BeforeAll
    static void beforeAll() {
        brandDtoMapper = BrandDtoMapperImpl.getInstance();
    }

    @Test
    void mapIncomingDto() {
        BrandIncomingDto brandIncomingDto = new BrandIncomingDto("test");
        Brand brand = brandDtoMapper.map(brandIncomingDto);
        Assertions.assertEquals(brandIncomingDto.getBrand(), brand.getBrand());
    }

    @Test
    void mapOutgoingDto() {
        Brand brand = new Brand(1L, "test");
        BrandOutgoingDto brandOutgoingDto = brandDtoMapper.map(brand);
        Assertions.assertEquals(brand.getId(), brandOutgoingDto.getId());
        Assertions.assertEquals(brand.getBrand(), brandOutgoingDto.getBrand());
    }

    @Test
    void mapUpdateDto() {
        BrandUpdateDto brandUpdateDto = new BrandUpdateDto(1L, "test");
        Brand brand = brandDtoMapper.map(brandUpdateDto);
        Assertions.assertEquals(brandUpdateDto.getId(), brand.getId());
        Assertions.assertEquals(brandUpdateDto.getBrand(), brand.getBrand());
    }

    @Test
    void mapListBrands() {
        List<Brand> brands = List.of(new Brand(1L, "test"), new Brand(2L, "test"));
        List<BrandOutgoingDto> brandOutgoingDtos = brandDtoMapper.map(brands);
        Assertions.assertEquals(brands.size(), brandOutgoingDtos.size());
    }
}
