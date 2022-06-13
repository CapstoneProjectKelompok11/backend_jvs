package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.dao.City;
import com.capstone.booking.domain.dto.CityRequest;
import com.capstone.booking.repository.CityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CityService.class)
class CityServiceTest {

    @MockBean
    private CityRepository cityRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private CityService cityService;

    @Test
    void getAllCity_Success_Test() {
        List<City> cityList = new ArrayList<>();
        City city = City.builder()
                .id(1L)
                .name("City")
                .build();

        cityList.add(city);

        CityRequest cityRequest = CityRequest.builder()
                .id(1L)
                .cityName("City")
                .build();

        when(cityRepository.findAll()).thenReturn(cityList);
        when(modelMapper.map(any(), eq(CityRequest.class))).thenReturn(cityRequest);

        ResponseEntity responseEntity = cityService.getAllCity();
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        List<CityRequest> result = ((List<CityRequest>) apiResponse.getData());

        assertEquals(1L, result.get(0).getId());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void getAll_Error_Test() {
        when(cityRepository.findAll()).thenThrow(NullPointerException.class);
        ResponseEntity<Object> responseEntity = cityService.getAllCity();
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());
        assertEquals(AppConstant.ResponseCode.UNKNOWN_ERROR.getCode(), apiResponse.getStatus().getCode());
    }

    @Test
    void addNewCity_Success_Test() {
        CityRequest cityRequest = CityRequest.builder()
                .id(1L)
                .cityName("New City")
                .build();

        City city = City.builder()
                .id(1L)
                .name("New City")
                .build();
        when(modelMapper.map(any(), eq(City.class))).thenReturn(city);
        when(cityRepository.save(any())).thenReturn(city);
        when(modelMapper.map(any(), eq(CityRequest.class))).thenReturn(cityRequest);

        ResponseEntity<Object> responseEntity = cityService.addNewCity(cityRequest);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("New City", ((CityRequest) apiResponse.getData()).getCityName());

    }

    @Test
    void addNew_Error_Test() {
        City city = City.builder()
                .id(1L)
                .name("New City")
                .build();

        CityRequest cityRequest = CityRequest.builder()
                .id(1L)
                .cityName("New City")
                .build();

        when(modelMapper.map(any(), eq(City.class))).thenReturn(city);
        when(cityRepository.save(any())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = cityService.addNewCity(cityRequest);

        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(AppConstant.ResponseCode.UNKNOWN_ERROR.getCode(), apiResponse.getStatus().getCode());
    }
}