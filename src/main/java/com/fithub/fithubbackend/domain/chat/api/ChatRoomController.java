package com.fithub.fithubbackend.domain.chat.api;

import com.fithub.fithubbackend.domain.chat.application.ChatMessageService;
import com.fithub.fithubbackend.domain.chat.application.ChatRoomService;
import com.fithub.fithubbackend.domain.chat.dto.ChatRoomResponseDto;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.domain.AuthUser;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {

    @Autowired
    private final ChatRoomService chatRoomService;

    @Operation(summary = "채팅방 목록 조회", responses = {
            @ApiResponse(responseCode = "200", description = "채팅방 목록 조회 완료"),
    })
    @GetMapping("/list")
    public ResponseEntity<List<ChatRoomResponseDto>> getChatList(@AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(chatRoomService.findChatRoomDesc(user));
    }

    @Operation(summary = "채팅방 생성", responses = {
            @ApiResponse(responseCode = "200", description = "채팅방 생성 완료"),
    }, parameters = {
            @Parameter(name = "receiverId", description = "수신자 아이디"),
    })
    @PostMapping("/create")
    public ResponseEntity<Void> createChat(@AuthUser User user, @RequestParam("receiverId") Long receiverId) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        chatRoomService.save(user, receiverId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "기존 채팅방 존재 유무 확인", responses = {
            @ApiResponse(responseCode = "200", description = "채팅방 존재 확인 완료"),
    }, parameters = {
            @Parameter(name = "receiverId", description = "수신자 아이디"),
    })
    @GetMapping("/check")
    public ResponseEntity<Boolean> hasChatRoom(@AuthUser User user, @RequestParam("receiverId") Long receiverId) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(chatRoomService.hasChatRoom(user.getId(), receiverId));
    }

}
