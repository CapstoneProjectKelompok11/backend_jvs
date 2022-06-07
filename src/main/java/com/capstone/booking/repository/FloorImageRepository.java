package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.FloorImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FloorImageRepository extends JpaRepository<FloorImage, Long> {
}
