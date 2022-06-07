package com.capstone.booking.controller;

import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.service.BuildingService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@CrossOrigin
@RequestMapping()
public class BuildingController {
    @Autowired
    private BuildingService buildingService;

    @GetMapping("/building")
    public ResponseEntity<Object> getAllBuilding() {
        return buildingService.getAllBuilding();
    }

    @PostMapping(value = "/admin/building")
    public ResponseEntity<Object> addNewBuilding(@RequestBody BuildingRequest request,
                                                 @RequestParam ("ComplexID") Long complexId) {
        return buildingService.addNewBuilding(request, complexId);
    }

    @PostMapping(value = "/admin/building/image", consumes = "multipart/form-data")
    public ResponseEntity<Object> addNewImage(@RequestParam ("BuildingID") Long buildingId,
                                              @RequestPart("image") MultipartFile file) throws IOException{
        return buildingService.addImage(buildingId, file);
    }

    @GetMapping("/building/image/{filename}")
    public ResponseEntity<Object> getImage(@PathVariable String filename) {
        log.info(filename);
        return buildingService.getImage(filename);
    }
}
