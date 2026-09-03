package com.saloon.services.clients;

import com.saloon.dtos.CategoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("CATEGORY-SERVICE")
public interface CategoryFeignClient {
//    @GetMapping("/api/categories/{id}")
//    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id);
    @GetMapping("/api/categories/saloon-owner/saloon/{saloonId}/category/{id}")
    public ResponseEntity<CategoryDto> getCategoryByIdAndSaloonId(@PathVariable Long id, @PathVariable Long saloonId);
}
