package aston.bootcamp.entity;

import java.util.List;
import java.util.Objects;

/**
 * Many to many dealership <-> bike
 */
public class Dealership {
    private Long id;
    private String city;
    private String street;
    private Long houseNum;
    private List<Bike> bikes;

    public Dealership() {
    }

    public Dealership(Long id, String city, String street, Long houseNum, List<Bike> bikes) {
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

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Long getHouseNum() {
        return houseNum;
    }

    public void setHouseNum(Long houseNum) {
        this.houseNum = houseNum;
    }

    public List<Bike> getBikes() {
        return bikes;
    }

    public void setBikes(List<Bike> bikes) {
        this.bikes = bikes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dealership that = (Dealership) o;
        return Objects.equals(id, that.id) && Objects.equals(city, that.city) && Objects.equals(street, that.street) && Objects.equals(houseNum, that.houseNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, street, houseNum, bikes);
    }

    @Override
    public String toString() {
        return "Dealership{" +
               "id=" + id +
               ", city='" + city + '\'' +
               ", street='" + street + '\'' +
               ", houseNum=" + houseNum +
               '}';
    }
}
