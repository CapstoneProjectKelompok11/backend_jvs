package com.capstone.booking.domain.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailPassword {
    private String email;
    private String password;
}
