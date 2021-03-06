package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail (String email);
    boolean existsByEmail (String email);
}
