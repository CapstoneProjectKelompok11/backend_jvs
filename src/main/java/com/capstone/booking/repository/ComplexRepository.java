package com.capstone.booking.repository;

import com.capstone.booking.domain.dao.Complex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplexRepository extends JpaRepository<Complex, Long>, JpaSpecificationExecutor<Complex> {
    List<Complex> findAllByCity_Name(String name);
}
