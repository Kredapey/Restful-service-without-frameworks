package aston.bootcamp.servlet.mapper.impl;

import aston.bootcamp.entity.Bike;
import aston.bootcamp.servlet.dto.BikeIncomingDto;
import aston.bootcamp.servlet.dto.BikeOutgoingDto;
import aston.bootcamp.servlet.dto.BikeUpdateDto;
import aston.bootcamp.servlet.mapper.BikeDtoMapper;
import aston.bootcamp.servlet.mapper.BrandDtoMapper;
import aston.bootcamp.servlet.mapper.DealershipDtoMapper;
import aston.bootcamp.servlet.mapper.TypeDtoMapper;

import java.util.List;

public class BikeDtoMapperImpl implements BikeDtoMapper {
    private static BikeDtoMapper instance;
    private static final BrandDtoMapper brandDtoMapper = BrandDtoMapperImpl.getInstance();
    private static final TypeDtoMapper typeDtoMapper = TypeDtoMapperImpl.getInstance();
    private static final DealershipDtoMapper dealershipDtoMapper = DealershipDtoMapperImpl.getInstance();

    public static synchronized BikeDtoMapper getInstance() {
        if (instance == null) {
            instance = new BikeDtoMapperImpl();
        }
        return instance;
    }

    private BikeDtoMapperImpl() {

    }

    @Override
    public Bike map(BikeIncomingDto bikeIncomingDto) {
        return new Bike(null,
                bikeIncomingDto.getType(),
                bikeIncomingDto.getBrand(),
                bikeIncomingDto.getModel(),
                bikeIncomingDto.getCost(),
                null);
    }

    @Override
    public BikeOutgoingDto map(Bike bike) {
        return new BikeOutgoingDto(
                bike.getId(),
                typeDtoMapper.map(bike.getType()),
                brandDtoMapper.map(bike.getBrand()),
                bike.getModel(),
                bike.getCost(),
                dealershipDtoMapper.map(bike.getDealerships())
        );
    }

    @Override
    public Bike map(BikeUpdateDto bikeUpdateDto) {
        return new Bike(
                bikeUpdateDto.getId(),
                typeDtoMapper.map(bikeUpdateDto.getType()),
                brandDtoMapper.map(bikeUpdateDto.getBrand()),
                bikeUpdateDto.getModel(),
                bikeUpdateDto.getCost(),
                dealershipDtoMapper.mapUpdateList(bikeUpdateDto.getDealerships())
        );
    }

    @Override
    public List<BikeOutgoingDto> map(List<Bike> bikes) {
        return bikes.stream().map(this::map).toList();
    }

    @Override
    public List<Bike> mapUpdateList(List<BikeUpdateDto> bikeUpdateDtoList) {
        return bikeUpdateDtoList.stream().map(this::map).toList();
    }
}
