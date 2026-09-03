package com.saloon.Respositories;

import com.saloon.models.Saloon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaloonRepository extends JpaRepository<Saloon, Long> {

    Optional<Saloon> findByOwnerId(Long ownerId);

    List<Saloon> findByCity(String city);
    @Query("""
    SELECT s
    FROM Saloon s
    WHERE LOWER(s.name) LIKE CONCAT('%', LOWER(:keyword), '%')
       OR LOWER(s.description) LIKE CONCAT('%', LOWER(:keyword), '%')
       OR LOWER(s.address) LIKE CONCAT('%', LOWER(:keyword), '%')
       OR LOWER(s.city) LIKE CONCAT('%', LOWER(:keyword), '%')
""")
    List<Saloon> searchSaloons(@Param("keyword") String keyword);
}
