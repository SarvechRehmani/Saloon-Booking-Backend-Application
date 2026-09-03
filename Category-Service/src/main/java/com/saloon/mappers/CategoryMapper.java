package com.saloon.mappers;

import com.saloon.models.Category;
import com.saloon.payloads.dtos.CategoryDto;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setImage(category.getImage());
        dto.setDescription(category.getDescription());
        dto.setSaloonId(category.getSaloonId());
        return dto;
    }

    public static Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setImage(categoryDto.getImage());
        category.setDescription(categoryDto.getDescription());
        category.setSaloonId(categoryDto.getSaloonId());
        return category;
    }
}
