package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
