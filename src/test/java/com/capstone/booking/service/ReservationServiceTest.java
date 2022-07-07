package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.dao.Building;
import com.capstone.booking.domain.dao.Floor;
import com.capstone.booking.domain.dao.Reservation;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.domain.dto.FloorRequest;
import com.capstone.booking.domain.dto.ReservationRequest;
import com.capstone.booking.domain.dto.ReservationUserRequest;
import com.capstone.booking.repository.FloorRepository;
import com.capstone.booking.repository.ReservationRepository;
import com.capstone.booking.repository.UserRepository;
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

import javax.swing.text.html.Option;
import javax.validation.constraints.Null;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReservationService.class)
class ReservationServiceTest {
    @MockBean
    private ReservationRepository reservationRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private FloorRepository floorRepository;

    @Autowired
    private ReservationService reservationService;

    @Test
    void userGetAllReservation_Success_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@email.com")
                .build();
        List<Reservation> reservations = new ArrayList<>();
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.WAITING)
                .floor(floor)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.WAITING)
                .build();
        BuildingRequest buildingRequest =  BuildingRequest.builder()
                .id(1L)
                .build();
        reservations.add(reservation);
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(reservationRepository.findAllByUser_IdAndStatusIsNot(anyLong(),
                any(AppConstant.ReservationStatus.class))).thenReturn(reservations);
        when(modelMapper.map(any(), eq(ReservationRequest.class))).thenReturn(request);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(buildingRequest);

        ResponseEntity<Object> responseEntity = reservationService.userGetAllReservation("email");
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(AppConstant.ReservationStatus.WAITING, ((List<ReservationRequest>) apiResponse.getData()).get(0).getStatus());

    }

    @Test
    void userGetAllReservation_UserEmpty_Test() {

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reservationService.userGetAllReservation("email");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }

    @Test
    void userGetAllReservation_Error_Test() {

        when(userRepository.findUserByEmail(anyString())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reservationService.userGetAllReservation("email");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());

    }

    @Test
    void userAddReservation_Success_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@email.com")
                .build();
        Floor floor = Floor.builder()
                .id(1L)
                .name("Lantai 1")
                .maxCapacity(10)
                .startingPrice(400000)
                .size("800x800 m2")
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.WAITING)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.WAITING)
                .build();
        ReservationUserRequest userRequest = ReservationUserRequest.builder()
                .build();

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(floorRepository.findById(anyLong())).thenReturn(Optional.of(floor));
        when(modelMapper.map(any(), eq(Reservation.class))).thenReturn(reservation);
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(modelMapper.map(any(), eq(ReservationRequest.class))).thenReturn(request);

        ResponseEntity<Object> responseEntity = reservationService.userAddReservation(userRequest, 1L, "email");
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(AppConstant.ReservationStatus.WAITING, ((ReservationRequest) apiResponse.getData()).getStatus());
    }

    @Test
    void userAddReservation_UserEmpty_Test() {

        ReservationUserRequest userRequest = ReservationUserRequest.builder()
                .build();
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reservationService.userAddReservation(userRequest, 1L, "email");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void userAddReservation_FloorEmpty_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@email.com")
                .build();
        ReservationUserRequest userRequest = ReservationUserRequest.builder()
                .build();

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(floorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reservationService.userAddReservation(userRequest, 1L, "email");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void userAddReservation_Error_Test() {

        ReservationUserRequest userRequest = ReservationUserRequest.builder()
                .build();

        when(userRepository.findUserByEmail(anyString())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reservationService.userAddReservation(userRequest, 1L, "email");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void addPayment_Success_Test() throws IOException {
        User user = User.builder()
                .id(1L)
                .email("some_email@email")
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .status(AppConstant.ReservationStatus.WAITING)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.WAITING)
                .image("some_file_name")
                .build();
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        try (MockedStatic<FileUtil> utilities = Mockito.mockStatic(FileUtil.class)) {
            utilities.when(() -> FileUtil.upload(anyString(), any(MultipartFile.class))).thenReturn("some_file_name");

            when(reservationRepository.save(any())).thenReturn(reservation);
            when(modelMapper.map(any(), eq(ReservationRequest.class))).thenReturn(request);

            ResponseEntity<Object> responseEntity = reservationService.addPayment(1L, file, "email");

            ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

            assert apiResponse != null;
            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals("some_file_name", ((ReservationRequest) apiResponse.getData()).getImage());
        }
    }

    @Test
    void addPayment_Failed_Test() throws IOException {
        User user = User.builder()
                .id(1L)
                .email("some_email@email")
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user)
                .status(AppConstant.ReservationStatus.WAITING)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.WAITING)
                .image("some_file_name")
                .build();
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        try (MockedStatic<FileUtil> utilities = Mockito.mockStatic(FileUtil.class)) {
            utilities.when(() -> FileUtil.upload(anyString(), any(MultipartFile.class))).thenReturn(null);

            ResponseEntity<Object> responseEntity = reservationService.addPayment(1L, file, "email");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        }
    }

    @Test
    void addPayment_UserEmpty_Test() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());
        ResponseEntity<Object> responseEntity = reservationService.addPayment(1L, file, "email");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void addPayment_ReservationEmpty_Test() throws IOException {
        User user = User.builder()
                .id(1L)
                .email("some_email@email")
                .build();
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());
        ResponseEntity<Object> responseEntity = reservationService.addPayment(1L, file, "email");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void addPayment_Error_Test() throws IOException {

        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(userRepository.findUserByEmail(anyString())).thenThrow(NullPointerException.class);
        ResponseEntity<Object> responseEntity = reservationService.addPayment(1L, file, "email");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void addPayment_Unauthorized_Test() throws IOException {
        User user = User.builder()
                .id(1L)
                .email("some_email@email")
                .build();

        User user2 = User.builder()
                .id(1L)
                .email("another_email@email")
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .user(user2)
                .status(AppConstant.ReservationStatus.WAITING)
                .build();
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        ResponseEntity<Object> responseEntity = reservationService.addPayment(1L, file, "email");

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    void adminGetAllPendingReservation_Success_Test() {
        List<Reservation> reservations = new ArrayList<>();
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.PENDING)
                .floor(floor)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.PENDING)
                .build();
        BuildingRequest buildingRequest =  BuildingRequest.builder()
                .id(1L)
                .build();
        reservations.add(reservation);

        when(reservationRepository.findAllByStatusIs(any())).thenReturn(reservations);
        when(modelMapper.map(any(), eq(ReservationRequest.class))).thenReturn(request);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(buildingRequest);

        ResponseEntity<Object> responseEntity = reservationService.adminGetAllPendingReservation();
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(AppConstant.ReservationStatus.PENDING, ((List<ReservationRequest>) apiResponse.getData()).get(0).getStatus());
    }

    @Test
    void adminGetAllPendingReservation_Error_Test() {
        when(reservationRepository.findAllByStatusIs(any())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reservationService.adminGetAllPendingReservation();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void adminGetAllReservation_Success_Test() {
        List<Reservation> reservations = new ArrayList<>();
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .floor(floor)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .build();
        BuildingRequest buildingRequest =  BuildingRequest.builder()
                .id(1L)
                .build();
        reservations.add(reservation);

        when(reservationRepository.findAllByStatusIsNot(any())).thenReturn(reservations);
        when(modelMapper.map(any(), eq(ReservationRequest.class))).thenReturn(request);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(buildingRequest);

        ResponseEntity<Object> responseEntity = reservationService.adminGetAllReservation();
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(AppConstant.ReservationStatus.ACTIVE, ((List<ReservationRequest>) apiResponse.getData()).get(0).getStatus());
    }

    @Test
    void adminGetAllReservation_Error_Test() {
        when(reservationRepository.findAllByStatusIsNot(any())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reservationService.adminGetAllReservation();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
    @Test
    void updateReservationStatus_Success_Test() {
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .floor(floor)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .build();
        BuildingRequest buildingRequest =  BuildingRequest.builder()
                .id(1L)
                .build();

        when(reservationRepository.findById(any())).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(modelMapper.map(any(), eq(ReservationRequest.class))).thenReturn(request);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(buildingRequest);

        ResponseEntity<Object> responseEntity = reservationService.updateReservationStatus(
                1L,
                AppConstant.ReservationStatus.ACTIVE);

        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(AppConstant.ReservationStatus.ACTIVE, ((ReservationRequest) apiResponse.getData()).getStatus());
    }

    @Test
    void updateReservationStatus_ReservationEmpty_Test() {

        when(reservationRepository.findById(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reservationService.updateReservationStatus(
                1L,
                AppConstant.ReservationStatus.ACTIVE);


        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void updateReservationStatus_Error_Test() {

        when(reservationRepository.findById(any())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reservationService.updateReservationStatus(
                1L,
                AppConstant.ReservationStatus.ACTIVE);


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void adminAddReservation_Success_Test() {
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .floor(floor)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .build();
        BuildingRequest buildingRequest =  BuildingRequest.builder()
                .id(1L)
                .build();

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        when(floorRepository.findById(anyLong())).thenReturn(Optional.of(floor));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(modelMapper.map(any(), eq(ReservationRequest.class))).thenReturn(request);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(buildingRequest);

        ResponseEntity<Object> responseEntity = reservationService.adminAddReservation(
                1L,
                request,
                1L);

        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(AppConstant.ReservationStatus.ACTIVE, ((ReservationRequest) apiResponse.getData()).getStatus());
    }

    @Test
    void adminAddReservation_ReservationEmpty_Test() {
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .build();

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reservationService.adminAddReservation(
                1L,
                request,
                1L);


        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void adminAddReservation_FloorEmpty_Test() {
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .floor(floor)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .build();

        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));
        when(floorRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reservationService.adminAddReservation(
                1L,
                request,
                1L);


        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    @Test
    void adminAddReservation_Error_Test() {
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .floor(floor)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .build();
        BuildingRequest buildingRequest =  BuildingRequest.builder()
                .id(1L)
                .build();

        when(reservationRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reservationService.adminAddReservation(
                1L,
                request,
                1L);


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void userCancelReservation_Success_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@email.com")
                .build();
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.WAITING)
                .floor(floor)
                .user(user)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.WAITING)
                .build();
        BuildingRequest buildingRequest =  BuildingRequest.builder()
                .id(1L)
                .build();

        when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        when(reservationRepository.save(any())).thenReturn(reservation);
        when(modelMapper.map(any(), eq(ReservationRequest.class))).thenReturn(request);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(buildingRequest);

        ResponseEntity<Object> responseEntity = reservationService.userCancelReservation(1L,"email");

        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(AppConstant.ReservationStatus.WAITING, ((ReservationRequest) apiResponse.getData()).getStatus());
    }

    @Test
    void userCancelReservation_UserEmpty_Test() {

        when(userRepository.findUserByEmail(any())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reservationService.userCancelReservation(1L,"email");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void userCancelReservation_ReservationEmpty_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@email.com")
                .build();

        when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reservationService.userCancelReservation(1L,"email");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void userCancelReservation_DifferentUser_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@email.com")
                .build();
        User user1 = User.builder()
                .id(1L)
                .email("another-email@email.com")
                .build();
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.WAITING)
                .floor(floor)
                .user(user)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.WAITING)
                .build();

        when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(user1));
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));


        ResponseEntity<Object> responseEntity = reservationService.userCancelReservation(1L,"email");

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    void userCancelReservation_WrongStatus_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@email.com")
                .build();
        Building building = Building.builder().build();
        Floor floor = Floor.builder()
                .building(building)
                .build();
        Reservation reservation = Reservation.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .floor(floor)
                .user(user)
                .build();
        ReservationRequest request = ReservationRequest.builder()
                .id(1L)
                .status(AppConstant.ReservationStatus.ACTIVE)
                .build();
        BuildingRequest buildingRequest =  BuildingRequest.builder()
                .id(1L)
                .build();

        when(userRepository.findUserByEmail(any())).thenReturn(Optional.of(user));
        when(reservationRepository.findById(anyLong())).thenReturn(Optional.of(reservation));

        when(reservationRepository.save(any())).thenReturn(reservation);
        when(modelMapper.map(any(), eq(ReservationRequest.class))).thenReturn(request);
        when(modelMapper.map(any(), eq(BuildingRequest.class))).thenReturn(buildingRequest);

        ResponseEntity<Object> responseEntity = reservationService.userCancelReservation(1L,"email");

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }

    @Test
    void userCancelReservation_Error_Test() {

        when(userRepository.findUserByEmail(any())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reservationService.userCancelReservation(1L,"email");


        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}