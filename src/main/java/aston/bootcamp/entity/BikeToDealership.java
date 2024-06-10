package aston.bootcamp.entity;


/**
 * Класс для связи между сущностями bike и dealership
 */
public class BikeToDealership {
    private Long id;
    private Dealership dealership;
    private Bike bike;

    public BikeToDealership() {
    }

    public BikeToDealership(Long id, Dealership dealership, Bike bike) {
        this.id = id;
        this.dealership = dealership;
        this.bike = bike;
    }

    public Long getId() {
        return id;
    }

    public Dealership getDealership() {
        return dealership;
    }

    public void setDealership(Dealership dealership) {
        this.dealership = dealership;
    }

    public Bike getBike() {
        return bike;
    }

    public void setBike(Bike bike) {
        this.bike = bike;
    }

    @Override
    public String toString() {
        return "BikeToDealership{" +
               "id=" + id +
               ", dealership=" + dealership +
               ", bike=" + bike +
               '}';
    }
}
