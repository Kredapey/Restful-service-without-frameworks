package aston.bootcamp.service.impl;

import aston.bootcamp.entity.Bike;
import aston.bootcamp.entity.Brand;
import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.repository.BikeRepository;
import aston.bootcamp.repository.impl.BikeRepositoryImpl;
import aston.bootcamp.service.BikeService;
import aston.bootcamp.servlet.dto.*;
import aston.bootcamp.servlet.mapper.BikeDtoMapper;
import aston.bootcamp.servlet.mapper.BrandDtoMapper;
import aston.bootcamp.servlet.mapper.TypeDtoMapper;
import aston.bootcamp.servlet.mapper.impl.BikeDtoMapperImpl;
import aston.bootcamp.servlet.mapper.impl.BrandDtoMapperImpl;
import aston.bootcamp.servlet.mapper.impl.TypeDtoMapperImpl;

import java.util.List;

public class BikeServiceImpl implements BikeService {
    private static BikeService instance;
    private final BikeRepository bikeRepository = BikeRepositoryImpl.getInstance();
    private final BikeDtoMapper bikeDtoMapper = BikeDtoMapperImpl.getInstance();

    public static synchronized BikeService getInstance() {
        if (instance == null) {
            instance = new BikeServiceImpl();
        }
        return instance;
    }

    private BikeServiceImpl() {
    }

    private void checkBike(Long id) throws NotFoundException {
        if (!bikeRepository.existById(id)) {
            throw new NotFoundException("Bike not found");
        }
    }

    @Override
    public BikeOutgoingDto save(BikeIncomingDto bike) {
        Bike createBike = bikeRepository.save(bikeDtoMapper.map(bike));
        return bikeDtoMapper.map(createBike);
    }

    @Override
    public void update(BikeUpdateDto bike) throws NotFoundException {
        if (bike == null || bike.getId() == null) {
            throw new IllegalArgumentException("Incorrect bike params");
        }
        checkBike(bike.getId());
        bikeRepository.update(bikeDtoMapper.map(bike));
    }

    @Override
    public boolean delete(Long bikeId) throws NotFoundException {
        checkBike(bikeId);
        return bikeRepository.deleteById(bikeId);
    }

    @Override
    public BikeOutgoingDto findById(Long bikeId) throws NotFoundException {
        checkBike(bikeId);
        Bike bike = bikeRepository.findById(bikeId).orElseThrow(
                () -> new NotFoundException("Bike not found")
        );
        return bikeDtoMapper.map(bike);
    }

    @Override
    public List<BikeOutgoingDto> findAll() {
        List<Bike> bikes = bikeRepository.findAll();
        return bikeDtoMapper.map(bikes);
    }

}
