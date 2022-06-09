package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.City;
import com.capstone.booking.domain.dto.CityRequest;
import com.capstone.booking.repository.CityRepository;
import com.capstone.booking.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<Object> getAllCity() {
        log.info("Executing get all city");
        try {
            List<City> cityList = cityRepository.findAll();
            List<CityRequest> cityRequests = new ArrayList<>();
            for (City city :
                    cityList) {
                cityRequests.add(modelMapper.map(city, CityRequest.class));
            }

            log.info("Successfully retrieved all City");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    cityRequests,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to get all city. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> addNewCity(CityRequest req) {
        log.info("Executing create new City");
        try {
            City city = modelMapper.map(req, City.class);
            cityRepository.save(city);
            log.info("Successfully added new city");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, modelMapper.map(city, CityRequest.class), HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred while trying to add new city. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
