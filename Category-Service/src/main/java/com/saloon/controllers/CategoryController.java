package com.saloon.controllers;

import com.saloon.payloads.dtos.CategoryDto;
import com.saloon.payloads.dtos.SaloonDto;
import com.saloon.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Set<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(this.categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(this.categoryService.getCategoryById(id));
    }

    @GetMapping("/saloon/{saloonId}")
    public ResponseEntity<Set<CategoryDto>> getAllCategoryBySaloonId(@PathVariable Long saloonId) {
        return ResponseEntity.ok(this.categoryService.getAllCategoryBySaloonId(saloonId));
    }
}
