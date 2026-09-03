package com.saloon.services.impl;

import com.saloon.exceptions.BadRequestException;
import com.saloon.exceptions.ResourceNotFoundException;
import com.saloon.mappers.CategoryMapper;
import com.saloon.models.Category;
import com.saloon.payloads.dtos.CategoryDto;
import com.saloon.payloads.dtos.SaloonDto;
import com.saloon.repositories.CategoryRepository;
import com.saloon.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;


    @Override
    public CategoryDto createCategory(CategoryDto categoryDto, SaloonDto saloonDto) {
        categoryDto.setSaloonId(saloonDto.getId());
        Category category = this.categoryRepository.save(CategoryMapper.toCategory(categoryDto));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        CategoryDto existingCategory = this.getCategoryById(categoryId);
        if(!existingCategory.getSaloonId().equals(categoryDto.getSaloonId())) {
            throw new BadRequestException("Id doesn't match");
        }
        existingCategory.setName(categoryDto.getName());
        existingCategory.setImage(categoryDto.getImage());
        existingCategory.setDescription(categoryDto.getDescription());
        existingCategory.setSaloonId(categoryDto.getSaloonId());
        return CategoryMapper.toCategoryDto(this.categoryRepository.save(CategoryMapper.toCategory(existingCategory)));
    }

    @Override
    public void deleteCategory(Long id, Long SaloonId) {
        Category category = CategoryMapper.toCategory(this.getCategoryById(id));
        if(!category.getSaloonId().equals(SaloonId)) {
            throw new BadRequestException("You don't have permission to delete this category.");
        }
        this.categoryRepository.delete(category);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return this.categoryRepository.findById(id).map(CategoryMapper::toCategoryDto).orElseThrow(() -> new ResourceNotFoundException("No Category found with id " + id));
    }

    @Override
    public Set<CategoryDto> getAllCategoryBySaloonId(Long saloonId) {
        Set<CategoryDto> categoryDtos = this.categoryRepository.findBySaloonId(saloonId).stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toSet());
        if(categoryDtos.isEmpty()){
            throw new ResourceNotFoundException("No Category found with saloon id " + saloonId);
        }
        return categoryDtos;
    }

    @Override
    public Set<CategoryDto> getAllCategories() {
        Set<CategoryDto> categoryDtos = this.categoryRepository.findAll().stream().map(CategoryMapper::toCategoryDto).collect(Collectors.toSet());
        if(categoryDtos.isEmpty()){
            throw new ResourceNotFoundException("No Category found");
        }
        return categoryDtos;
    }

    @Override
    public CategoryDto getCategoryByIdAndSaloonId(Long id, Long saloonId) {
        return this.categoryRepository.findByIdAndSaloonId(id, saloonId)
                .map(CategoryMapper::toCategoryDto)
                .orElseThrow(() -> new ResourceNotFoundException("No Category found with id " + id + " and saloon id " + saloonId));
    }
}
