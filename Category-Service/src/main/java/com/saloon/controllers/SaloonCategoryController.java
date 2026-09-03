package com.saloon.controllers;

import com.saloon.payloads.dtos.CategoryDto;
import com.saloon.payloads.dtos.SaloonDto;
import com.saloon.services.CategoryService;
import com.saloon.services.clients.SaloonFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/categories/saloon-owner")
@RequiredArgsConstructor
public class SaloonCategoryController {

    private final CategoryService categoryService;
    private final SaloonFeignClient saloonFeignClient;

    @GetMapping("/saloon/{saloonId}/category/{id}")
    public ResponseEntity<CategoryDto> getCategoryByIdAndSaloonId(@PathVariable Long id, @PathVariable Long saloonId) {
        return ResponseEntity.ok(this.categoryService.getCategoryByIdAndSaloonId(id,saloonId));
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto, @RequestHeader("Authorization") String jwt) {
        SaloonDto saloonDto = this.saloonFeignClient.getSaloonsByOwner(jwt).getBody();
        return new ResponseEntity<>(this.categoryService.createCategory(categoryDto, saloonDto), HttpStatus.CREATED);
    }
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(this.categoryService.updateCategory(categoryId, categoryDto));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId, @RequestHeader("Authorization") String jwt) {
        SaloonDto saloonDto = this.saloonFeignClient.getSaloonsByOwner(jwt).getBody();
        this.categoryService.deleteCategory(categoryId, saloonDto.getId());
        return ResponseEntity.ok("Category deleted successfully");
    }
}
