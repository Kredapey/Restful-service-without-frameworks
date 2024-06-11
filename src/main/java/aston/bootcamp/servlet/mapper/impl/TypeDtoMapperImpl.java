package aston.bootcamp.servlet.mapper.impl;

import aston.bootcamp.entity.Type;
import aston.bootcamp.servlet.dto.TypeIncomingDto;
import aston.bootcamp.servlet.dto.TypeOutgoingDto;
import aston.bootcamp.servlet.dto.TypeUpdateDto;
import aston.bootcamp.servlet.mapper.TypeDtoMapper;

import java.util.List;

public class TypeDtoMapperImpl implements TypeDtoMapper {
    private static TypeDtoMapper instance;

    public static synchronized TypeDtoMapper getInstance() {
        if (instance == null) {
            instance = new TypeDtoMapperImpl();
        }
        return instance;
    }

    private TypeDtoMapperImpl() {

    }

    @Override
    public Type map(TypeIncomingDto typeIncomingDto) {
        return new Type(null, typeIncomingDto.getType());
    }

    @Override
    public TypeOutgoingDto map(Type type) {
        return new TypeOutgoingDto(type.getId(), type.getType());
    }

    @Override
    public Type map(TypeUpdateDto typeUpdateDto) {
        return new Type(typeUpdateDto.getId(), typeUpdateDto.getType());
    }

    @Override
    public List<TypeOutgoingDto> map(List<Type> brands) {
        return brands.stream().map(this::map).toList();
    }
}
