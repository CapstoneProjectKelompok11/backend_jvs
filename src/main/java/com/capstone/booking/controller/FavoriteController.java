package com.capstone.booking.controller;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.service.FavoriteService;
import com.capstone.booking.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping()
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/auth/building/favorite")
    public ResponseEntity<Object> getFavoriteBuilding(Principal principal) {
        if(principal != null) {
            return favoriteService.getFavorite(principal.getName());
        } else {
            return ResponseUtil.build(AppConstant.ResponseCode.NOT_LOGGED_IN, null, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/auth/building/favorite")
    public ResponseEntity<Object> addToFavorite(Principal principal, @RequestParam ("buildingId") Long buildingId) {
        if(principal != null) {
            return favoriteService.addToFavorites(principal.getName(), buildingId);
        } else {
            return ResponseUtil.build(AppConstant.ResponseCode.NOT_LOGGED_IN, null, HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/auth/building/unfavorite")
    public ResponseEntity<Object> deleteFromFavorite(Principal principal, @RequestParam ("buildingId") Long buildingId) {
        if(principal != null) {
            return favoriteService.unFavorite(principal.getName(), buildingId);
        } else {
            return ResponseUtil.build(AppConstant.ResponseCode.NOT_LOGGED_IN, null, HttpStatus.FORBIDDEN);
        }
    }

}
