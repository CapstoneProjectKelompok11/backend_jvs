package com.capstone.booking.service;

import com.capstone.booking.config.security.JwtTokenProvider;
import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.user.RegisterRequest;
import com.capstone.booking.domain.dto.user.RegisterResponse;
import com.capstone.booking.domain.payload.EmailPassword;
import com.capstone.booking.domain.payload.TokenResponse;
import com.capstone.booking.repository.UserRepository;
import com.capstone.booking.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    public ResponseEntity<Object> register(RegisterRequest req) {
        log.info("Executing register new user");
        Optional<User> userOptional = userRepository.findUserByEmail(req.getEmail());

        if (userOptional.isPresent()) {
            log.info("User with email : [{}] already exist, aborting register", req.getEmail());
            return ResponseUtil.build(
                    AppConstant.ResponseCode.BAD_CREDENTIALS,
                    "Email is already registered",
                    HttpStatus.BAD_REQUEST
            );
        }

        log.info("User doesnt exist yet, creating new user");
        User user = modelMapper.map(req, User.class);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        userRepository.save(user);
        log.info("User doesnt exist yet, creating new user");
        return ResponseUtil.build(
                AppConstant.ResponseCode.SUCCESS,
                modelMapper.map(user, RegisterResponse.class),
                HttpStatus.OK
        );
    }

    public ResponseEntity<Object> generateToken(EmailPassword req) {
        log.info("Generating JWT based on provided email and password");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getEmail(),
                            req.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);
            TokenResponse tokenResponse = new TokenResponse();
            tokenResponse.setToken(jwt);

            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, tokenResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            log.error("Bad Credential", e);
            return ResponseUtil.build(
                    AppConstant.ResponseCode.BAD_CREDENTIALS,
                    "Email or Password is incorrect",
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseUtil.build(
                    AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
