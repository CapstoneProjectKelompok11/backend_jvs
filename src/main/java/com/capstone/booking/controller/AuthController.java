package com.capstone.booking.controller;

import com.capstone.booking.domain.dto.user.RegisterRequest;
import com.capstone.booking.domain.payload.EmailPassword;
import com.capstone.booking.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> generateToken(@RequestBody EmailPassword req) {
        return authService.generateToken(req);
    }

    @GetMapping("/admin/smth")
    public ResponseEntity<Object> adminTest() {
        return ResponseEntity.ok("test successfull");
    }

}
