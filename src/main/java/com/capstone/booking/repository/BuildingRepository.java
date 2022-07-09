package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Building;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long>,
        JpaSpecificationExecutor<Building> {
    Page<Building> findAllByComplex_Id(Long complexId, Pageable pageable);

    List<Building> findAllByComplex_Id(Long complexId);

    @Query("SELECT DISTINCT (c.building) FROM Chat c WHERE c.user.email = ?1")
    Set<Building> findAllByChat(String email);

    List<Building> findAllByNameContainsIgnoreCase(String name);
}
