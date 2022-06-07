package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Long> {
    List<Floor> findAllByBuilding_Id(Long buildingId);
}
