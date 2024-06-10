package aston.bootcamp.servlet.mapper;

import aston.bootcamp.entity.Type;
import aston.bootcamp.servlet.dto.TypeIncomingDto;
import aston.bootcamp.servlet.dto.TypeOutgoingDto;
import aston.bootcamp.servlet.dto.TypeUpdateDto;

import java.util.List;

public interface TypeDtoMapper {
    Type map(TypeIncomingDto typeIncomingDto);
    TypeOutgoingDto map(Type type);
    Type map(TypeUpdateDto typeUpdateDto);
    List<TypeOutgoingDto> map(List<Type> brands);
}
