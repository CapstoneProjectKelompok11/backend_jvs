package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.common.SearchSpecification;
import com.capstone.booking.domain.dao.*;
import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.domain.dto.FavoriteResponse;
import com.capstone.booking.domain.dto.SearchRequest;
import com.capstone.booking.repository.*;
import com.capstone.booking.util.ResponseUtil;
import com.capstone.booking.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class BuildingService {

    @Value("${img-folder.building}")
    public String path;

    @Autowired
    private BuildingImageRepository buildingImageRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private ComplexRepository complexRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<Object> getBuilding(Long complexId, Integer page, Integer limit) {
        log.info("Executing get all Building with Pagination");
        try {
            Pageable pageable = PageRequest.of(page, limit);
            Page<Building> buildingList;
            if(complexId == null) {
                log.info("Complex Id is null. Getting all building with Pagination");
                buildingList = buildingRepository.findAll(pageable);
            } else {
                log.info("Complex Id is not null. Getting all building with complex ID and Pagination : {}", complexId);
                buildingList = buildingRepository.findAllByComplex_Id(complexId, pageable);
            }
            List<BuildingRequest> buildingRequests = new ArrayList<>();
            for (Building building :
                    buildingList) {
                Set<String> types = floorRepository.findDistinctTypeByBuildingId(building.getId());
                Double rating = reviewRepository.averageOfBuildingReviewRating(building.getId());
                BuildingRequest request = modelMapper.map(building, BuildingRequest.class);
                int floorCount = floorRepository.countByBuilding_Id(building.getId());
                request.setOfficeType(types);
                request.setRating(Objects.requireNonNullElse(rating, 0.0));
                request.setFloorCount(floorCount);
                buildingRequests.add(request);
            }

            log.info("Successfully retrieved all Building with pagination");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    buildingRequests,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to get all building. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<Object> getAllBuilding(Long complexId) {
        log.info("Executing get all Building");
        try {
            List<Building> buildingList;
            if(complexId == null) {
                log.info("Complex Id is null. Getting all building");
                buildingList = buildingRepository.findAll();
            } else {
                log.info("Complex Id is not null. Getting all building with complex ID : {}", complexId);
                buildingList = buildingRepository.findAllByComplex_Id(complexId);
            }
            List<BuildingRequest> buildingRequests = new ArrayList<>();
            for (Building building :
                    buildingList) {
                Set<String> types = floorRepository.findDistinctTypeByBuildingId(building.getId());
                Double rating = reviewRepository.averageOfBuildingReviewRating(building.getId());
                BuildingRequest request = modelMapper.map(building, BuildingRequest.class);

                int floorCount = floorRepository.countByBuilding_Id(building.getId());
                request.setOfficeType(types);
                request.setRating(Objects.requireNonNullElse(rating, 0.0));
                request.setFloorCount(floorCount);
                buildingRequests.add(request);
            }

            log.info("Successfully retrieved all Building");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    buildingRequests,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to get all building. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getBuildingById(Long id) {
        log.info("Executing get Building with ID : {}", id);
        try {
            Optional<Building> building = buildingRepository.findById(id);
            if(building.isEmpty()) {
                log.info("Building with ID [{}] not found ", id);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Set<String> types = floorRepository.findDistinctTypeByBuildingId(id);
            Double rating = reviewRepository.averageOfBuildingReviewRating(id);

            BuildingRequest request = modelMapper.map(building, BuildingRequest.class);
            int floorCount = floorRepository.countByBuilding_Id(id);
            request.setOfficeType(types);
            request.setRating(Objects.requireNonNullElse(rating, 0.0));
            request.setFloorCount(floorCount);

            log.info("Successfully retrieved Building with ID : {}", id);
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    request,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to get building with ID : {}. Error : {}", id, e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public ResponseEntity<Object> getBuildings(SearchRequest request) {
        log.info("Executing get Buildings");
        try {
            SearchSpecification<Building> specification = new SearchSpecification<>(request);
            Pageable pageable = SearchSpecification.getPageable(request.getPage(), request.getSize());
            Page<Building> buildings = buildingRepository.findAll(specification, pageable);
            List<BuildingRequest> buildingRequests = new ArrayList<>();
            for (Building building :
                    buildings) {
                Set<String> types = floorRepository.findDistinctTypeByBuildingId(building.getId());
                Double rating = reviewRepository.averageOfBuildingReviewRating(building.getId());
                BuildingRequest buildingRequest = modelMapper.map(building, BuildingRequest.class);
                int floorCount = floorRepository.countByBuilding_Id(building.getId());

                buildingRequest.setOfficeType(types);
                buildingRequest.setRating(Objects.requireNonNullElse(rating, 0.0));
                buildingRequest.setFloorCount(floorCount);
                buildingRequests.add(buildingRequest);
            }
            log.info("Successfully retrieved all Building");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    buildingRequests,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to get all building. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> searchBuilding(String name) {
        log.info("Executing search building");
        try {
            List<Building> buildings = buildingRepository.findAllByNameContainsIgnoreCase(name);
            List<BuildingRequest> requests = new ArrayList<>();
            for (Building building :
                    buildings) {
                Set<String> types = floorRepository.findDistinctTypeByBuildingId(building.getId());
                Double rating = reviewRepository.averageOfBuildingReviewRating(building.getId());
                BuildingRequest request = modelMapper.map(building, BuildingRequest.class);

                int floorCount = floorRepository.countByBuilding_Id(building.getId());
                request.setOfficeType(types);
                request.setRating(Objects.requireNonNullElse(rating, 0.0));
                request.setFloorCount(floorCount);
                requests.add(request);
            }
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    requests,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to get all building. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    public ResponseEntity<Object> addNewBuilding(BuildingRequest req, Long complexId) {
        log.info("Executing create new Building");
        try {
            Optional<Complex> optionalComplex = complexRepository.findById(complexId);
            if(optionalComplex.isEmpty()) {
                log.info("Complex with ID [{}] not found ", complexId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Building building = modelMapper.map(req, Building.class);
            building.setComplex(optionalComplex.get());
            building.setImages(null);
            buildingRepository.save(building);

            log.info("Successfully added new Building");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(building, BuildingRequest.class),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred while trying to add new Building. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> updateBuilding(Long buildingId, BuildingRequest request) {
        log.info("Updating a Building with ID : [{}]", buildingId);
        try {
            Optional<Building> buildingOptional = buildingRepository.findById(buildingId);
            if(buildingOptional.isEmpty()) {
                log.info("Building with ID : [{}] is not found", buildingId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
            }

            Building building = buildingOptional.get();
            building.setName(request.getName());
            building.setAddress(request.getAddress());
            building.setDescription(request.getDescription());
            building.setCapacity(request.getCapacity());
            building.setBuildingSize(request.getBuildingSize());
            building.setFacilities(request.getFacilities());
            buildingRepository.save(building);

            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(building, BuildingRequest.class),
                    HttpStatus.OK);

        } catch (Exception e){
            log.error("An error occurred while trying to update building with ID : [{}]. Error : [{}]", buildingId, e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> deleteBuilding(Long buildingId) {
        log.info("Deleting building with ID : [{}]", buildingId);
        try {
            Optional<Building> buildingOptional = buildingRepository.findById(buildingId);
            if(buildingOptional.isEmpty()) {
                log.info("Building with ID : [{}] is not found", buildingId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
            }

            buildingRepository.delete(buildingOptional.get());
            log.info("Successfully deleted building with ID : [{}]", buildingId);
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, null, HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to delete building with ID : [{}]. Error : [{}]", buildingId, e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> addImage(Long buildingId, MultipartFile image) throws IOException {
        log.info("Executing add image to building with ID : {}", buildingId);
        //Checking building exist or not on database
        try {
            Optional<Building> building = buildingRepository.findById(buildingId);
            if (building.isEmpty()) {
                log.info("Building with ID [{}] not found ", buildingId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }
            String uploadedFile = FileUtil.upload(path, image);
            if (uploadedFile == null) {
                log.info("Upload failed, check FileUtil log for more information");
                return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                        null,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            buildingImageRepository.save(BuildingImage.builder()
                    .fileName(uploadedFile)
                    .building(building.get())
                    .build());
            log.info("Image Successfully uploaded");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, uploadedFile, HttpStatus.OK);
        } catch (Exception e){
            log.error("An error occurred while trying to add image. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getImage(String filename) {
        return FileUtil.getFileContent(path, filename);
    }

}
