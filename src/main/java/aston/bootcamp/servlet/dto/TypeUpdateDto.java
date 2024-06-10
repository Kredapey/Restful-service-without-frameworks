package aston.bootcamp.servlet.dto;

public class TypeUpdateDto {
    private Long id;
    private String type;

    public TypeUpdateDto() {
    }

    public TypeUpdateDto(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
