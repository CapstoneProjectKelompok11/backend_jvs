package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {
    List<Floor> findAllByBuilding_Id(Long buildingId);

    @Query("SELECT DISTINCT (f.type) FROM Floor f where f.building.id = ?1")
    Set<String> findDistinctTypeByBuilding_Id(Long buildingId);

    int countByBuilding_Id (Long buildingId);
}
