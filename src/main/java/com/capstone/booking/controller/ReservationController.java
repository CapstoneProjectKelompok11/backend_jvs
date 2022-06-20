package com.capstone.booking.controller;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dto.ReservationRequest;
import com.capstone.booking.service.ReservationService;
import com.capstone.booking.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping()
public class ReservationController {
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/auth/reservation")
    public ResponseEntity<Object> getReservation (Principal principal) {
        if(principal!=null) {
            return reservationService.userGetAllReservation(principal.getName());
        } else {
            return ResponseUtil.build(AppConstant.ResponseCode.NOT_LOGGED_IN, null, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/auth/reservation")
    public ResponseEntity<Object> addReservation (@RequestBody ReservationRequest request,
                                                  @RequestParam ("floorId") Long floorId,
                                                  Principal principal) {
        if (principal != null){
            return reservationService.userAddReservation(request, floorId, principal.getName());
        } else {
            return ResponseUtil.build(AppConstant.ResponseCode.NOT_LOGGED_IN, null, HttpStatus.FORBIDDEN);
        }
    }
}
