package com.capstone.booking.util;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.dao.Building;
import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ResponseUtil.class)
class ResponseUtilTest {
    @Test
    void buildBody_Test() {
        BuildingRequest data = BuildingRequest.builder()
                .id(1L)
                .name("Test Building")
                .build();

        ResponseEntity<Object> responseEntity = ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, data, HttpStatus.OK);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        assert apiResponse != null;
        assertEquals("Test Building", ((BuildingRequest) apiResponse.getData()).getName());
        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());

    }
}