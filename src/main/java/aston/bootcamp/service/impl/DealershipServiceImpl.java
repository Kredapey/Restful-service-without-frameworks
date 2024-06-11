package aston.bootcamp.service.impl;

import aston.bootcamp.entity.BikeToDealership;
import aston.bootcamp.entity.Dealership;
import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.repository.BikeRepository;
import aston.bootcamp.repository.BikeToDealershipRepository;
import aston.bootcamp.repository.DealershipRepository;
import aston.bootcamp.repository.impl.BikeRepositoryImpl;
import aston.bootcamp.repository.impl.BikeToDealershipRepositoryImpl;
import aston.bootcamp.repository.impl.DealershipRepositoryImpl;
import aston.bootcamp.service.DealershipService;
import aston.bootcamp.servlet.dto.DealershipIncomingDto;
import aston.bootcamp.servlet.dto.DealershipOutgoingDto;
import aston.bootcamp.servlet.dto.DealershipUpdateDto;
import aston.bootcamp.servlet.mapper.DealershipDtoMapper;
import aston.bootcamp.servlet.mapper.impl.DealershipDtoMapperImpl;

import java.util.List;

public class DealershipServiceImpl implements DealershipService {
    private static DealershipService instance;
    private final DealershipRepository dealershipRepository = DealershipRepositoryImpl.getInstance();
    private final DealershipDtoMapper dealershipDtoMapper = DealershipDtoMapperImpl.getInstance();
    private final BikeRepository bikeRepository = BikeRepositoryImpl.getInstance();
    private final BikeToDealershipRepository bikeToDealershipRepository = BikeToDealershipRepositoryImpl.getInstance();

    private DealershipServiceImpl() {
    }

    public static synchronized DealershipService getInstance() {
        if (instance == null) {
            instance = new DealershipServiceImpl();
        }
        return instance;
    }

    private void checkDealership(Long id) throws NotFoundException {
        if (!dealershipRepository.existById(id)) {
            throw new NotFoundException("Dealership not found");
        }
    }

    @Override
    public DealershipOutgoingDto save(DealershipIncomingDto dealership) {
        Dealership createDealership = dealershipRepository.save(dealershipDtoMapper.map(dealership));
        return dealershipDtoMapper.map(createDealership);
    }

    @Override
    public void update(DealershipUpdateDto dealership) throws NotFoundException {
        if (dealership == null || dealership.getId() == null) {
            throw new IllegalArgumentException("Incorrect dealership params");
        }
        checkDealership(dealership.getId());
        dealershipRepository.update(dealershipDtoMapper.map(dealership));
    }

    @Override
    public boolean delete(Long id) throws NotFoundException {
        checkDealership(id);
        return dealershipRepository.deleteById(id);
    }

    @Override
    public DealershipOutgoingDto findById(Long id) throws NotFoundException {
        checkDealership(id);
        Dealership dealership = dealershipRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Dealership not found")
        );
        return dealershipDtoMapper.map(dealership);
    }

    @Override
    public List<DealershipOutgoingDto> findAll() {
        List<Dealership> bikes = dealershipRepository.findAll();
        return dealershipDtoMapper.map(bikes);
    }


    @Override
    public void deleteBikeFromDealership(Long dealershipId, Long bikeId) throws NotFoundException {
        checkDealership(dealershipId);
        if (bikeRepository.existById(bikeId)) {
            BikeToDealership bikeToDealership = bikeToDealershipRepository.findByBikeIdAndDealershipId(dealershipId, bikeId).orElseThrow(
                    () -> new NotFoundException("Not found link between dealership and bike")
            );
            bikeToDealershipRepository.deleteById(bikeToDealership.getId());
        } else {
            throw new NotFoundException("Bike not found");
        }
    }

    @Override
    public void addBikeToDealership(Long dealershipId, Long bikeId) throws NotFoundException {
        checkDealership(dealershipId);
        if (bikeRepository.existById(bikeId)) {
            BikeToDealership bikeToDealership = new BikeToDealership(
                    null,
                    dealershipRepository.findById(dealershipId).orElseThrow(
                            () -> new NotFoundException("Can't find dealership")
                    ),
                    bikeRepository.findById(bikeId).orElseThrow(
                            () -> new NotFoundException("Can't find bike")
                    )
            );
            bikeToDealershipRepository.save(bikeToDealership);
        } else {
            throw new NotFoundException("Bike not found");
        }
    }
}
