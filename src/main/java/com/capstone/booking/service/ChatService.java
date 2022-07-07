package com.capstone.booking.service;

import com.capstone.booking.constant.AppConstant;
import com.capstone.booking.domain.dao.Chat;
import com.capstone.booking.domain.dao.Role;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.ChatRequest;
import com.capstone.booking.domain.dto.ChatResponse;
import com.capstone.booking.repository.ChatRepository;
import com.capstone.booking.repository.RoleRepository;
import com.capstone.booking.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private ModelMapper modelMapper;

    public ChatResponse sendChat(ChatRequest request) {
        log.info("Executing sending chat");
        try {
            Optional<User> senderOptional = userRepository.findById(request.getSender());
            Optional<User> receiverOptional = userRepository.findById(request.getReceiver());
            if(senderOptional.isEmpty()||receiverOptional.isEmpty()){
                log.info("User data not found, Sender found : {}, Receiver found: {}",
                        senderOptional.isPresent(),
                        receiverOptional.isPresent());
                throw new IllegalArgumentException("User Not Found");
            }

            Optional<Role> roleOptional = roleRepository.findByName(AppConstant.RoleType.ROLE_ADMIN);
            if(roleOptional.isEmpty()) {
                log.info("Role Not Found");
                throw new IllegalArgumentException("Role Not Found");
            }

            if(senderOptional.get().getRoles().contains(roleOptional.get()) == receiverOptional.get().getRoles().contains(roleOptional.get())){
                log.info("Chat is only available from admin to user and vice versa");
                throw new UnsupportedOperationException("Admin can only chat to user and vice versa");
            }

            Chat chat = Chat.builder()
                    .sender(senderOptional.get())
                    .receiver(receiverOptional.get())
                    .message(request.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();

            chatRepository.save(chat);
            return modelMapper.map(chat, ChatResponse.class);
        } catch (Exception e) {
            log.error("Error occurred while trying to send chat. Error : {}", e.getMessage());
            throw e;
        }
    }

    public List<ChatResponse> getChatByUserId(Long userId) {
        log.info("Executing Getting chat history");
        try {
            List<Chat> chats = chatRepository.findAllBySenderIdOrReceiverId(userId,userId);
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

    public List<ChatResponse> getChatByAdminWithUser(Long userId) {
        log.info("Executing Getting chat history for Admin");
        try {
            List<Chat> chats = chatRepository.findAllBySenderIdOrReceiverId(userId ,userId);
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
}
