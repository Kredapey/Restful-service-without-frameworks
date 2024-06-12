package aston.bootcamp.servlet.mapper.impl;

import aston.bootcamp.entity.Type;
import aston.bootcamp.servlet.dto.*;
import aston.bootcamp.servlet.mapper.TypeDtoMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class TypeDtoMapperImplTest {
    private static TypeDtoMapper typeDtoMapper;

    @BeforeAll
    static void beforeAll() {
        typeDtoMapper = TypeDtoMapperImpl.getInstance();
    }

    @Test
    void mapIncomingDto() {
        TypeIncomingDto typeIncomingDto = new TypeIncomingDto("test");
        Type type = typeDtoMapper.map(typeIncomingDto);
        Assertions.assertEquals(typeIncomingDto.getType(), type.getType());
    }

    @Test
    void mapOutgoingDto() {
        Type type = new Type(1L, "test");
        TypeOutgoingDto typeOutgoingDto = typeDtoMapper.map(type);
        Assertions.assertEquals(type.getId(), typeOutgoingDto.getId());
        Assertions.assertEquals(type.getType(), typeOutgoingDto.getType());
    }

    @Test
    void mapUpdateDto() {
        TypeUpdateDto typeUpdateDto = new TypeUpdateDto(1L, "test");
        Type type = typeDtoMapper.map(typeUpdateDto);
        Assertions.assertEquals(typeUpdateDto.getId(), type.getId());
        Assertions.assertEquals(typeUpdateDto.getType(), type.getType());
    }

    @Test
    void mapListBrands() {
        List<Type> types = List.of(new Type(1L, "test"), new Type(2L, "test"));
        List<TypeOutgoingDto> typeOutgoingDtos = typeDtoMapper.map(types);
        Assertions.assertEquals(types.size(), typeOutgoingDtos.size());
    }
}
