package aston.bootcamp.servlet.dto;

public class BrandIncomingDto {
    private String brand;

    public BrandIncomingDto() {
    }

    public BrandIncomingDto(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return brand;
    }
}
