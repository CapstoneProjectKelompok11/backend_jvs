package com.capstone.booking.resolver.mutation;

import com.capstone.booking.domain.dto.ChatRequest;
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

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ChatResponse sendMessage(String receiver, String message) {
        log.info("Executing send chat mutation");
        return chatService.sendChat(receiver,message);
    }
}
