package aston.bootcamp.service;

import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.servlet.dto.*;

import java.util.List;

public interface BikeService {
    BikeOutgoingDto save(BikeIncomingDto bike);
    void update(BikeUpdateDto bike) throws NotFoundException;
    boolean delete(Long bikeId) throws NotFoundException;
    BikeOutgoingDto findById(Long bikeId) throws NotFoundException;
    List<BikeOutgoingDto> findAll();
}
