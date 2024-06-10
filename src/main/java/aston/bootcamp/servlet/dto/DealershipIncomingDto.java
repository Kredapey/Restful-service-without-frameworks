package aston.bootcamp.servlet.dto;

public class DealershipIncomingDto {
    private String city;
    private String street;
    private Long houseNum;

    public DealershipIncomingDto() {
    }

    public DealershipIncomingDto(String city, String street, Long houseNum) {
        this.city = city;
        this.street = street;
        this.houseNum = houseNum;
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
}
