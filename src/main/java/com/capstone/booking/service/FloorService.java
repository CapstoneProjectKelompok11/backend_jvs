package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Building;
import com.capstone.booking.domain.dao.Floor;
import com.capstone.booking.domain.dto.FloorRequest;
import com.capstone.booking.repository.BuildingRepository;
import com.capstone.booking.repository.FloorRepository;
import com.capstone.booking.util.FileUtil;
import com.capstone.booking.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FloorService {

    @Value("${img-folder.floor}")
    public String path;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<Object> getByBuilding(Long buildingId) {
        log.info("Executing get All Floor on Building Id : {}", buildingId);
        try {
            Optional<Building> building = buildingRepository.findById(buildingId);
            if (building.isEmpty()) {
                log.info("Building with ID [{}] not found ", buildingId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }
            List<Floor> floorList = floorRepository.findAllByBuilding_Id(buildingId);
            List<FloorRequest> floorRequests = new ArrayList<>();
            for (Floor floor :
                    floorList) {
                floorRequests.add(modelMapper.map(floor, FloorRequest.class));
            }
            log.info("Successfully retrieved floors");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    floorRequests,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to get floors. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateFloor(Long floorId, FloorRequest floorRequest) {
        log.info("Updating Floor with ID [{}]", floorId);
        try {
            Optional<Floor> floorOptional = floorRepository.findById(floorId);
            if(floorOptional.isEmpty()) {
                log.info("Floor with ID : [{}] is not found", floorId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
            }
            Floor floor = floorOptional.get();
            floor.setName(floorRequest.getName());
            floor.setType(floorRequest.getType());
            floor.setMaxCapacity(floorRequest.getMaxCapacity());
            floor.setSize(floorRequest.getFloorSize());
            floor.setStartingPrice(floorRequest.getStartingPrice());
            floorRepository.save(floor);

            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(floor, FloorRequest.class),
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to update flor with ID : [{}]. Error : [{}]", floorId, e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> deleteFloor(Long floorId) {
        log.info("Deleting Floor with ID : [{}]", floorId);
        try {
            Optional<Floor> floorOptional = floorRepository.findById(floorId);
            if(floorOptional.isEmpty()) {
                log.info("Floor with ID : [{}] is not found", floorId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
            }

            floorRepository.delete(floorOptional.get());
            log.info("Successfully deleted floor with ID : [{}]", floorId);
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, null, HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to delete flor with ID : [{}]. Error : [{}]", floorId, e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public ResponseEntity<Object> addFloor(FloorRequest req, Long buildingId) {
        log.info("Executing create new Floor");
        try {
            Optional<Building> optionalBuilding = buildingRepository.findById(buildingId);
            if(optionalBuilding.isEmpty()) {
                log.info("Building with ID [{}] not found ", buildingId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Floor floor = modelMapper.map(req, Floor.class);
            floor.setBuilding(optionalBuilding.get());
            floorRepository.save(floor);

            log.info("Successfully added new Floor");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(floor, FloorRequest.class),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred while trying to add new Floor. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> addImageToFloor(Long floorId, MultipartFile file) throws IOException {
        log.info("Executing add image to floor with ID : {}", floorId);
        try {
            Optional<Floor> floorOptional = floorRepository.findById(floorId);
            if (floorOptional.isEmpty()) {
                log.info("Floor with ID [{}] not found ", floorId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }
            String uploadedFile = FileUtil.upload(path, file);
            if (uploadedFile == null) {
                log.info("Upload failed, check FileUtil log for more information");
                return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                        null,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            Floor floor = floorOptional.get();
            if(floor.getImage()!=null) {
                FileUtil.delete(path, floor.getImage());
            }
            floor.setImage(uploadedFile);
            floorRepository.save(floor);
            log.info("Image Successfully uploaded");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(floor, FloorRequest.class),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred while trying to add image. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getImage(String filename) {
        return FileUtil.getFileContent(path, filename);
    }
}
