package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Floor;
import com.capstone.booking.domain.dao.Reservation;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.domain.dto.ReservationRequest;
import com.capstone.booking.domain.dto.ReservationUserRequest;
import com.capstone.booking.repository.FloorRepository;
import com.capstone.booking.repository.ReservationRepository;
import com.capstone.booking.repository.UserRepository;
import com.capstone.booking.util.FileUtil;
import com.capstone.booking.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ReservationService {

    @Value("${img-folder.reservation}")
    public String path;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FloorRepository floorRepository;

    public ResponseEntity<Object> userGetAllReservation(String email) {
        log.info("Getting user reservation for user : {}", email);
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if(userOptional.isEmpty()) {
                log.info("User with Email [{}] not found ", email);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            List<Reservation> reservations = reservationRepository
                    .findAllByUser_IdAndStatusIsNot(userOptional.get().getId(), AppConstant.ReservationStatus.PENDING);

            List<ReservationRequest> requests = new ArrayList<>();
            for (Reservation reservation :
                    reservations) {
                ReservationRequest request = modelMapper.map(reservation, ReservationRequest.class);
                request.setBuilding(modelMapper.map(reservation.getFloor().getBuilding(), BuildingRequest.class));
                requests.add(request);

            }
            log.info("Successfully retrieved all reservation with status reserved and waiting payment made by user {}", email);
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, requests, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while trying to get user reservation. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> adminGetAllPendingReservation() {
        try {
            log.info("Getting all reservation with pending status");
            List<Reservation> reservations = reservationRepository.findAllByStatusIs(AppConstant.ReservationStatus.PENDING);
            List<ReservationRequest> requests = new ArrayList<>();
            for (Reservation reservation :
                    reservations) {
                ReservationRequest request = modelMapper.map(reservation, ReservationRequest.class);
                request.setBuilding(modelMapper.map(reservation.getFloor().getBuilding(), BuildingRequest.class));
                requests.add(request);
            }
            log.info("Successfully retrieved all reservation with pending status");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, requests, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while trying to get pending reservation. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> adminGetAllReservation() {
        log.info("Getting all reservation except pending status");
        try {
            List<Reservation> reservations = reservationRepository.findAllByStatusIsNot(AppConstant.ReservationStatus.PENDING);
            List<ReservationRequest> requests = new ArrayList<>();
            for (Reservation reservation :
                    reservations) {
                ReservationRequest request = modelMapper.map(reservation, ReservationRequest.class);
                request.setBuilding(modelMapper.map(reservation.getFloor().getBuilding(), BuildingRequest.class));
                requests.add(request);
            }
            log.info("Successfully retrieved all reservation except pending ");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, requests, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while trying to get all reservation. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateReservationStatus(Long reservationId, AppConstant.ReservationStatus status) {
        log.info("Updating reservation ID [{}] status to {}", reservationId, status.toString());
        try {
            Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
            if(reservationOptional.isEmpty()){
                log.info("Reservation with ID [{}] not found", reservationId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
            }
            Reservation reservation = reservationOptional.get();
            reservation.setStatus(status);
            reservationRepository.save(reservation);

            ReservationRequest request = modelMapper.map(reservation, ReservationRequest.class);
            request.setBuilding(modelMapper.map(reservation.getFloor().getBuilding(), BuildingRequest.class));

            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    request,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while trying to get update reservation with ID : [{}]. Error : {}",
                    reservationId, e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> adminAddReservation(Long reservationId, ReservationRequest request, Long floorId) {
        log.info("Add Order from Pending to Waiting for Payment");
        try {
            Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
            if(reservationOptional.isEmpty()){
                log.info("Reservation with ID [{}] not found", reservationId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
            }
            Optional<Floor> optionalFloor = floorRepository.findById(floorId);
            if(optionalFloor.isEmpty()) {
                log.info("Floor with ID [{}] not found ", floorId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Reservation reservation = reservationOptional.get();
            reservation.setStartReservation(request.getStartReservation());
            reservation.setPrice(request.getPrice());
            reservation.setCompany(request.getCompany());
            reservation.setStatus(AppConstant.ReservationStatus.WAITING);
            reservation.setParticipant(request.getParticipant());
            reservation.setFloor(optionalFloor.get());
            reservationRepository.save(reservation);

            ReservationRequest reservationRequest = modelMapper.map(reservation, ReservationRequest.class);
            reservationRequest.setBuilding(modelMapper.map(reservation.getFloor().getBuilding(), BuildingRequest.class));
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, reservationRequest, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while admin is trying to get add reservation with ID : [{}]. Error : {}",
                    reservationId, e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> userAddReservation(ReservationUserRequest request, Long floorId, String email) {
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if(userOptional.isEmpty()) {
                log.info("User with Email [{}] not found ", email);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Optional<Floor> optionalFloor = floorRepository.findById(floorId);
            if(optionalFloor.isEmpty()) {
                log.info("Floor with ID [{}] not found ", floorId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Reservation reservation = modelMapper.map(request, Reservation.class);
            reservation.setUser(userOptional.get());
            reservation.setFloor(optionalFloor.get());
            reservation.setStatus(AppConstant.ReservationStatus.PENDING);
            reservation.setPrice((long) optionalFloor.get().getStartingPrice());
            reservation.setImage(null);
            reservationRepository.save(reservation);

            ReservationRequest reservationRequest = modelMapper.map(reservation, ReservationRequest.class);
            reservationRequest.setBuilding(modelMapper.map(reservation.getFloor().getBuilding(), BuildingRequest.class));


            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    reservationRequest,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while trying to add user reservation. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> userCancelReservation(Long reservationId, String email) {
        log.info("Canceling reservation with ID : [{}]", reservationId);
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if(userOptional.isEmpty()) {
                log.info("User with Email [{}] not found ", email);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
            if(reservationOptional.isEmpty()) {
                log.info("Reservation with ID [{}] not found", reservationId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
            }

            if(!reservationOptional.get().getUser().equals(userOptional.get())) {
                log.info("User with email [{}] does not have access to reservation made by User [{}]",
                        email, userOptional.get().getEmail());
                return ResponseUtil.build(AppConstant.ResponseCode.UNAUTHORIZED_ACCESS,
                        null,
                        HttpStatus.FORBIDDEN);
            }
            if(!reservationOptional.get().getStatus().equals(AppConstant.ReservationStatus.WAITING) ) {
                log.info("Reservation can only be cancelled before sending payment");
                return ResponseUtil.build(AppConstant.ResponseCode.UNAUTHORIZED_ACCESS,
                        null,
                        HttpStatus.FORBIDDEN);
            }
            Reservation reservation = reservationOptional.get();
            reservation.setStatus(AppConstant.ReservationStatus.CANCELED);
            reservationRepository.save(reservation);
            log.info("Successfully canceled the reservation");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(reservation, ReservationRequest.class),
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while trying to cancel reservation. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> addPayment(Long reservationId, MultipartFile file, String email) throws IOException {
        log.info("Executing add payment image to reservation with ID : {}", reservationId);
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if(userOptional.isEmpty()) {
                log.info("User with Email [{}] not found ", email);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);
            if (reservationOptional.isEmpty()) {
                log.info("Reservation with ID [{}] not found ", reservationId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }
            if(!reservationOptional.get().getUser().equals(userOptional.get())) {
                log.info("User with email [{}] does not have access to reservation made by User [{}]",
                        email, userOptional.get().getEmail());
                return ResponseUtil.build(AppConstant.ResponseCode.UNAUTHORIZED_ACCESS,
                        null,
                        HttpStatus.FORBIDDEN);
            }

            if(reservationOptional.get().getStatus().equals(AppConstant.ReservationStatus.PENDING) ) {
                log.info("Reservation has not been approved yet by admin");
                return ResponseUtil.build(AppConstant.ResponseCode.UNAUTHORIZED_ACCESS,
                        null,
                        HttpStatus.FORBIDDEN);
            }

            String uploadedFile = FileUtil.upload(path, file);
            if (uploadedFile == null) {
                log.info("Upload failed, check FileUtil log for more information");
                return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                        null,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Reservation reservation = reservationOptional.get();
            if(reservation.getImage()!=null) {
                FileUtil.delete(path, reservation.getImage());
            }
            reservation.setImage(uploadedFile);
            reservationRepository.save(reservation);
            log.info("Image Successfully uploaded");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(reservation, ReservationRequest.class),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred while trying to add image. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    public ResponseEntity<Object> getImage(String filename) {
        return FileUtil.getFileContent(path, filename);
    }

}
