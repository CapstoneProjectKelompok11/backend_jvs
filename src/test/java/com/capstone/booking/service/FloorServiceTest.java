package com.capstone.booking.service;

import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.dao.Building;
import com.capstone.booking.domain.dao.Floor;
import com.capstone.booking.domain.dto.FloorRequest;
import com.capstone.booking.repository.BuildingRepository;
import com.capstone.booking.repository.FloorRepository;
import com.capstone.booking.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = FloorService.class)
class FloorServiceTest {

    @MockBean
    private FloorRepository floorRepository;

    @MockBean
    private BuildingRepository buildingRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private FloorService floorService;

    @Test
    void getByBuilding_Success_Test() {
        List<Floor> floors = new ArrayList<>();

        Floor floor = Floor.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .size("800x800 m2")
                .build();

        floors.add(floor);

        FloorRequest floorRequest = FloorRequest.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .floorSize("800x800 m2")
                .build();

        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(floorRepository.findAllByBuilding_Id(anyLong())).thenReturn(floors);
        when(modelMapper.map(any(), eq(FloorRequest.class))).thenReturn(floorRequest);

        ResponseEntity<Object> responseEntity = floorService.getByBuilding(1L);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        List<FloorRequest> result = ((List<FloorRequest>) apiResponse.getData());

        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Lantai 1", result.get(0).getName());
        assertEquals(10 , result.get(0).getMaxCapacity());
        assertEquals(400000, result.get(0).getStartingPrice());
        assertEquals("800x800 m2", result.get(0).getFloorSize());

    }

    @Test
    void getByBuilding_BuildingEmpty_Test() {
        when(buildingRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = floorService.getByBuilding(1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }

    @Test
    void getByBuilding_Error_Test() {
        when(buildingRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = floorService.getByBuilding(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void addFloor_Success_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        Floor floor = Floor.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .size("800x800 m2")
                .build();

        FloorRequest floorRequest = FloorRequest.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .floorSize("800x800 m2")
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(modelMapper.map(any(), eq(Floor.class))).thenReturn(floor);
        when(floorRepository.save(any())).thenReturn(floor);
        when(modelMapper.map(any(), eq(FloorRequest.class))).thenReturn(floorRequest);

        ResponseEntity<Object> responseEntity = floorService.addFloor(floorRequest, 1L);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Lantai 1", ((FloorRequest) apiResponse.getData()).getName());
        assertEquals(10 , ((FloorRequest) apiResponse.getData()).getMaxCapacity());
        assertEquals(400000, ((FloorRequest) apiResponse.getData()).getStartingPrice());
        assertEquals("800x800 m2", ((FloorRequest) apiResponse.getData()).getFloorSize());
    }
    @Test
    void addFloor_BuildingEmpty_Test() {

        FloorRequest floorRequest = FloorRequest.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .floorSize("800x800 m2")
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = floorService.addFloor(floorRequest, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void addFloor_Error_Test() {
        FloorRequest floorRequest = FloorRequest.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .floorSize("800x800 m2")
                .build();

        when(buildingRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = floorService.addFloor(floorRequest, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void addImageToFloor_Success_Test() throws IOException {
        Floor floor = Floor.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .size("800x800 m2")
                .build();

        FloorRequest floorRequest = FloorRequest.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .floorSize("800x800 m2")
                .image("some_file_name")
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(floorRepository.findById(anyLong())).thenReturn(Optional.of(floor));

        try (MockedStatic<FileUtil> utilities = Mockito.mockStatic(FileUtil.class)) {
            utilities.when(() -> FileUtil.upload(anyString(), any(MultipartFile.class))).thenReturn("some_file_name");

            when(floorRepository.save(any())).thenReturn(floor);
            when(modelMapper.map(any(), eq(FloorRequest.class))).thenReturn(floorRequest);

            ResponseEntity<Object> responseEntity = floorService.addImageToFloor(1L, file);

            ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

            assert apiResponse != null;
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals("some_file_name", ((FloorRequest) apiResponse.getData()).getImage());
        }

    }

    @Test
    void addImageToFloor_Failed_Test() throws IOException {
        Floor floor = Floor.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .size("800x800 m2")
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(floorRepository.findById(anyLong())).thenReturn(Optional.of(floor));

        try (MockedStatic<FileUtil> utilities = Mockito.mockStatic(FileUtil.class)) {
            utilities.when(() -> FileUtil.upload(anyString(), any(MultipartFile.class))).thenReturn(null);

            ResponseEntity<Object> responseEntity = floorService.addImageToFloor(1L, file);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        }

    }

    @Test
    void addImageToFloor_FloorEmpty_Test() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(floorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = floorService.addImageToFloor(1L, file);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void addImageToFloor_Error_Test() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(floorRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = floorService.addImageToFloor(1L, file);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void updateFloor_Success_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        Floor floor = Floor.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .size("800x800 m2")
                .build();

        FloorRequest floorRequest = FloorRequest.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .floorSize("800x800 m2")
                .build();

        when(floorRepository.findById(anyLong())).thenReturn(Optional.of(floor));
        when(floorRepository.save(any())).thenReturn(floor);
        when(modelMapper.map(any(), eq(FloorRequest.class))).thenReturn(floorRequest);

        ResponseEntity<Object> responseEntity = floorService.updateFloor(1L, floorRequest);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Lantai 1", ((FloorRequest) apiResponse.getData()).getName());
        assertEquals(10 , ((FloorRequest) apiResponse.getData()).getMaxCapacity());
        assertEquals(400000, ((FloorRequest) apiResponse.getData()).getStartingPrice());
        assertEquals("800x800 m2", ((FloorRequest) apiResponse.getData()).getFloorSize());
    }

    @Test
    void updateFloor_FloorEmpty_Test() {
        FloorRequest floorRequest = FloorRequest.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .floorSize("800x800 m2")
                .build();

        when(floorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = floorService.updateFloor(1L, floorRequest);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void updateFloor_Error_Test() {

        FloorRequest floorRequest = FloorRequest.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .floorSize("800x800 m2")
                .build();

        when(floorRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = floorService.updateFloor(1L, floorRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void deleteFloor_Success_Test() {
        Floor floor = Floor.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .size("800x800 m2")
                .build();

        when(floorRepository.findById(anyLong())).thenReturn(Optional.of(floor));
        doNothing().when(floorRepository).delete(any());

        ResponseEntity<Object> responseEntity = floorService.deleteFloor(1L);
        verify(floorRepository, times(1)).delete(any());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteFloor_FloorEmpty_Test() {
        when(floorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = floorService.deleteFloor(1L);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void deleteFloor_Error_Test() {
        when(floorRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = floorService.deleteFloor(1L);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

}