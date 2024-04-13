package com.fithub.fithubbackend.domain.chat.api;

import com.fithub.fithubbackend.domain.chat.application.ChatMessageService;
import com.fithub.fithubbackend.domain.chat.application.ChatRoomService;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageResponseDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.auth.JwtTokenProvider;
import com.fithub.fithubbackend.global.domain.AuthUser;
import com.fithub.fithubbackend.global.domain.UserAdapter;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final ChatRoomService chatRoomService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    @MessageMapping("/chatroom")
    public void sendMessage(@Header("Authorization") String accessToken, ChatMessageRequestDto messageRequestDto) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        UserAdapter userAdapter = (UserAdapter) authentication.getPrincipal();
        chatMessageService.save(messageRequestDto, userAdapter.getUser());
        simpMessagingTemplate.convertAndSend("/topic/chatroom/" + messageRequestDto.getRoomId(), messageRequestDto.getMessage());
    }

    @Operation(summary = "채팅 메세지 조회", responses = {
            @ApiResponse(responseCode = "200", description = "채팅 메세지 조회 완료"),
    })
    @GetMapping("/chatroom/message")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatList(@AuthUser User user, @RequestParam("chatRoomId") Long chatRoomId) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        List<ChatMessageResponseDto> chatMessageResponseDtoList = chatMessageService.findAllByChatRoomId(chatRoomId);
        return ResponseEntity.ok(chatMessageResponseDtoList);
    }
}

