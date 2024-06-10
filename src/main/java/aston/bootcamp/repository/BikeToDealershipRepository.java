package aston.bootcamp.repository;

import aston.bootcamp.entity.Bike;
import aston.bootcamp.entity.BikeToDealership;
import aston.bootcamp.entity.Dealership;

import java.util.List;
import java.util.Optional;

public interface BikeToDealershipRepository extends Repository<BikeToDealership, Long>{
    List<BikeToDealership> findAllByBikeId(Long bikeId);
    List<BikeToDealership> findAllByDealershipId(Long dealershipId);
    List<Bike> findAllBikesByDealershipId(Long dealershipId);
    List<Dealership> findAllDealershipsByBikeId(Long bikeId);
    Optional<BikeToDealership> findByBikeIdAndDealershipId(Long dealershipId, Long bikeId);
    boolean deleteByBikeId(Long bikeId);
    boolean deleteByDealershipId(Long dealershipId);
}
