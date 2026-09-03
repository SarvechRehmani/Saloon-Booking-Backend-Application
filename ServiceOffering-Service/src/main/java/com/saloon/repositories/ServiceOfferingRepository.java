package com.saloon.repositories;

import com.saloon.models.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {
    Set<ServiceOffering> findBySaloonId(Long saloonId);
}
