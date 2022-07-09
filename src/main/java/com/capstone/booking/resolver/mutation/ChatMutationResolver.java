package com.capstone.booking.resolver.mutation;

import com.capstone.booking.domain.dto.ChatResponse;
import com.capstone.booking.service.ChatService;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatMutationResolver implements GraphQLMutationResolver {


    @Autowired
    private ChatService chatService;

    @PreAuthorize("hasRole('USER')")
    public ChatResponse userSendMessage(String message, Long buildingId) {
        log.info("Executing send chat mutation");
        return chatService.userSendChat(message, buildingId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ChatResponse adminSendMessage(String receiver, String message, Long buildingId) {
        log.info("Executing send chat mutation");
        return chatService.adminSendChat(receiver,message, buildingId);
    }
}
