package com.capstone.booking.controller;

import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.domain.dto.SearchRequest;
import com.capstone.booking.service.BuildingService;
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

    @GetMapping("/buildings")
    public ResponseEntity<Object> getAllBuilding(@RequestParam (value = "complexId", required = false) Long complexId,
                                                 @RequestParam (required = false, value = "page") Integer page,
                                                 @RequestParam (required = false, value = "limit") Integer limit) {
        if(page == null || limit == null){
            return buildingService.getAllBuilding(complexId);
        } else {
            return buildingService.getBuilding(complexId, page, limit);
        }
    }

    @GetMapping("/building")
    public ResponseEntity<Object> getOneBuilding(@RequestParam (value = "id") Long id) {
        return buildingService.getBuildingById(id);
    }

    @PutMapping("/admin/building")
    public ResponseEntity<Object> updateBuilding(@RequestParam ("buildingId") Long buildingId,
                                                 @RequestBody BuildingRequest buildingRequest) {
        return buildingService.updateBuilding(buildingId, buildingRequest);
    }

    @DeleteMapping("/admin/building")
    public ResponseEntity<Object> deleteBuilding(@RequestParam ("buildingId") Long buildingId) {
        return buildingService.deleteBuilding(buildingId);
    }

    @PostMapping("/building")
    public ResponseEntity<Object> get(@RequestBody (required = false) SearchRequest request) {
        return buildingService.getBuildings(request);
    }

    @PostMapping(value = "/admin/building")
    public ResponseEntity<Object> addNewBuilding(@RequestBody BuildingRequest request,
                                                 @RequestParam ("complexId") Long complexId) {
        return buildingService.addNewBuilding(request, complexId);
    }

    @PostMapping(value = "/admin/building/image", consumes = "multipart/form-data")
    public ResponseEntity<Object> addNewImage(@RequestParam ("buildingId") Long buildingId,
                                              @RequestPart("image") MultipartFile file) throws IOException{
        return buildingService.addImage(buildingId, file);
    }

    @GetMapping(value = "/building/image/{filename}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<Object> getImage(@PathVariable String filename) {
        return buildingService.getImage(filename);
    }


}
