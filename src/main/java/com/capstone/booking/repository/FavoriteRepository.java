package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT f FROM Favorite f WHERE f.user.email = ?1 AND f.building.isDeleted = false AND f.isFavorite = true")
    List<Favorite> findFavoriteByUserEmailAndBuildingIsDeletedFalse(String email);

    @Query("SELECT f FROM Favorite f WHERE f.user.email = ?1 AND f.building.id = ?2 AND f.building.isDeleted = false ")
    Optional<Favorite> findFavorite(String email, Long buildingId);
}
