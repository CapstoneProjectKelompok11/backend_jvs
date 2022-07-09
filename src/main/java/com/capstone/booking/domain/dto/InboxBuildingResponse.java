package com.capstone.booking.domain.dto;

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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InboxBuildingResponse {
    private Long id;

    private String buildingName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.DATETIME_JSON_FORMAT)
    private LocalDateTime latestMessageTimestamp;

    private String latestMessage;

    private String buildingImages;

}
