package com.capstone.booking.repository;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>{

    List<Reservation> findAllByUser_IdAndStatusIsNot(Long userId, AppConstant.ReservationStatus status);

    List<Reservation> findAllByStatusIs(AppConstant.ReservationStatus status);

    List<Reservation> findAllByStatusIsNot(AppConstant.ReservationStatus status);
}
