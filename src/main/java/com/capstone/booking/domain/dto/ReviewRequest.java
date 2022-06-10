package com.capstone.booking.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewRequest {

    @Schema(hidden = true)
    private RegisterResponse user;

    @Schema(type = "String", example = "Fasilitasnya sangat bagus dan pelayanannya ramah, proses reservasinya juga simpel, tinggal sat set sat set")
    private String review;

    @Schema(type = "int", example = "5")
    private int rating;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @Schema(hidden = true)
    private LocalDateTime reviewDate;

}
