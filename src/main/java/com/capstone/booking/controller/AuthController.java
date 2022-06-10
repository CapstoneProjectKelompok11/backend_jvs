package com.capstone.booking.controller;

import com.capstone.booking.domain.dto.RegisterRequest;
import com.capstone.booking.domain.payload.EmailPassword;
import com.capstone.booking.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
