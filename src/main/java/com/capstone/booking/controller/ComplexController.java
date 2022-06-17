package com.capstone.booking.controller;

import com.capstone.booking.domain.dto.ComplexRequest;
import com.capstone.booking.service.ComplexService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping()
public class ComplexController {

    @Autowired
    private ComplexService complexService;

    @GetMapping("/complex")
    @SecurityRequirements
    public ResponseEntity<Object> getAllComplex(@RequestParam (value = "city", required = false) String city) {
        return complexService.getAllComplex(city);
    }

    @PostMapping("/admin/complex")
    public ResponseEntity<Object> addNewComplex(@RequestBody ComplexRequest request,
                                                @RequestParam (value = "cityId") Long cityId) {
        return complexService.addNewComplex(request, cityId);
    }
}
