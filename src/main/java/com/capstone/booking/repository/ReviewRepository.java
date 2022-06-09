package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByBuildingIdAndUserId (Long buildingId, Long userId);
    Page<Review> findAllByBuilding_Id(Long buildingId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.building.id = ?1")
    Double averageOfBuildingReviewRating(Long buildingId);
}
