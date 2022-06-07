package com.capstone.booking.domain.dto;

import com.capstone.booking.domain.dao.BuildingImage;
import com.capstone.booking.domain.dao.FloorImage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FloorRequest {
    private Long id;

    private String name;

    private String type;

    private String size;

    private int maxCapacity;

    private String description;

    @Schema(hidden = true)
    private List<FloorImage> images;

}
