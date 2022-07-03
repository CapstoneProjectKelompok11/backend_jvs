package com.capstone.booking.domain.dto;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Building;
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
public class ReviewAdminResponse {
    @Schema(hidden = true)
    private RegisterResponse user;

    private Building building;

    @Schema(type = "String", example = "Fasilitasnya sangat bagus dan pelayanannya ramah, proses reservasinya juga simpel, tinggal sat set sat set")
    private String review;

    @Schema(type = "int", example = "5")
    private int rating;

    private Boolean isApproved;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.DATE_JSON_FORMAT)
    @Schema(hidden = true)
    private LocalDateTime reviewDate;

}
