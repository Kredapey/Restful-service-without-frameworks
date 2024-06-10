package aston.bootcamp.servlet.dto;

import java.util.List;

public class BikeOutgoingDto {
    private Long id;
    private TypeOutgoingDto type;
    private BrandOutgoingDto brand;
    private String model;
    private Long cost;
    private List<DealershipOutgoingDto> dealerships;

    public BikeOutgoingDto() {
    }

    public BikeOutgoingDto(Long id, TypeOutgoingDto type, BrandOutgoingDto brand, String model, Long cost, List<DealershipOutgoingDto> dealerships) {
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

    public TypeOutgoingDto getType() {
        return type;
    }

    public BrandOutgoingDto getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public Long getCost() {
        return cost;
    }

    public List<DealershipOutgoingDto> getDealerships() {
        return dealerships;
    }
}
