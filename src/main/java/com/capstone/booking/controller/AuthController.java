package com.capstone.booking.controller;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dto.RegisterRequest;
import com.capstone.booking.domain.dto.RegisterResponse;
import com.capstone.booking.domain.payload.EmailPassword;
import com.capstone.booking.service.AuthService;
import com.capstone.booking.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @SecurityRequirements
    public ResponseEntity<Object> register(@RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    @SecurityRequirements
    public ResponseEntity<Object> login(@RequestBody EmailPassword req) {
        return authService.generateToken(req);
    }

    @GetMapping("/auth/profile")
    public ResponseEntity<Object> profile(Principal principal) {
        if (principal != null){
            return authService.getProfile(principal.getName());
        } else {
            return ResponseUtil.build(AppConstant.ResponseCode.NOT_LOGGED_IN, null, HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/auth/profile/image", consumes = "multipart/form-data")
    public ResponseEntity<Object> uploadProfilePicture (@RequestPart ("image") MultipartFile file,
                                                        Principal principal) throws IOException {
        if (principal != null){
            return authService.addProfilePicture(file, principal.getName());
        } else {
            return ResponseUtil.build(AppConstant.ResponseCode.NOT_LOGGED_IN, null, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(value = "/profile/image/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Object> showProfilePicture (@PathVariable String filename) {
        return authService.getImage(filename);
    }

    @PutMapping(value = "/auth/profile/edit")
    public ResponseEntity<Object> editProfile(Principal principal, @RequestBody RegisterResponse response) {
        if (principal != null){
            return authService.editProfile(response, principal.getName());
        } else {
            return ResponseUtil.build(AppConstant.ResponseCode.NOT_LOGGED_IN, null, HttpStatus.FORBIDDEN);
        }
    }
}
