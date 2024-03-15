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

    @Operation(summary = "채팅 메세지", parameters = {
            @Parameter(name = "messageRequestDto", description = "채탱 메세지 요청 dto")
    })
    @MessageMapping("/chat/message")
    public void message(@AuthUser User user, @RequestParam("messageRequestDto") ChatMessageRequestDto messageRequestDto) {
        chatMessageService.save(messageRequestDto, user);
        simpMessagingTemplate.convertAndSend("/subscribe/rooms/" + messageRequestDto.getRoomId(), messageRequestDto.getMessage());
    }
}

