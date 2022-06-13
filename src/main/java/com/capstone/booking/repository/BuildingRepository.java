package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    Page<Building> findAllByComplex_Id(Long complexId, Pageable pageable);
}
