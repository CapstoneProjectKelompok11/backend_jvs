package com.capstone.booking.domain.payload;

import com.capstone.booking.domain.dao.Role;
import lombok.Data;

import java.util.Set;

@Data
public class TokenResponse {
    private String token;
    private Set<String> roles;
}
