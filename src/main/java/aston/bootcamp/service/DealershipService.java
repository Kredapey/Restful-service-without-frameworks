package aston.bootcamp.service;

import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.servlet.dto.DealershipIncomingDto;
import aston.bootcamp.servlet.dto.DealershipOutgoingDto;
import aston.bootcamp.servlet.dto.DealershipUpdateDto;

import java.util.List;

public interface DealershipService {
    DealershipOutgoingDto save(DealershipIncomingDto dealership);
    void update(DealershipUpdateDto dealership) throws NotFoundException;
    boolean delete(Long id) throws NotFoundException;
    DealershipOutgoingDto findById(Long id) throws NotFoundException;
    List<DealershipOutgoingDto> findAll();
    void deleteBikeFromDealership(Long dealershipId, Long bikeId) throws NotFoundException;
    void addBikeToDealership(Long dealershipId, Long bikeId) throws NotFoundException;
}
