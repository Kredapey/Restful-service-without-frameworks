package aston.bootcamp.servlet.dto;

public class TypeOutgoingDto {
    private Long id;
    private String type;

    public TypeOutgoingDto() {
    }

    public TypeOutgoingDto(Long id, String type) {
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
