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
public class ReservationRequest {

    @Schema(hidden = true)
    private Long id;

    @Schema(hidden = true)
    private RegisterResponse user;

    @Schema(hidden = true)
    private FloorRequest floor;

    @Schema(hidden = true)
    private BuildingRequest building;

    @Schema(type = "String", example = "20-06-2022 08:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.DATETIME_JSON_FORMAT)
    private LocalDateTime startReservation;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.DATETIME_JSON_FORMAT)
    @Schema(type = "String", format = "date-time", example = "20-06-2022 19:10:33")
    private LocalDateTime endReservation;

    @Schema(type = "String", example = "PT. Alterra Academy")
    private String company;

    @Schema(type = "Long", example = "4300000")
    private Long price;

    @Schema(type = "String")
    private String note;

    @Schema(hidden = true)
    private String image;

    @Schema(hidden = true)
    private AppConstant.ReservationStatus status;
}
