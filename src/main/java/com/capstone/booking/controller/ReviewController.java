package com.capstone.booking.controller;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dto.ReviewRequest;
import com.capstone.booking.service.ReviewService;
import com.capstone.booking.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;


@RestController
@CrossOrigin
@RequestMapping()
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/review")
    public ResponseEntity<Object> getReviewByBuilding (@RequestParam ("buildingId") Long buildingId,
                                                       @RequestParam ("page") int page ,
                                                       @RequestParam ("limit") int limit) {
        return reviewService.getReviewByBuildingId(buildingId, page, limit);
    }

    @GetMapping("/admin/review")
    public ResponseEntity<Object> getReviewForAdmin () {
        return reviewService.getAllReviewForApproval();
    }

    @PutMapping("/admin/review")
    public ResponseEntity<Object> updateReviewApproval(@RequestParam ("userId") Long userId,
                                                       @RequestParam ("buildingId") Long buildingId,
                                                       Boolean approved) {
        return reviewService.updateApproval(userId, buildingId, approved);
    }

    @PostMapping("/auth/review")
    public ResponseEntity<Object> addReview (@RequestBody ReviewRequest request,
                                             @RequestParam ("buildingId") Long buildingId,
                                             Principal principal) {
        if(principal != null) {
            return reviewService.addReview(request, principal.getName(), buildingId);
        } else {
            return ResponseUtil.build(AppConstant.ResponseCode.NOT_LOGGED_IN, null, HttpStatus.FORBIDDEN);
        }
    }

}
