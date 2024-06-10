package aston.bootcamp.service;

import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.servlet.dto.TypeIncomingDto;
import aston.bootcamp.servlet.dto.TypeOutgoingDto;
import aston.bootcamp.servlet.dto.TypeUpdateDto;

import java.util.List;

public interface TypeService {
    TypeOutgoingDto save(TypeIncomingDto type);
    void update(TypeUpdateDto type) throws NotFoundException;
    boolean delete(Long id) throws NotFoundException;
    TypeOutgoingDto findById(Long id) throws NotFoundException;
    List<TypeOutgoingDto> findAll();
}
