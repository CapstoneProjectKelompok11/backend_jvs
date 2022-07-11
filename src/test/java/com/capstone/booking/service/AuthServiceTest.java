package com.capstone.booking.service;

import com.capstone.booking.config.security.JwtTokenProvider;
import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.common.ApiResponseStatus;
import com.capstone.booking.domain.dao.Role;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.RegisterRequest;
import com.capstone.booking.domain.dto.RegisterResponse;
import com.capstone.booking.domain.payload.EmailPassword;
import com.capstone.booking.domain.payload.TokenResponse;
import com.capstone.booking.repository.RoleRepository;
import com.capstone.booking.repository.UserRepository;
import com.capstone.booking.util.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthService.class)
class AuthServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private AuthService authService;

    @Test
    void register_Success_Test() {
        RegisterRequest req = new RegisterRequest(
                "Felix",
                "Reinaldo",
                "08554865949",
                "felixreinaldo@gmail.com",
                "password"
        );

        RegisterResponse response = RegisterResponse.builder()
                .id(1L)
                .firstName("Felix")
                .lastName("Reinaldo")
                .phone("08554865949")
                .email("felixreinaldo@gmail.com")
                .build();

        User user = User.builder()
                .id(1L)
                .email("felixreinaldo@gmail.com")
                .build();
        Role role = new Role(1L, AppConstant.RoleType.ROLE_USER);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password_mock");
        when(modelMapper.map(any(), eq(User.class))).thenReturn(user);
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(any(), eq(RegisterResponse.class))).thenReturn(response);

        ResponseEntity responseEntity = authService.register(req);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("felixreinaldo@gmail.com", ((RegisterResponse) apiResponse.getData()).getEmail());
        assertEquals("Felix", ((RegisterResponse) apiResponse.getData()).getFirstName());
        assertEquals("Reinaldo", ((RegisterResponse) apiResponse.getData()).getLastName());
        assertEquals("08554865949", ((RegisterResponse) apiResponse.getData()).getPhone());
    }

    @Test
    void register_Duplicate_Test(){
        RegisterRequest req = new RegisterRequest(
                "Felix",
                "Reinaldo",
                "08554865949",
                "felixreinaldo@gmail.com",
                "password"
        );

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ResponseEntity responseEntity = authService.register(req);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Email is already registered", apiResponse.getData().toString());
    }

    @Test
    void generateToken_Success_Test() {
        EmailPassword emailPassword = new EmailPassword();
        emailPassword.setEmail("felixreinaldo@gmail.com");
        emailPassword.setPassword("password");
        Authentication authentication = new UsernamePasswordAuthenticationToken("uname", "password");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtTokenProvider.generateToken(any())).thenReturn("some_long_long_token");

        ResponseEntity responseEntity = authService.generateToken(emailPassword);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("some_long_long_token", ((TokenResponse) apiResponse.getData()).getToken());
        assertTrue(((TokenResponse) apiResponse.getData()).getRoles().isEmpty() );
    }

    @Test
    void generateToken_BadCredentials_Test() {
        EmailPassword emailPassword = new EmailPassword();
        emailPassword.setEmail("felixreinaldo@gmail.com");
        emailPassword.setPassword("passwd");

        when(authenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);

        ResponseEntity responseEntity = authService.generateToken(emailPassword);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());
        String code = ((ApiResponseStatus) apiResponse.getStatus()).getCode();

        assertEquals("BAD_CREDENTIALS", code);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    @Test
    void generateToken_Error_Test() {
        EmailPassword emailPassword = new EmailPassword();
        emailPassword.setEmail("felixreinaldo@gmail.com");
        emailPassword.setPassword("passwd");

        when(authenticationManager.authenticate(any())).thenThrow(NullPointerException.class);

        ResponseEntity responseEntity = authService.generateToken(emailPassword);
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());
        String code = ((ApiResponseStatus) apiResponse.getStatus()).getCode();

        assertEquals("UNKNOWN_ERROR", code);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void getProfile_Success_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(modelMapper.map(any(), eq(RegisterResponse.class))).thenReturn(registerResponse);

        ResponseEntity responseEntity = authService.getProfile("email");
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("some-email@gmail.com", ((RegisterResponse) apiResponse.getData()).getEmail());
    }

    @Test
    void getProfile_UserEmpty_Test() {
        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity responseEntity = authService.getProfile("email");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void getProfile_Error_Test() {
        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        when(userRepository.findUserByEmail(anyString())).thenThrow(NullPointerException.class);

        ResponseEntity responseEntity = authService.getProfile("email");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void addProfilePicture_Success_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));

        try (MockedStatic<FileUtil> utilities = Mockito.mockStatic(FileUtil.class)) {
            utilities.when(() -> FileUtil.upload(anyString(), any(MultipartFile.class))).thenReturn("some_file_name");
            when(userRepository.save(any())).thenReturn(user);
            when(modelMapper.map(any(), eq(RegisterResponse.class))).thenReturn(registerResponse);

            ResponseEntity<Object> responseEntity = authService.addProfilePicture(file, "some-email@email.com");

            ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        }
    }

    @Test
    void addProfilePicture_UserEmpty_Test() {
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = authService.addProfilePicture(file, "some-email@email.com");
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    @Test
    void addProfilePicture_UploadFailed_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));

        try (MockedStatic<FileUtil> utilities = Mockito.mockStatic(FileUtil.class)) {
            utilities.when(() -> FileUtil.upload(anyString(), any(MultipartFile.class))).thenReturn(null);

            ResponseEntity<Object> responseEntity = authService.addProfilePicture(file, "some-email@email.com");
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        }
    }

    @Test
    void addProfilePicture_Error_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        MockMultipartFile file = new MockMultipartFile(
                "name",
                "some_original_filename",
                MediaType.TEXT_PLAIN_VALUE,
                "think of this as image".getBytes());

        when(userRepository.findUserByEmail(anyString())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = authService.addProfilePicture(file, "some-email@email.com");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }

    @Test
    void editProfile_Success_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(any(), eq(RegisterResponse.class))).thenReturn(registerResponse);

        ResponseEntity<Object> responseEntity = authService.editProfile(registerResponse, "some-email@gmail.com");
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("some-email@gmail.com", ((RegisterResponse) apiResponse.getData()).getEmail());
    }

    @Test
    void editProfile_UserEmpty_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Object> responseEntity = authService.editProfile(registerResponse, "some-email@gmail.com");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void editProfile_EmailExist_Test() {
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        ResponseEntity<Object> responseEntity = authService.editProfile(registerResponse, "new-email@gmail.com");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void editProfile_Error_Test() {
        RegisterResponse registerResponse = RegisterResponse.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();

        when(userRepository.findUserByEmail(anyString())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = authService.editProfile(registerResponse, "some-email@gmail.com");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}