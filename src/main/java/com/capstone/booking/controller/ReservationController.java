package com.capstone.booking.controller;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dto.ReservationRequest;
import com.capstone.booking.service.ReservationService;
import com.capstone.booking.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @PostMapping(value = "/auth/reservation/image", consumes = "multipart/form-data")
    public ResponseEntity<Object> addImage(@RequestParam ("reservationid") Long reservationId,
                                           @RequestPart ("image") MultipartFile file,
                                           Principal principal) throws IOException {
        if (principal != null){
            return reservationService.addPayment(reservationId, file, principal.getName());
        } else {
            return ResponseUtil.build(AppConstant.ResponseCode.NOT_LOGGED_IN, null, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/admin/floor/image/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Object> getImage(@PathVariable String filename) {
        return reservationService.getImage(filename);
    }
}
