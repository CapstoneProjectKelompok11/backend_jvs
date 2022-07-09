package com.capstone.booking.resolver.query;

import com.capstone.booking.domain.dto.ChatResponse;
import com.capstone.booking.service.ChatService;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ChatQueryResolver implements GraphQLQueryResolver {

    @Autowired
    private ChatService chatService;

    @PreAuthorize("hasRole('USER')")
    public List<ChatResponse> getChatByUser(Long buildingId) {
        log.info("Executing get chat query");
        return chatService.getChatByUser(buildingId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ChatResponse> getChatForAdmin(String userEmail, Long buildingId) {
        log.info("Executing get chat for admin query");
        return chatService.getChatForAdmin(userEmail, buildingId);
    }
}
