package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByUserEmailAndBuildingId(String email, Long buildingId);

}
