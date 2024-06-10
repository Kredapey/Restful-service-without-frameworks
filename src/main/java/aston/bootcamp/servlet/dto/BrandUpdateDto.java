package aston.bootcamp.servlet.dto;

public class BrandUpdateDto {
    private Long id;
    private String brand;

    public BrandUpdateDto() {
    }

    public BrandUpdateDto(Long id, String brand) {
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
