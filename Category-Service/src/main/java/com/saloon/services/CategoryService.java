package com.saloon.services;

import com.saloon.payloads.dtos.CategoryDto;
import com.saloon.payloads.dtos.SaloonDto;

import java.util.Set;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryDto, SaloonDto saloonDto);
    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);
    void deleteCategory(Long id, Long SaloonId);
    CategoryDto getCategoryById(Long id);
    Set<CategoryDto> getAllCategoryBySaloonId(Long saloonId);
    Set<CategoryDto> getAllCategories();
    CategoryDto getCategoryByIdAndSaloonId(Long id, Long saloonId);
}
