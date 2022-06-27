package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Complex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplexRepository extends JpaRepository<Complex, Long> {
    List<Complex> findAllByCity_Name(String name);
}
