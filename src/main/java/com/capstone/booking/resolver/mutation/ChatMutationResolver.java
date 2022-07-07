package com.capstone.booking.resolver.mutation;

import com.capstone.booking.domain.dto.ChatRequest;
import com.capstone.booking.domain.dto.ChatResponse;
import com.capstone.booking.service.ChatService;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChatMutationResolver implements GraphQLMutationResolver {


    @Autowired
    private ChatService chatService;

    public ChatResponse sendMessage(ChatRequest request) {
        log.info("Executing send chat mutation");
        return chatService.sendChat(request);
    }
}
