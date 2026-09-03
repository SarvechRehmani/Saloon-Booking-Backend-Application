package com.saloon.repositories;

import com.saloon.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findBySaloonId(Long saloonId);
    Optional<Category> findByIdAndSaloonId(Long id, Long saloonId);

}
