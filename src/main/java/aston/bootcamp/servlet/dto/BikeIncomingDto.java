package aston.bootcamp.servlet.dto;

import aston.bootcamp.entity.Brand;
import aston.bootcamp.entity.Type;

public class BikeIncomingDto {
    private Type type;
    private Brand brand;
    private String model;
    private Long cost;

    public BikeIncomingDto() {
    }

    public BikeIncomingDto(Type type, Brand brand, String model, Long cost) {
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.cost = cost;
    }

    public Type getType() {
        return type;
    }

    public Brand getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Long getCost() {
        return cost;
    }
}
