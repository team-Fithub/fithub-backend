package com.fithub.fithubbackend.domain.chat.api;

import com.fithub.fithubbackend.domain.chat.application.ChatMessageService;
import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageRequestDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/chatroom/{id}")
    public void sendMessage(@DestinationVariable("id") Long id, @AuthUser User user, @RequestParam("messageRequestDto") ChatMessageRequestDto messageRequestDto) {
        chatMessageService.save(messageRequestDto, user);
        simpMessagingTemplate.convertAndSend("/sub/chatroom/" + messageRequestDto.getRoomId(), messageRequestDto.getMessage());
    }
}

