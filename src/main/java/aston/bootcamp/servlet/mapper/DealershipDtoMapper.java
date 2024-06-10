package aston.bootcamp.servlet.mapper;

import aston.bootcamp.entity.Dealership;
import aston.bootcamp.servlet.dto.DealershipIncomingDto;
import aston.bootcamp.servlet.dto.DealershipOutgoingDto;
import aston.bootcamp.servlet.dto.DealershipUpdateDto;

import java.util.List;

public interface DealershipDtoMapper {
    Dealership map(DealershipIncomingDto dealershipIncomingDto);
    DealershipOutgoingDto map(Dealership dealership);
    Dealership map(DealershipUpdateDto dealershipUpdateDto);
    List<DealershipOutgoingDto> map(List<Dealership> dealerships);
    List<Dealership> mapUpdateList(List<DealershipUpdateDto> dealershipUpdateDtoList);
}
