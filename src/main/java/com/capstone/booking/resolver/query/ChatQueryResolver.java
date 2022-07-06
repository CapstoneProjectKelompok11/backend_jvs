package com.capstone.booking.resolver.query;

import com.capstone.booking.domain.dto.ChatRequest;
import com.capstone.booking.domain.dto.ChatResponse;
import com.capstone.booking.service.ChatService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ChatQueryResolver implements GraphQLQueryResolver {

    @Autowired
    private ChatService chatService;

    public List<ChatResponse> getChatBySender(Long senderId) {
        log.info("Executing get chat query");
        return chatService.getChatBySenderId(senderId);
    }
}
