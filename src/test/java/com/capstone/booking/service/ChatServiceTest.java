package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.common.ApiResponse;
import com.capstone.booking.domain.dao.*;
import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.domain.dto.ChatResponse;
import com.capstone.booking.domain.dto.InboxBuildingResponse;
import com.capstone.booking.domain.dto.RegisterResponse;
import com.capstone.booking.repository.BuildingRepository;
import com.capstone.booking.repository.ChatRepository;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ChatService.class)
class ChatServiceTest {
    @MockBean
    private ChatRepository chatRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BuildingRepository buildingRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Autowired
    private ChatService chatService;


    @Test
    @WithMockUser(username = "some-emaill", authorities = {"USER"})
    void userSendChat_Success_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        Chat chat = Chat.builder()
                .id(1L)
                .user(user)
                .building(building)
                .sentByUser(true)
                .build();
        ChatResponse response = ChatResponse.builder()
                .id(1L)
                .user(user)
                .sentByUser(true)
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(chatRepository.save(chat)).thenReturn(chat);
        when(modelMapper.map(any(), eq(ChatResponse.class))).thenReturn(response);

        ChatResponse chatResponse = chatService.userSendChat("meeeesssaageee", 1L);

        assertEquals(response.getId(), chatResponse.getId());
        assertEquals(user, chatResponse.getUser());
        assertEquals(true, chatResponse.getSentByUser());

    }
    @Test
    void userSendChat_BuildingEmpty_Test() {
        when(buildingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> chatService.userSendChat("meeeesssaageee", 1L));

    }

    @Test
    @WithMockUser(username = "some-emaill", authorities = {"USER"})
    void userSendChat_UserEmpty_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> chatService.userSendChat("meeeesssaageee", 1L));

    }

    @Test
    @WithMockUser(username = "some-emaill", authorities = {"USER"})
    void userSendChat_Error_Test() {

        when(buildingRepository.findById(anyLong())).thenThrow(NullPointerException.class);

        assertThrows(NullPointerException.class, () -> chatService.userSendChat("meeeesssaageee", 1L));

    }


    @Test
    void adminSendChat_Success_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        Role role = new Role(1L, AppConstant.RoleType.ROLE_ADMIN);
        User user = User.builder()
                .id(1L)
                .roles(Collections.emptySet())
                .email("some-email@gmail.com")
                .build();
        Chat chat = Chat.builder()
                .id(1L)
                .user(user)
                .building(building)
                .sentByUser(false)
                .build();
        ChatResponse response = ChatResponse.builder()
                .id(1L)
                .user(user)
                .sentByUser(false)
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(AppConstant.RoleType.ROLE_ADMIN)).thenReturn(Optional.of(role));
        when(chatRepository.save(any())).thenReturn(chat);
        when(modelMapper.map(any(), eq(ChatResponse.class))).thenReturn(response);

        ChatResponse chatResponse = chatService.adminSendChat("user-email@email.com","meeeesssaageee", 1L);

        assertEquals(response.getId(), chatResponse.getId());
        assertEquals(user, chatResponse.getUser());
        assertEquals(false, chatResponse.getSentByUser());
    }

    @Test
    void adminSendChat_BuildingEmpty_Test() {

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> chatService.adminSendChat("user-email@email.com","msg", 1L) );

    }

    @Test
    void adminSendChat_UserEmpty_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> chatService.adminSendChat("user-email@email.com","msg", 1L) );

    }

    @Test
    void adminSendChat_RoleEmpty_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        User user = User.builder()
                .id(1L)
                .roles(Collections.emptySet())
                .email("some-email@gmail.com")
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(AppConstant.RoleType.ROLE_ADMIN)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> chatService.adminSendChat("user-email@email.com","msg", 1L) );
    }
    @Test
    void adminSendChat_AdminChatToAdmin_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        Role role = new Role(1L, AppConstant.RoleType.ROLE_ADMIN);
        User user = User.builder()
                .id(1L)
                .roles(Set.of(role))
                .email("some-email@gmail.com")
                .build();

        when(buildingRepository.findById(anyLong())).thenReturn(Optional.of(building));
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(AppConstant.RoleType.ROLE_ADMIN)).thenReturn(Optional.of(role));

        assertThrows(UnsupportedOperationException.class,
                () -> chatService.adminSendChat("user-email@email.com","msg", 1L) );

    }


    @Test
    @WithMockUser(username = "some-emaill", authorities = {"USER"})
    void getChatByUser_Success_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        User user = User.builder()
                .id(1L)
                .roles(Collections.emptySet())
                .email("some-email@gmail.com")
                .build();
        List<Chat> chats = new ArrayList<>();
        Chat chat = Chat.builder()
                .id(1L)
                .user(user)
                .building(building)
                .sentByUser(true)
                .build();
        chats.add(chat);
        ChatResponse response = ChatResponse.builder()
                .id(1L)
                .user(user)
                .sentByUser(true)
                .build();

        when(chatRepository.findAllByUserEmailAndBuildingId(anyString(), anyLong())).thenReturn(chats);
        when(modelMapper.map(any(), eq(ChatResponse.class))).thenReturn(response);

        List<ChatResponse> chatResponses = chatService.getChatByUser(1L);

        assertEquals(response.getId(), chatResponses.get(0).getId());
        assertEquals(user, chatResponses.get(0).getUser());
        assertEquals(true, chatResponses.get(0).getSentByUser());
    }

    @Test
    @WithMockUser(username = "some-emaill", authorities = {"USER"})
    void getChatByUser_Error_Test() {

        when(chatRepository.findAllByUserEmailAndBuildingId(anyString(), anyLong())).thenThrow(NullPointerException.class);

        assertThrows(NullPointerException.class, () -> chatService.getChatByUser(1L));
    }

    @Test
    void getChatForAdmin_Success_Test() {
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .capacity(100)
                .build();
        Role role = new Role(1L, AppConstant.RoleType.ROLE_ADMIN);
        User user = User.builder()
                .id(1L)
                .roles(Collections.emptySet())
                .email("some-email@gmail.com")
                .build();
        List<Chat> chats = new ArrayList<>();
        Chat chat = Chat.builder()
                .id(1L)
                .user(user)
                .building(building)
                .sentByUser(false)
                .build();
        chats.add(chat);
        ChatResponse response = ChatResponse.builder()
                .id(1L)
                .user(user)
                .sentByUser(false)
                .build();

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));
        when(chatRepository.findAllByUserEmailAndBuildingId(anyString(), anyLong())).thenReturn(chats);
        when(modelMapper.map(any(), eq(ChatResponse.class))).thenReturn(response);

        List<ChatResponse> chatResponses = chatService.getChatForAdmin("useremail", 1L);

        assertEquals(response.getId(), chatResponses.get(0).getId());
        assertEquals(user, chatResponses.get(0).getUser());
        assertEquals(false, chatResponses.get(0).getSentByUser());
    }

    @Test
    void getChatForAdmin_UserEmpty_Test() {

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> chatService.getChatForAdmin("user-email@email.com", 1L) );
    }
    @Test
    void getChatForAdmin_RoleEmpty_Test() {
        User user = User.builder()
                .id(1L)
                .roles(Collections.emptySet())
                .email("some-email@gmail.com")
                .build();

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> chatService.getChatForAdmin("user-email@email.com", 1L) );
    }

    @Test
    void getChatForAdmin_NotUserEmail_Test() {

        Role role = new Role(1L, AppConstant.RoleType.ROLE_ADMIN);
        User user = User.builder()
                .id(1L)
                .roles(Set.of(role))
                .email("some-email@gmail.com")
                .build();

        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(user));
        when(roleRepository.findByName(any())).thenReturn(Optional.of(role));

        assertThrows(IllegalArgumentException.class,
                () -> chatService.getChatForAdmin("user-email@email.com", 1L) );
    }


    @Test
    @WithMockUser(username = "some-emaill", authorities = {"USER"})
    void showBuildingChatHistory_Success_Test() {
        List<BuildingImage> buildingImages = new ArrayList<>();
        buildingImages.add(BuildingImage.builder().fileName("fileee").build());
        Building building = Building.builder()
                .id(1L)
                .name("Gedung A")
                .address("Jalan A")
                .images(buildingImages)
                .capacity(100)
                .build();
        User user = User.builder()
                .id(1L)
                .email("some-email@gmail.com")
                .build();
        ChatResponse response = ChatResponse.builder()
                .id(1L)
                .user(user)
                .message("last message")
                .sentByUser(true)
                .build();
        List<Chat> chats = new ArrayList<>();
        Chat chat = Chat.builder()
                .id(1L)
                .user(user)
                .building(building)
                .sentByUser(true)
                .build();
        chats.add(chat);

        when(buildingRepository.findAllByChat(anyString())).thenReturn(Set.of(building));
        when(chatRepository.findAllByUserEmailAndBuildingId(anyString(), anyLong())).thenReturn(chats);
        when(modelMapper.map(any(), eq(ChatResponse.class))).thenReturn(response);

        ResponseEntity<Object> responseEntity = chatService.showBuildingChatHistory("some-emaill");
        ApiResponse apiResponse = ((ApiResponse) responseEntity.getBody());


        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("last message", ((List<InboxBuildingResponse>) apiResponse.getData()).get(0).getLatestMessage());
    }

    @Test
    @WithMockUser(username = "some-emaill", authorities = {"USER"})
    void showBuildingChatHistory_Error_Test() {
        when(buildingRepository.findAllByChat(anyString())).thenThrow(NullPointerException.class);

        ResponseEntity<Object> responseEntity = chatService.showBuildingChatHistory("some-emaill");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
    }
}