package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.City;
import com.capstone.booking.domain.dao.Complex;
import com.capstone.booking.domain.dto.ComplexRequest;
import com.capstone.booking.repository.CityRepository;
import com.capstone.booking.repository.ComplexRepository;
import com.capstone.booking.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ComplexService {

    @Autowired
    private ComplexRepository complexRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<Object> getAllComplex() {
        log.info("Executing get all complex");
        try {
            List<Complex> complexList = complexRepository.findAll();
            List<ComplexRequest> complexRequests = new ArrayList<>();
            for (Complex complex :
                    complexList) {
                complexRequests.add(modelMapper.map(complex, ComplexRequest.class));
            }

            log.info("Successfully retrieved all Complex");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    complexRequests,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to get all complex. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> addNewCity(ComplexRequest req, Long cityId) {
        log.info("Executing add new City");
        try {
            //Checking city exist or not on database
            Optional<City> city = cityRepository.findById(cityId);
            if(city.isEmpty()) {
                log.info("Complex with ID [{}] not found ", req.getCity().getId());
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Complex complex = modelMapper.map(req, Complex.class);
            complex.setCity(city.get());
            complexRepository.save(complex);

            log.info("Successfully added new Complex");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(complex, ComplexRequest.class),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred while trying to add new complex. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
