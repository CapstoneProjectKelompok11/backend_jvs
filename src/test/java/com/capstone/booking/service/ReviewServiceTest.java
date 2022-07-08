package com.capstone.booking.service;

import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.dao.Building;
import com.capstone.booking.domain.dao.Review;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.ReviewAdminResponse;
import com.capstone.booking.domain.dto.ReviewRequest;
import com.capstone.booking.repository.BuildingRepository;
import com.capstone.booking.repository.ReviewRepository;
import com.capstone.booking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ReviewService.class)
class ReviewServiceTest {

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private ReviewRepository reviewRepository;

    @MockBean
    private BuildingRepository buildingRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ReviewService reviewService;

    @Test
    void getReviewByBuildingId_Success_Test() {
        List<Review> reviews = new ArrayList<>();
        Review review = Review.builder()
                .review("this is review")
                .rating(5)
                .build();
        reviews.add(review);
        ReviewRequest request = ReviewRequest.builder()
                .review("this is review")
                .rating(5)
                .build();


        Pageable pageable = PageRequest.of(0, 1);
        Page<Review> reviewPage = new PageImpl<Review>(reviews.subList(0,1), pageable, reviews.size());

        when(reviewRepository.findAllByBuildingId(anyLong(), any(Pageable.class))).thenReturn(reviewPage);
        when(modelMapper.map(any(), eq(ReviewRequest.class))).thenReturn(request);

        ResponseEntity<Object> responseEntity = reviewService.getReviewByBuildingId(1L, 0,1);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        List<ReviewRequest> result = ((List<ReviewRequest>) apiResponse.getData());

        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("this is review", result.get(0).getReview());
        assertEquals(5, result.get(0).getRating());
    }

    @Test
    void getReviewByBuildingId_Error_Test() {

        when(reviewRepository.findAllByBuildingId(anyLong(), any(Pageable.class))).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reviewService.getReviewByBuildingId(1L, 0,1);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void addReview_Success_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        User user = User.builder()
                .id(1L)
                .email("email@email.com")
                .build();

        Review review = Review.builder()
                .review("this is review")
                .rating(5)
                .building(building)
                .build();

        ReviewRequest request = ReviewRequest.builder()
                .review("this is review")
                .rating(5)
                .build();


        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(modelMapper.map(any(), eq(Review.class))).thenReturn(review);
        when(reviewRepository.save(any())).thenReturn(review);
        when(modelMapper.map(any(), eq(ReviewRequest.class))).thenReturn(request);

        ResponseEntity<Object> responseEntity = reviewService.addReview(request, "some_long_email",1L);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("this is review", ((ReviewRequest) apiResponse.getData()).getReview());
        assertEquals(5, ((ReviewRequest) apiResponse.getData()).getRating());
    }

    @Test
    void addReview_BuildingEmpty_Test() {

        ReviewRequest request = ReviewRequest.builder()
                .review("this is review")
                .rating(5)
                .build();
        User user = User.builder()
                .id(1L)
                .email("email@email.com")
                .build();

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(buildingRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reviewService.addReview(request, "some_long_email",1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void addReview_UserEmpty_Test() {

        ReviewRequest request = ReviewRequest.builder()
                .review("this is review")
                .rating(5)
                .build();


        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reviewService.addReview(request, "some_long_email",1L);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void addReview_Error_Test() {

        ReviewRequest request = ReviewRequest.builder()
                .review("this is review")
                .rating(5)
                .build();

        when(userRepository.findUserByEmail(anyString())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reviewService.addReview(request, "some_long_email",1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void getAllReviewForApproval_Success_Test() {
        List<Review> reviews = new ArrayList<>();
        Review review = Review.builder()
                .review("this is review")
                .rating(5)
                .build();
        reviews.add(review);

        ReviewAdminResponse request = ReviewAdminResponse.builder()
                .review("this is review")
                .rating(5)
                .build();


        when(reviewRepository.findAllByIsApprovedIsNull()).thenReturn(reviews);
        when(modelMapper.map(any(), eq(ReviewAdminResponse.class))).thenReturn(request);

        ResponseEntity<Object> responseEntity = reviewService.getAllReviewForApproval();
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        List<ReviewAdminResponse> result = ((List<ReviewAdminResponse>) apiResponse.getData());

        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("this is review", result.get(0).getReview());
        assertEquals(5, result.get(0).getRating());
    }

    @Test
    void getAllReviewForApproval_Error_Test() {
        when(reviewRepository.findAllByIsApprovedIsNull()).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reviewService.getAllReviewForApproval();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void updateApproval_Success_Test() {
        List<Review> reviews = new ArrayList<>();
        Review review = Review.builder()
                .review("this is review")
                .rating(5)
                .build();
        reviews.add(review);

        ReviewAdminResponse request = ReviewAdminResponse.builder()
                .review("this is review")
                .rating(5)
                .build();


        when(reviewRepository.findByUserIdAndBuildingId(anyLong(), anyLong())).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenReturn(review);
        when(modelMapper.map(any(), eq(ReviewAdminResponse.class))).thenReturn(request);

        ResponseEntity<Object> responseEntity = reviewService.updateApproval(1L, 1L, true);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        ReviewAdminResponse result = ((ReviewAdminResponse) apiResponse.getData());

        assertNotNull(apiResponse);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("this is review", result.getReview());
        assertEquals(5, result.getRating());
    }

    @Test
    void updateApproval_ReviewEmpty_Test() {
        List<Review> reviews = new ArrayList<>();
        Review review = Review.builder()
                .review("this is review")
                .rating(5)
                .build();
        reviews.add(review);

        ReviewAdminResponse request = ReviewAdminResponse.builder()
                .review("this is review")
                .rating(5)
                .build();


        when(reviewRepository.findByUserIdAndBuildingId(anyLong(), anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = reviewService.updateApproval(1L, 1L, true);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void updateApproval_Error_Test() {
        List<Review> reviews = new ArrayList<>();
        Review review = Review.builder()
                .review("this is review")
                .rating(5)
                .build();
        reviews.add(review);

        ReviewAdminResponse request = ReviewAdminResponse.builder()
                .review("this is review")
                .rating(5)
                .build();


        when(reviewRepository.findByUserIdAndBuildingId(anyLong(), anyLong())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = reviewService.updateApproval(1L, 1L, true);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}