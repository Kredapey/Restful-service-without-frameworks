package aston.bootcamp.servlet.dto;

public class BrandOutgoingDto {
    private Long id;
    private String brand;

    public BrandOutgoingDto() {
    }

    public BrandOutgoingDto(Long id, String brand) {
        this.id = id;
        this.brand = brand;
    }

    public Long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }
}
