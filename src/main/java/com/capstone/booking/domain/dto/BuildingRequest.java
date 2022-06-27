package com.capstone.booking.domain.dto;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.BuildingImage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildingRequest {

    @Schema(hidden = true)
    private Long id;

    @Schema(type = "String", example = "Equity Tower", description = "Nama Bangunan")
    private String name;

    @Schema(type = "String", example = "SCBD Lot 9 Jl. Jenderal Sudirman Kav. 52-53, RT.5/RW.3, Senayan",
            description = "Alamat Bangunan")
    private String address;

    @Schema(type = "String", description = "Deskripsi Bangunan")
    private String description;

    @Schema(hidden = true)
    private double rating;

    @Schema(hidden = true)
    private Set<String> officeType;

    @Schema(type = "String", example = "290 x 290 m2")
    private String buildingSize;

    @Schema(hidden = true)
    private int floorCount;

    @Schema(type = "int", example = "30")
    private int capacity;

    @Schema(hidden = true)
    private List<BuildingImage> images;

    @Schema(hidden = true)
    private ComplexRequest complex;

    @Schema(type = "Set<FacilityType>")
    private Set<AppConstant.FacilityType> facilities;
}
