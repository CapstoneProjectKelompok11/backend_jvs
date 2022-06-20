package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Floor;
import com.capstone.booking.domain.dao.Reservation;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.ReservationRequest;
import com.capstone.booking.repository.FloorRepository;
import com.capstone.booking.repository.ReservationRepository;
import com.capstone.booking.repository.UserRepository;
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
public class ReservationService {
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
                    .findAllReservationForUser(
                            userOptional.get().getId(),
                            AppConstant.ReservationStatus.WAITING,
                            AppConstant.ReservationStatus.RESERVED
                            );

            List<ReservationRequest> requests = new ArrayList<>();
            for (Reservation reservation :
                    reservations) {
                requests.add(modelMapper.map(reservation, ReservationRequest.class));
            }
            log.info("Successfully retrieved all reservation with status reserved and waiting payment made by user {}", email);
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, requests, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while trying to get user reservation. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> userAddReservation(ReservationRequest request, Long floorId, String email) {
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
            reservationRepository.save(reservation);

            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(reservation, ReservationRequest.class),
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while trying to add user reservation. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
