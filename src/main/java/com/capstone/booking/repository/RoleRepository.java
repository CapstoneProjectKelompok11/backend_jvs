package com.capstone.booking.repository;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(AppConstant.RoleType name);
}
