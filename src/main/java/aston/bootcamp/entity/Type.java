package aston.bootcamp.entity;

import java.util.Objects;

/**
 * One to many type -> bike
 */
public class Type {
    private Long id;
    private String type;

    public Type() {
    }

    public Type(Long id, String type) {
        this.id = id;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type1 = (Type) o;
        return Objects.equals(id, type1.id) && Objects.equals(type, type1.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public String toString() {
        return "Type{" +
               "id=" + id +
               ", type='" + type + '\'' +
               '}';
    }
}
