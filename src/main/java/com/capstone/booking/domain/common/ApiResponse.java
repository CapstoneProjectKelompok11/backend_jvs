package com.capstone.booking.domain.common;

import com.capstone.booking.constant.AppConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApiResponse <T> {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.DATETIME_JSON_FORMAT)
    private LocalDateTime timestamp;

    private ApiResponseStatus status;

    private T data;

}
