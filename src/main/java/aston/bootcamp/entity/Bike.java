package aston.bootcamp.entity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Many to one bike -> brand
 * Many to one bike -> type
 * Many to many bike <-> dealership
 */
public class Bike {
    private Long id;
    private Type type;
    private Brand brand;
    private String model;
    private Long cost;
    private List<Dealership> dealerships;

    public Bike() {
    }

    public Bike(Long id, Type type, Brand brand, String model, Long cost, List<Dealership> dealerships) {
        this.id = id;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.cost = cost;
        this.dealerships = dealerships;
    }

    public Long getId() {
        return id;
    }
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public List<Dealership> getDealerships() {
        return dealerships;
    }

    public void setDealerships(List<Dealership> dealerships) {
        this.dealerships = dealerships;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bike bike = (Bike) o;
        return Objects.equals(id, bike.id) && Objects.equals(type, bike.type) && Objects.equals(brand, bike.brand) && Objects.equals(model, bike.model) && Objects.equals(cost, bike.cost);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, brand, model, cost, dealerships);
    }

    @Override
    public String toString() {
        return "Bike{" +
               "id=" + id +
               ", type=" + type +
               ", brand=" + brand +
               ", model='" + model + '\'' +
               ", cost=" + cost +
               '}';
    }
}
