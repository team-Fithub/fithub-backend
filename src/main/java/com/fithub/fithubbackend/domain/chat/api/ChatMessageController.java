package com.fithub.fithubbackend.domain.chat.api;

import com.fithub.fithubbackend.domain.chat.application.ChatMessageService;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageRequestDto;
import com.fithub.fithubbackend.global.auth.JwtTokenProvider;
import com.fithub.fithubbackend.global.domain.UserAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @MessageMapping("/chatroom")
    public void sendMessage(@Header("Authorization") String accessToken, ChatMessageRequestDto messageRequestDto) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        UserAdapter userAdapter = (UserAdapter) authentication.getPrincipal();
        chatMessageService.save(messageRequestDto, userAdapter.getUser());
        simpMessagingTemplate.convertAndSend("/topic/chatroom/" + messageRequestDto.getRoomId(), messageRequestDto.getMessage());
    }
}

