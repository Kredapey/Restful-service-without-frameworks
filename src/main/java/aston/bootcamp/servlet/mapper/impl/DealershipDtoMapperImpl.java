package aston.bootcamp.servlet.mapper.impl;

import aston.bootcamp.entity.Dealership;
import aston.bootcamp.servlet.dto.DealershipIncomingDto;
import aston.bootcamp.servlet.dto.DealershipOutgoingDto;
import aston.bootcamp.servlet.dto.DealershipUpdateDto;
import aston.bootcamp.servlet.mapper.BikeDtoMapper;
import aston.bootcamp.servlet.mapper.DealershipDtoMapper;

import java.util.List;

public class DealershipDtoMapperImpl implements DealershipDtoMapper {
    private static DealershipDtoMapper instance;
    private static final BikeDtoMapper bikeDtoMapper = BikeDtoMapperImpl.getInstance();

    public static synchronized DealershipDtoMapper getInstance() {
        if (instance == null) {
            instance = new DealershipDtoMapperImpl();
        }
        return instance;
    }

    private DealershipDtoMapperImpl() {

    }
    @Override
    public Dealership map(DealershipIncomingDto dealershipIncomingDto) {
        return new Dealership(null,
                dealershipIncomingDto.getCity(),
                dealershipIncomingDto.getStreet(),
                dealershipIncomingDto.getHouseNum(),
                null);
    }

    @Override
    public DealershipOutgoingDto map(Dealership dealership) {
        return new DealershipOutgoingDto(
                dealership.getId(),
                dealership.getCity(),
                dealership.getStreet(),
                dealership.getHouseNum(),
                bikeDtoMapper.map(dealership.getBikes())
        );
    }

    @Override
    public Dealership map(DealershipUpdateDto dealershipUpdateDto) {
        return new Dealership(
                dealershipUpdateDto.getId(),
                dealershipUpdateDto.getCity(),
                dealershipUpdateDto.getStreet(),
                dealershipUpdateDto.getHouseNum(),
                bikeDtoMapper.mapUpdateList(dealershipUpdateDto.getBikes())
        );
    }

    @Override
    public List<DealershipOutgoingDto> map(List<Dealership> dealerships) {
        return dealerships.stream().map(this::map).toList();
    }

    @Override
    public List<Dealership> mapUpdateList(List<DealershipUpdateDto> dealershipUpdateDtoList) {
        return dealershipUpdateDtoList.stream().map(this::map).toList();
    }
}
