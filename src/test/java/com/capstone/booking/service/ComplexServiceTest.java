package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.dao.City;
import com.capstone.booking.domain.dao.Complex;
import com.capstone.booking.domain.dto.CityRequest;
import com.capstone.booking.domain.dto.ComplexRequest;
import com.capstone.booking.repository.CityRepository;
import com.capstone.booking.repository.ComplexRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ComplexService.class)
class ComplexServiceTest {

    @MockBean
    private ComplexRepository complexRepository;

    @MockBean
    private CityRepository cityRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ComplexService complexService;

    @Test
    void getAllComplex_Success_Test() {
        List<Complex> complexList = new ArrayList<>();
        Complex complex = Complex.builder()
                .name("Komplek")
                .build();

        complexList.add(complex);

        ComplexRequest complexRequest = ComplexRequest.builder()
                .complexName("Komplek")
                .build();

        when(complexRepository.findAllByCity_Name(anyString())).thenReturn(complexList);
        when(modelMapper.map(any(), eq(ComplexRequest.class))).thenReturn(complexRequest);

        ResponseEntity responseEntity = complexService.getAllComplex("City");
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        List<ComplexRequest> result = ((List<ComplexRequest>) apiResponse.getData());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Komplek", result.get(0).getComplexName());

    }

    @Test
    void getAllComplex_NoCity_Test() {
        List<Complex> complexList = new ArrayList<>();
        Complex complex = Complex.builder()
                .name("Komplek")
                .build();

        complexList.add(complex);

        ComplexRequest complexRequest = ComplexRequest.builder()
                .complexName("Komplek")
                .build();

        when(complexRepository.findAll()).thenReturn(complexList);
        when(modelMapper.map(any(), eq(ComplexRequest.class))).thenReturn(complexRequest);

        ResponseEntity responseEntity = complexService.getAllComplex(null);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        List<ComplexRequest> result = ((List<ComplexRequest>) apiResponse.getData());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Komplek", result.get(0).getComplexName());
    }

    @Test
    void getAllComplex_Error_Test() {
        when(complexRepository.findAll()).thenThrow(NullPointerException.class);
        ResponseEntity<Object> responseEntity = complexService.getAllComplex(null);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());
        assertEquals(AppConstant.ResponseCode.UNKNOWN_ERROR.getCode(), apiResponse.getStatus().getCode());
    }

    @Test
    void addNewComplex_Success_Test() {
        City city = City.builder()
                .id(1L)
                .name("New City")
                .build();

        CityRequest cityRequest = CityRequest.builder()
                .id(1L)
                .cityName("New City")
                .build();

        ComplexRequest complexRequest = ComplexRequest.builder()
                .complexName("Komplek")
                .city(cityRequest)
                .build();

        Complex complex = Complex.builder()
                .name("Komplek")
                .build();



        when(cityRepository.findById(anyLong())).thenReturn(Optional.of(city));
        when(modelMapper.map(any(), eq(Complex.class))).thenReturn(complex);
        when(complexRepository.save(any())).thenReturn(complex);
        when(modelMapper.map(any(), eq(ComplexRequest.class))).thenReturn(complexRequest);

        ResponseEntity<Object> responseEntity = complexService.addNewComplex(complexRequest, 1L);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("New City", ((ComplexRequest) apiResponse.getData()).getCity().getCityName());
        assertEquals("Komplek", ((ComplexRequest) apiResponse.getData()).getComplexName());
    }

    @Test
    void addNewComplex_CityEmpty_Test() {
        ComplexRequest complexRequest = ComplexRequest.builder()
                .complexName("Komplek")
                .build();

        when(cityRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = complexService.addNewComplex(complexRequest,1L);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(AppConstant.ResponseCode.DATA_NOT_FOUND.getCode(), apiResponse.getStatus().getCode());
    }

    @Test
    void addNew_Error_Test() {
        ComplexRequest complexRequest = ComplexRequest.builder()
                .complexName("Komplek")
                .build();

        when(cityRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = complexService.addNewComplex(complexRequest, 1L);

        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());
        assertEquals(AppConstant.ResponseCode.UNKNOWN_ERROR.getCode(), apiResponse.getStatus().getCode());
    }
}