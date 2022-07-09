package com.capstone.booking.service;

import com.capstone.booking.config.security.JwtTokenProvider;
import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Reservation;
import com.capstone.booking.domain.dao.Role;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.RegisterRequest;
import com.capstone.booking.domain.dto.RegisterResponse;
import com.capstone.booking.domain.dto.ReservationRequest;
import com.capstone.booking.domain.payload.EmailPassword;
import com.capstone.booking.domain.payload.TokenResponse;
import com.capstone.booking.repository.RoleRepository;
import com.capstone.booking.repository.UserRepository;
import com.capstone.booking.util.FileUtil;
import com.capstone.booking.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthService {

    @Value("${img-folder.user}")
    private String path;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

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

        if (userRepository.existsByEmail(req.getEmail())) {
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
        Set<Role> roles = new HashSet<>();

        roleRepository.findByName(AppConstant.RoleType.ROLE_USER).ifPresent(roles::add);

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseUtil.build(
                AppConstant.ResponseCode.SUCCESS,
                modelMapper.map(user, RegisterResponse.class),
                HttpStatus.OK
        );
    }

    public ResponseEntity<Object> getProfile(String email) {
        log.info("Getting profile information of user with email : [{}]", email);
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if(userOptional.isEmpty()) {
                log.info("User with Email [{}] is not found", email);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND, null, HttpStatus.BAD_REQUEST);
            }
            RegisterResponse response = modelMapper.map(userOptional.get(), RegisterResponse.class);
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS, response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while trying to get user [{}]'s profile. Error : {}", email, e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> addProfilePicture(MultipartFile file, String email) {
        log.info("Executing add profile picture to user with email : {}", email);
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if(userOptional.isEmpty()) {
                log.info("User with Email [{}] not found ", email);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }


            String uploadedFile = FileUtil.upload(path, file);
            if (uploadedFile == null) {
                log.info("Upload failed, check FileUtil log for more information");
                return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR,
                        null,
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
            User user = userOptional.get();
            if(user.getImage()!=null) {
                FileUtil.delete(path, user.getImage());
            }
            user.setImage(uploadedFile);
            userRepository.save(user);
            log.info("Image Successfully uploaded");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(user, RegisterResponse.class),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("An error occurred while trying to add image. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> getImage(String filename) {
        return FileUtil.getFileContent(path, filename);
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
            Set<String> roles = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            tokenResponse.setRoles(roles);
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

    public ResponseEntity<Object> editProfile(RegisterResponse request, String email) {
        log.info("Executing update profile");
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if(userOptional.isEmpty()) {
                log.info("User with Email [{}] not found ", email);
                return ResponseUtil.build(AppConstant.ResponseCode.DATA_NOT_FOUND,
                        null,
                        HttpStatus.BAD_REQUEST);
            }
            if((!Objects.equals(email, request.getEmail())) && userRepository.existsByEmail(request.getEmail())) {
                log.info("An Account with this email already exist");
                return ResponseUtil.build(AppConstant.ResponseCode.EMAIL_ALREADY_EXIST, null, HttpStatus.BAD_REQUEST);
            }
            User user = userOptional.get();
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            userRepository.save(user);

            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    modelMapper.map(user, RegisterResponse.class),
                    HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error occured while trying to edit profile. Error : {}", e.getMessage());
            return ResponseUtil.build(
                    AppConstant.ResponseCode.UNKNOWN_ERROR,
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
