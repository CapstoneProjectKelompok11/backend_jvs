package com.capstone.booking.resolver.query;

import com.capstone.booking.domain.dto.ChatResponse;
import com.capstone.booking.service.ChatService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ChatQueryResolver implements GraphQLQueryResolver {

    @Autowired
    private ChatService chatService;

    public List<ChatResponse> getChatByUser(Long userId) {
        log.info("Executing get chat query");
        return chatService.getChatByUserId(userId);
    }

    public List<ChatResponse> getChatForAdmin(Long userId) {
        log.info("Executing get chat for admin query");
        return chatService.getChatByAdminWithUser(userId);
    }
}
