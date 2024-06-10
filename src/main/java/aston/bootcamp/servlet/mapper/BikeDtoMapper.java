package aston.bootcamp.servlet.mapper;

import aston.bootcamp.entity.Bike;
import aston.bootcamp.servlet.dto.BikeIncomingDto;
import aston.bootcamp.servlet.dto.BikeOutgoingDto;
import aston.bootcamp.servlet.dto.BikeUpdateDto;

import java.util.List;

public interface BikeDtoMapper {
    Bike map(BikeIncomingDto bikeIncomingDto);
    BikeOutgoingDto map(Bike bike);
    Bike map(BikeUpdateDto bikeUpdateDto);
    List<BikeOutgoingDto> map(List<Bike> bikes);
    List<Bike> mapUpdateList(List<BikeUpdateDto> bikeUpdateDtoList);
}
