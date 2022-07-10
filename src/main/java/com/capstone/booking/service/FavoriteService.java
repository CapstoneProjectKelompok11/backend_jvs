package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Building;
import com.capstone.booking.domain.dao.Favorite;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.domain.dto.FavoriteResponse;
import com.capstone.booking.repository.*;
import com.capstone.booking.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class FavoriteService {
    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<Object> getFavorite(String email) {
        log.info("Executing get favorites");
        try {
            List<Favorite> favorites = favoriteRepository.findFavoriteByUserEmailAndBuildingIsDeletedFalse(email);
            List<FavoriteResponse> responses = new ArrayList<>();
            for (Favorite favorite :
                    favorites) {
                Set<String> types = floorRepository.findDistinctTypeByBuildingId(favorite.getBuilding().getId());
                Double rating = reviewRepository.averageOfBuildingReviewRating(favorite.getBuilding().getId());
                BuildingRequest request = modelMapper.map(favorite.getBuilding(), BuildingRequest.class);
                int floorCount = floorRepository.countByBuilding_Id(favorite.getBuilding().getId());
                request.setOfficeType(types);
                request.setRating(Objects.requireNonNullElse(rating, 0.0));
                request.setFloorCount(floorCount);
                FavoriteResponse response = FavoriteResponse.builder().building(request).build();
                responses.add(response);
            }
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, responses, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while trying to get Favorites. Error: {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> unFavorite(String email, Long buildingId) {
        log.info("Unfavoriting a building");
        try {
            Optional<Favorite> favoriteOptional = favoriteRepository.findFavorite(email, buildingId);
            if(favoriteOptional.isEmpty()) {
                log.info("Object Favorite not found ");
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }
            favoriteRepository.delete(favoriteOptional.get());
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, null, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occurred while trying to unFavorites. Error: {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> addToFavorites(String email, Long buildingId) {
        try {
            Optional<Building> buildingOptional = buildingRepository.findById(buildingId);
            if(buildingOptional.isEmpty()) {
                log.info("Building with ID [{}] not found ", buildingId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if(userOptional.isEmpty()) {
                log.info("User with Email [{}] not found ", email);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Favorite favorite = Favorite.builder()
                    .user(userOptional.get())
                    .building(buildingOptional.get())
                    .build();

            favoriteRepository.save(favorite);
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, null, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while trying to get Favorites. Error: {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
