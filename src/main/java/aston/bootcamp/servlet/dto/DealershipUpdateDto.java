package aston.bootcamp.servlet.dto;

import java.util.List;

public class DealershipUpdateDto {
    Long id;
    private String city;
    private String street;
    private Long houseNum;
    private List<BikeUpdateDto> bikes;

    public DealershipUpdateDto() {
    }

    public DealershipUpdateDto(Long id, String city, String street, Long houseNum, List<BikeUpdateDto> bikes) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.houseNum = houseNum;
        this.bikes = bikes;
    }

    public Long getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public Long getHouseNum() {
        return houseNum;
    }

    public List<BikeUpdateDto> getBikes() {
        return bikes;
    }
}
