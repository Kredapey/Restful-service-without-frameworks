package aston.bootcamp.servlet.dto;

import java.util.List;

public class BikeUpdateDto {
    private Long id;
    private TypeUpdateDto type;
    private BrandUpdateDto brand;
    private String model;
    private Long cost;
    private List<DealershipUpdateDto> dealerships;

    public BikeUpdateDto() {
    }

    public BikeUpdateDto(Long id, TypeUpdateDto type, BrandUpdateDto brand, String model, Long cost, List<DealershipUpdateDto> dealerships) {
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

    public TypeUpdateDto getType() {
        return type;
    }

    public BrandUpdateDto getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Long getCost() {
        return cost;
    }

    public List<DealershipUpdateDto> getDealerships() {
        return dealerships;
    }
}
