package com.capstone.booking.controller;

import com.capstone.booking.domain.dto.FloorRequest;
import com.capstone.booking.service.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping()
public class FloorController {

    @Autowired
    private FloorService floorService;

    @GetMapping("/floor")
    public ResponseEntity<Object> getFloor(@RequestParam ("buildingId") Long buildingId) {
        return floorService.getByBuilding(buildingId);
    }

    @PostMapping("/admin/floor")
    public ResponseEntity<Object> addFloor(@RequestBody FloorRequest request,
                                           @RequestParam ("buildingId") Long buildingId) {
        return floorService.addFloor(request, buildingId);
    }

    @PutMapping(value = "/admin/floor")
    public ResponseEntity<Object> updateFloor(@RequestParam ("floorId") Long floorId,
                                              @RequestBody FloorRequest request) {
        return floorService.updateFloor(floorId, request);
    }

    @DeleteMapping(value = "/admin/floor")
    public ResponseEntity<Object> deleteFloor(@RequestParam ("floorId") Long floorId) {
        return floorService.deleteFloor(floorId);
    }

    @PostMapping(value = "/admin/floor/image", consumes = "multipart/form-data")
    public ResponseEntity<Object> addImage(@RequestParam ("floorId") Long floorId,
                                           @RequestPart ("image") MultipartFile file) throws IOException {
        return floorService.addImageToFloor(floorId, file);
    }




    @GetMapping(value = "/floor/image/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Object> getImage(@PathVariable String filename) throws IOException {
        return floorService.getImage(filename);
    }

}
