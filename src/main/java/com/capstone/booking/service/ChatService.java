package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Building;
import com.capstone.booking.domain.dao.Chat;
import com.capstone.booking.domain.dao.Role;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.BuildingRequest;
import com.capstone.booking.domain.dto.ChatResponse;
import com.capstone.booking.domain.dto.InboxBuildingResponse;
import com.capstone.booking.repository.*;
import com.capstone.booking.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FloorRepository floorRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ModelMapper modelMapper;

    public ChatResponse userSendChat(String message, Long buildingId) {
        log.info("Executing sending chat");
        try {
            Optional<Building> building = buildingRepository.findById(buildingId);
            if(building.isEmpty()) {
                log.info("Building data not found");
                throw new IllegalArgumentException("Building Not Found");
            }
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Optional<User> senderOptional = userRepository.findUserByEmail(email);
            if(senderOptional.isEmpty()){
                log.info("User data not found");
                throw new IllegalArgumentException("User Not Found");
            }

            Chat chat = Chat.builder()
                    .user(senderOptional.get())
                    .sentByUser(true)
                    .message(message)
                    .building(building.get())
                    .timestamp(LocalDateTime.now())
                    .build();

            chatRepository.save(chat);
            return modelMapper.map(chat, ChatResponse.class);

        } catch (Exception e) {
            log.error("Error occurred while trying to send chat. Error : {}", e.getMessage());
            throw e;
        }
    }

    public ChatResponse adminSendChat(String receiver, String message, Long buildingId) {
        log.info("Executing sending chat");
        try {
            Optional<Building> building = buildingRepository.findById(buildingId);
            if(building.isEmpty()) {
                log.info("Building data not found");
                throw new IllegalArgumentException("Building Not Found");
            }
            Optional<User> receiverOptional = userRepository.findUserByEmail(receiver);
            if(receiverOptional.isEmpty()){
                log.info("User data not found");
                throw new IllegalArgumentException("User Not Found");
            }

            Optional<Role> roleOptional = roleRepository.findByName(AppConstant.RoleType.ROLE_ADMIN);
            if(roleOptional.isEmpty()) {
                log.info("Role Not Found");
                throw new IllegalArgumentException("Role Not Found");
            }

            if(receiverOptional.get().getRoles().contains(roleOptional.get())){
                log.info("Chat is only available from admin to user and vice versa");
                throw new UnsupportedOperationException("Admin can only chat to user and vice versa");
            }

            Chat chat = Chat.builder()
                    .user(receiverOptional.get())
                    .sentByUser(false)
                    .message(message)
                    .building(building.get())
                    .timestamp(LocalDateTime.now())
                    .build();

            chatRepository.save(chat);
            return modelMapper.map(chat, ChatResponse.class);
        } catch (Exception e) {
            log.error("Error occurred while trying to send chat. Error : {}", e.getMessage());
            throw e;
        }
    }

    public List<ChatResponse> getChatByUser(Long buildingId) {
        log.info("Executing Getting chat history");
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            List<Chat> chats = chatRepository.findAllByUserEmailAndBuildingId(email, buildingId);
            List<ChatResponse> chatResponses = new ArrayList<>();
            for (Chat chat :
                    chats) {
                chatResponses.add(modelMapper.map(chat, ChatResponse.class));
            }
            return chatResponses;

        } catch (Exception e) {
            log.error("Error occurred while trying to get chat. Error : {}", e.getMessage());
            throw e;
        }
    }

    public List<ChatResponse> getChatForAdmin(String userEmail, Long buildingId) {
        log.info("Executing Getting chat history for Admin");
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(userEmail);
            if(userOptional.isEmpty()){
                log.info("User data not found");
                throw new IllegalArgumentException("User Not Found");
            }
            Optional<Role> roleOptional = roleRepository.findByName(AppConstant.RoleType.ROLE_ADMIN);
            if(roleOptional.isEmpty()) {
                log.info("Role Not Found");
                throw new IllegalArgumentException("Role Not Found");
            }
            if(userOptional.get().getRoles().contains(roleOptional.get())){
                log.info("This is not a user's email");
                throw new IllegalArgumentException("Not a User");
            }
            List<Chat> chats = chatRepository.findAllByUserEmailAndBuildingId(userEmail ,buildingId);
            List<ChatResponse> chatResponses = new ArrayList<>();
            for (Chat chat :
                    chats) {
                chatResponses.add(modelMapper.map(chat, ChatResponse.class));
            }
            return chatResponses;

        } catch (Exception e) {
            log.error("Error occurred while trying to get chat. Error : {}", e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<Object> showBuildingChatHistory(String email) {
        log.info("Executing show all building in chat history");
        try {
            Set<Building> buildings = buildingRepository.findAllByChat(email);
            List<InboxBuildingResponse> buildingRequests = new ArrayList<>();
            for (Building building:
                 buildings) {
                List<ChatResponse> chats = getChatByUser(building.getId());
                ChatResponse latestChat = chats.get(chats.size()-1) ;
                InboxBuildingResponse inbox = InboxBuildingResponse.builder()
                        .id(building.getId())
                        .buildingName(building.getName())
                        .buildingImages(building.getImages().get(0).getFileName())
                        .latestMessage(latestChat.getMessage())
                        .latestMessageTimestamp(latestChat.getTimestamp())
                        .build();
                buildingRequests.add(inbox);
            }
            log.info("Successfully retrieved all Building in chat history");
            return ResponseUtil.build(AppConstant.ResponseCode.SUCCESS,
                    buildingRequests,
                    HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while trying to get building in chat history. Error : {}", e.getMessage());
            return ResponseUtil.build(AppConstant.ResponseCode.UNKNOWN_ERROR, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
