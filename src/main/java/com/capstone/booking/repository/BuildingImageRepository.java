package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.BuildingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BuildingImageRepository extends JpaRepository<BuildingImage, Long> {
}
