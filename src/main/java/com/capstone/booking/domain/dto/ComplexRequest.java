package com.capstone.booking.domain.dto;

import com.capstone.booking.domain.dao.City;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComplexRequest {

    @Schema(type = "long", hidden = true)
    private Long id;

    @Schema(type = "String", example = "SCBD", description = "Nama Complex")
    private String complexName;

    @Schema(hidden = true)
    private CityRequest city;
}
