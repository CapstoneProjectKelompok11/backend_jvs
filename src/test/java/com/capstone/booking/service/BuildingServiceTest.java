package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.dao.Building;
import com.capstone.booking.domain.dao.BuildingImage;
import com.capstone.booking.domain.dao.Complex;
import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.domain.dto.ComplexRequest;
import com.capstone.booking.repository.*;
import com.capstone.booking.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BuildingService.class)
class BuildingServiceTest {

    @MockBean
    private BuildingImageRepository buildingImageRepository;

    @MockBean
    private BuildingRepository buildingRepository;

    @MockBean
    private FloorRepository floorRepository;

    @MockBean
    private ComplexRepository complexRepository;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private BuildingService buildingService;

    @Test
    void getBuilding_Success_Test() {
        List<Building> buildingList = new ArrayList<>();
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        buildingList.add(building);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Building> buildingPage = new PageImpl<Building>(buildingList.subList(0,1), pageable, buildingList.size());

        Set<String> type = Set.of("Tipe 1", "Tipe 2");

        Double rating = 4.8;

        BuildingRequest request = BuildingRequest.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        when(buildingRepository.findAllByComplex_Id(anyLong(), any())).thenReturn(buildingPage);
        when(floorRepository.findDistinctTypeByBuilding_Id(anyLong())).thenReturn(type);
        when(reviewRepository.averageOfBuildingReviewRating(anyLong())).thenReturn(rating);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(request);

        ResponseEntity responseEntity = buildingService.getBuilding(1L, 0,1);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        List<BuildingRequest> result = ((List<BuildingRequest>) apiResponse.getData());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Gedung A", result.get(0).getName());
        assertEquals("Jalan A", result.get(0).getAddress());
        assertEquals(100, result.get(0).getCapacity());
        assertEquals(rating, result.get(0).getRating());
        assertEquals(type, result.get(0).getOfficeType());
    }

    @Test
    void getBuilding_NoComplex_Test() {
        List<Building> buildingList = new ArrayList<>();
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        buildingList.add(building);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Building> buildingPage = new PageImpl<Building>(buildingList.subList(0,1), pageable, buildingList.size());

        Set<String> type = Set.of("Tipe 1", "Tipe 2");

        Double rating = 4.8;

        BuildingRequest request = BuildingRequest.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        when(buildingRepository.findAll(any(Pageable.class))).thenReturn(buildingPage);
        when(floorRepository.findDistinctTypeByBuilding_Id(anyLong())).thenReturn(type);
        when(reviewRepository.averageOfBuildingReviewRating(anyLong())).thenReturn(rating);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(request);

        ResponseEntity responseEntity = buildingService.getBuilding(null, 0,1);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        List<BuildingRequest> result = ((List<BuildingRequest>) apiResponse.getData());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Gedung A", result.get(0).getName());
        assertEquals("Jalan A", result.get(0).getAddress());
        assertEquals(100, result.get(0).getCapacity());
        assertEquals(rating, result.get(0).getRating());
        assertEquals(type, result.get(0).getOfficeType());
    }

    @Test
    void getBuilding_Error_Test() {
        when(buildingRepository.findAll()).thenThrow(NullPointerException.class);
        ResponseEntity<Object> responseEntity = buildingService.getBuilding(null, 0, 1);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());
        assertEquals(AppConstant.ResponseCode.UNKNOWN_ERROR.getCode(), apiResponse.getStatus().getCode());
    }

    @Test
    void addNewBuilding_Success_Test() {
        Complex complex = Complex.builder()
                .name("Komplek A")
                .build();
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        ComplexRequest complexRequest = ComplexRequest.builder()
                .complexName("Komplek A")
                .build();
        BuildingRequest request = BuildingRequest.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .complex(complexRequest)
                .build();


        when(complexRepository.findById(anyLong())).thenReturn(Optional.of(complex));
        when(modelMapper.map(any(), eq(Building.class))).thenReturn(building);
        when(buildingRepository.save(any())).thenReturn(building);
        when(modelMapper.map(any(), eq(ComplexRequest.class))).thenReturn(complexRequest);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(request);


        ResponseEntity responseEntity = buildingService.addNewBuilding(request, 1L);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Gedung A", ((BuildingRequest) apiResponse.getData()).getName());
        assertEquals("Jalan A", ((BuildingRequest) apiResponse.getData()).getAddress());
        assertEquals(100, ((BuildingRequest) apiResponse.getData()).getCapacity());
        assertEquals("Komplek A", ((BuildingRequest) apiResponse.getData()).getComplex().getComplexName());

    }

    @Test
    void addNewBuilding_ComplexEmpty_Test() {
        BuildingRequest request = BuildingRequest.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();


        when(complexRepository.findById(anyLong())).thenReturn(Optional.empty());


        ResponseEntity responseEntity = buildingService.addNewBuilding(request, 1L);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }

    @Test
    void addNewBuilding_Error_Test() {
        BuildingRequest request = BuildingRequest.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        when(complexRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = buildingService.addNewBuilding(request, 1L);

        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());
        assertEquals(AppConstant.ResponseCode.UNKNOWN_ERROR.getCode(), apiResponse.getStatus().getCode());
    }

    @Test
    void addImage_Success_Test() throws IOException {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        BuildingImage buildingImage = BuildingImage.builder()
                .fileName("filename")
                .building(building)
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));

        try (MockedStatic<FileUtil> utilities = Mockito.mockStatic(FileUtil.class)) {
            utilities.when(() -> FileUtil.upload(anyString(), any(MultipartFile.class))).thenReturn("some_file_name");
            when(buildingImageRepository.save(any())).thenReturn(buildingImage);

            ResponseEntity<Object> responseEntity = buildingService.addImage(1L, file);

            ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

            assert apiResponse != null;
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals("some_file_name",apiResponse.getData());
        }

    }

    @Test
    void addImage_Failed_Test() throws IOException {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));

        try (MockedStatic<FileUtil> utilities = Mockito.mockStatic(FileUtil.class)) {
            utilities.when(() -> FileUtil.upload(anyString(), any(MultipartFile.class))).thenReturn(null);

            ResponseEntity<Object> responseEntity = buildingService.addImage(1L, file);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        }

    }
    @Test
    void addImage_BuildingEmpty_Test() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = buildingService.addImage(1L, file);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void addImage_Error_Test() throws IOException {

        BuildingRequest request = BuildingRequest.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(buildingRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = buildingService.addImage(1L, file);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void getImage_Success_Test() {

    }
}