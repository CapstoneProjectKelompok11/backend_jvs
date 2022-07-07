package com.capstone.booking.domain.dto;

import com.capstone.booking.constant.AppConstant;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservationUserRequest {

    @Schema(type = "String", example = "20-06-2022 08:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.DATETIME_JSON_FORMAT)
    private LocalDateTime startReservation;

    @Schema(type = "String", example = "PT. Alterra Academy")
    private String company;

    @Schema(type = "String", example = "08248435468")
    private String phone;

    @Schema(type = "int", example = "9")
    private Integer participant;

    @Schema(type = "String")
    private String note;

}
