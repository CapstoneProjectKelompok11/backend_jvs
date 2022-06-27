package com.capstone.booking.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FloorRequest {
    @Schema(hidden = true)
    private Long id;

    @Schema(type = "String", example = "Floor 48th")
    private String name;

    @Schema(type = "String", example = "Serviced Office")
    private String type;

    @Schema(type = "String", example = "320.20 sqm")
    private String floorSize;

    @Schema(type = "int", example = "8")
    private int maxCapacity;

    @Schema(type = "int", example = "2300000")
    private int startingPrice;

    @Schema(hidden = true)
    private String image;


}
