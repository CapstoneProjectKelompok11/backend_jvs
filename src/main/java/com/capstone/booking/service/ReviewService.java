package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Building;
import com.capstone.booking.domain.dao.Review;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.domain.dto.ReviewRequest;
import com.capstone.booking.repository.BuildingRepository;
import com.capstone.booking.repository.ReviewRepository;
import com.capstone.booking.repository.UserRepository;
import com.capstone.booking.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class ReviewService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<Object> getReviewByBuildingId (Long buildingId, int page, int limit) {
        log.info("Executing get all Review by Building ID [{}]", buildingId);
        try {
            Pageable pageable = PageRequest.of(page, limit);
            Page<Review> reviews = reviewRepository.findAllByBuilding_Id(buildingId, pageable);
            List<ReviewRequest> reviewRequests = new ArrayList<>();
            for (Review review :
                    reviews) {
                ReviewRequest request = modelMapper.map(review, ReviewRequest.class);
                reviewRequests.add(request);
            }

            log.info("Successfully retrieved Reviews by Building ID [{}]", buildingId);
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    reviewRequests,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("An error occurred while trying to get Reviews by Building ID [{}]. Error : {}",
                    buildingId,
                    e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> addReview (ReviewRequest req, Long userId, Long buildingId) {
        log.info("Executing add new review");
        try{
            Optional<Building> optionalBuilding = buildingRepository.findById(buildingId);
            if(optionalBuilding.isEmpty()) {
                log.info("Building with ID [{}] not found ", buildingId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Optional<User> optionalUser = userRepository.findById(userId);
            if(optionalUser.isEmpty()) {
                log.info("User with ID [{}] not found ", userId);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }

            Review review = modelMapper.map(req, Review.class);
            review.setBuilding(optionalBuilding.get());
            review.setUser(optionalUser.get());
            review.setReviewDate(LocalDateTime.now());
            reviewRepository.save(review);
            log.info("Successfully added Review");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(review, ReviewRequest.class),
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occured while trying to add review from User with ID {} to Building with ID {}. Error : {} ",
                    userId, buildingId, e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
