package aston.bootcamp.servlet.dto;

import java.util.List;

public class DealershipOutgoingDto {
    Long id;
    private String city;
    private String street;
    private Long houseNum;
    List<BikeOutgoingDto> bikes;

    public DealershipOutgoingDto() {
    }

    public DealershipOutgoingDto(Long id, String city, String street, Long houseNum, List<BikeOutgoingDto> bikes) {
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

    public List<BikeOutgoingDto> getBikes() {
        return bikes;
    }
}
