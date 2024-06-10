package aston.bootcamp.servlet.mapper;


import aston.bootcamp.entity.Brand;
import aston.bootcamp.servlet.dto.BrandIncomingDto;
import aston.bootcamp.servlet.dto.BrandOutgoingDto;
import aston.bootcamp.servlet.dto.BrandUpdateDto;

import java.util.List;

public interface BrandDtoMapper {
    Brand map(BrandIncomingDto brandIncomingDto);
    BrandOutgoingDto map(Brand brand);
    Brand map(BrandUpdateDto brandUpdateDto);
    List<BrandOutgoingDto> map(List<Brand> brands);
}
