package com.capstone.booking.service;

import com.capstone.booking.domain.dao.Chat;
import com.capstone.booking.domain.dao.User;
import com.capstone.booking.domain.dto.ChatRequest;
import com.capstone.booking.domain.dto.ChatResponse;
import com.capstone.booking.repository.ChatRepository;
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

    public List<ChatResponse> getChatBySenderId(Long senderId) {
        log.info("Executing Getting chat history");
        try {
            List<Chat> chats = chatRepository.findAllBySenderId(senderId);
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
