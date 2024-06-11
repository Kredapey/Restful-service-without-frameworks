package aston.bootcamp.service.impl;

import aston.bootcamp.entity.Type;
import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.repository.TypeRepository;
import aston.bootcamp.repository.impl.TypeRepositoryImpl;
import aston.bootcamp.service.TypeService;
import aston.bootcamp.servlet.dto.TypeIncomingDto;
import aston.bootcamp.servlet.dto.TypeOutgoingDto;
import aston.bootcamp.servlet.dto.TypeUpdateDto;
import aston.bootcamp.servlet.mapper.TypeDtoMapper;
import aston.bootcamp.servlet.mapper.impl.TypeDtoMapperImpl;

import java.util.List;

public class TypeServiceImpl implements TypeService {
    private static TypeService instance;
    private final TypeRepository typeRepository = TypeRepositoryImpl.getInstance();
    private final TypeDtoMapper typeDtoMapper = TypeDtoMapperImpl.getInstance();

    private TypeServiceImpl() {
    }

    public static synchronized TypeService getInstance() {
        if (instance == null) {
            instance = new TypeServiceImpl();
        }
        return instance;
    }

    private void checkType(Long typeId) throws NotFoundException {
        if (!typeRepository.existById(typeId)) {
            throw new NotFoundException("Type not found");
        }
    }

    @Override
    public TypeOutgoingDto save(TypeIncomingDto type) {
        Type createType = typeRepository.save(typeDtoMapper.map(type));
        return typeDtoMapper.map(createType);
    }

    @Override
    public void update(TypeUpdateDto type) throws NotFoundException {
        if (type == null || type.getId() == null) {
            throw new IllegalArgumentException("Incorrect type params");
        }
        checkType(type.getId());
        typeRepository.update(typeDtoMapper.map(type));
    }

    @Override
    public boolean delete(Long id) throws NotFoundException {
        checkType(id);
        return typeRepository.deleteById(id);
    }

    @Override
    public TypeOutgoingDto findById(Long id) throws NotFoundException {
        checkType(id);
        Type type = typeRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Type not found")
        );
        return typeDtoMapper.map(type);
    }

    @Override
    public List<TypeOutgoingDto> findAll() {
        List<Type> bikes = typeRepository.findAll();
        return typeDtoMapper.map(bikes);
    }
}
