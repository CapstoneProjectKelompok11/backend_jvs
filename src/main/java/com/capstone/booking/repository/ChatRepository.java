package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findAllByUserEmailAndBuildingId(String email, Long buildingId);

//    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Chat c " +
//            "WHERE c.user.email = ?1 AND c.building.id = ?2 AND c.isRead = false")
//    Boolean haveNewMessage(String email, Long buildingId);
}
