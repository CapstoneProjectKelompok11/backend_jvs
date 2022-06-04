package com.capstone.booking.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private ComplexRequest complex;
}
