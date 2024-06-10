package aston.bootcamp.servlet.mapper.impl;

import aston.bootcamp.entity.Brand;
import aston.bootcamp.servlet.dto.BrandIncomingDto;
import aston.bootcamp.servlet.dto.BrandOutgoingDto;
import aston.bootcamp.servlet.dto.BrandUpdateDto;
import aston.bootcamp.servlet.mapper.BrandDtoMapper;

import java.util.List;

public class BrandDtoMapperImpl implements BrandDtoMapper {
    private static BrandDtoMapper instance;

    public static synchronized BrandDtoMapper getInstance() {
        if (instance == null) {
            instance = new BrandDtoMapperImpl();
        }
        return instance;
    }

    private BrandDtoMapperImpl() {

    }
    @Override
    public Brand map(BrandIncomingDto brandIncomingDto) {
        return new Brand(null, brandIncomingDto.getBrand());
    }

    @Override
    public BrandOutgoingDto map(Brand brand) {
        return new BrandOutgoingDto(brand.getId(), brand.getBrand());
    }

    @Override
    public Brand map(BrandUpdateDto brandUpdateDto) {
        return new Brand(brandUpdateDto.getId(), brandUpdateDto.getBrand());
    }

    @Override
    public List<BrandOutgoingDto> map(List<Brand> brands) {
        return brands.stream().map(this::map).toList();
    }
}
