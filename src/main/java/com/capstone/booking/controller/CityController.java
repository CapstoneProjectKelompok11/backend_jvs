package com.capstone.booking.controller;

import com.capstone.booking.domain.dto.CityRequest;
import com.capstone.booking.service.CityService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping()
public class CityController {
    @Autowired
    private CityService cityService;

    @GetMapping("/city")
    @SecurityRequirements
    public ResponseEntity<Object> getAllCity() {
        return cityService.getAllCity();
    }

    @PostMapping("/admin/city")
    public ResponseEntity<Object> addNewCity(@RequestBody CityRequest request) {
        return cityService.addNewCity(request);
    }
}
