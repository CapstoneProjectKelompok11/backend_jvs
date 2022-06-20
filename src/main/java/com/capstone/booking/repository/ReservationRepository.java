package com.capstone.booking.repository;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.user.id = ?1 AND (r.status = ?2 OR r.status = ?3)")
    List<Reservation> findAllReservationForUser(Long userId,
                                                AppConstant.ReservationStatus waiting,
                                                AppConstant.ReservationStatus pending);
}
