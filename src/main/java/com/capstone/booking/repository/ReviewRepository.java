package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    @Query("SELECT r FROM Review r WHERE r.building.id = ?1 AND r.isApproved = TRUE ")
    Page<Review> findAllByBuildingId(Long buildingId, Pageable pageable);

    Optional<Review> findByUserIdAndBuildingId(Long userId, Long buildingId);
    List<Review> findAllByIsApprovedIsNull();

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.building.id = ?1 AND r.isApproved = TRUE")
    Double averageOfBuildingReviewRating(Long buildingId);
}
