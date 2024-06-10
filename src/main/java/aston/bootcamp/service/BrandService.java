package aston.bootcamp.service;

import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.servlet.dto.BrandIncomingDto;
import aston.bootcamp.servlet.dto.BrandOutgoingDto;
import aston.bootcamp.servlet.dto.BrandUpdateDto;

import java.util.List;

public interface BrandService {
    BrandOutgoingDto save(BrandIncomingDto brand);
    void update(BrandUpdateDto brand) throws NotFoundException;
    boolean delete(Long id) throws NotFoundException;
    BrandOutgoingDto findById(Long id) throws NotFoundException;
    List<BrandOutgoingDto> findAll();
}
