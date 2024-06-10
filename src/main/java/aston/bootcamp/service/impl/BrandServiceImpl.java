package aston.bootcamp.service.impl;

import aston.bootcamp.entity.Bike;
import aston.bootcamp.entity.Brand;
import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.repository.BrandRepository;
import aston.bootcamp.repository.impl.BrandRepositoryImpl;
import aston.bootcamp.service.BrandService;
import aston.bootcamp.service.TypeService;
import aston.bootcamp.servlet.dto.BrandIncomingDto;
import aston.bootcamp.servlet.dto.BrandOutgoingDto;
import aston.bootcamp.servlet.dto.BrandUpdateDto;
import aston.bootcamp.servlet.mapper.BrandDtoMapper;
import aston.bootcamp.servlet.mapper.impl.BrandDtoMapperImpl;

import java.util.List;

public class BrandServiceImpl implements BrandService {
    private static BrandService instance;
    private final BrandRepository brandRepository = BrandRepositoryImpl.getInstance();
    private final BrandDtoMapper brandDtoMapper = BrandDtoMapperImpl.getInstance();

    public static synchronized BrandService getInstance() {
        if (instance == null) {
            instance = new BrandServiceImpl();
        }
        return instance;
    }
    private BrandServiceImpl() {
    }

    private void checkBrand(Long brandId) throws NotFoundException {
        if (!brandRepository.existById(brandId)) {
            throw new NotFoundException("Brand not found");
        }
    }

    @Override
    public BrandOutgoingDto save(BrandIncomingDto brand) {
        Brand createBrand = brandRepository.save(brandDtoMapper.map(brand));
        return brandDtoMapper.map(createBrand);
    }

    @Override
    public void update(BrandUpdateDto brand) throws NotFoundException {
        if (brand == null || brand.getId() == null) {
            throw new IllegalArgumentException("Incorrect brand params");
        }
        checkBrand(brand.getId());
        brandRepository.update(brandDtoMapper.map(brand));
    }

    @Override
    public boolean delete(Long id) throws NotFoundException {
        checkBrand(id);
        return brandRepository.deleteById(id);
    }

    @Override
    public BrandOutgoingDto findById(Long id) throws NotFoundException {
        checkBrand(id);
        Brand brand = brandRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Brand not found")
        );
        return brandDtoMapper.map(brand);
    }

    @Override
    public List<BrandOutgoingDto> findAll() {
        List<Brand> bikes = brandRepository.findAll();
        return brandDtoMapper.map(bikes);
    }
}
