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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatroom")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @Operation(summary = "채팅방 생성", responses = {
            @ApiResponse(responseCode = "200", description = "채팅방 생성 완료"),
            }, parameters = {
            @Parameter(name = "receiverNickname", description = "수신자 닉네임"),
    })
    @PostMapping("/create")
    public ResponseEntity<Void> createChat(@AuthUser User user, @RequestParam("receiverNickname") String receiverNickname) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        chatRoomService.save(user, receiverNickname);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "채팅방 목록 조회", responses = {
            @ApiResponse(responseCode = "200", description = "채팅 생성 완료"),
    })
    @GetMapping("/list")
    public ResponseEntity<List<ChatRoomResponseDto>> getChatList(@AuthUser User user) {
        if(user == null) throw new CustomException(ErrorCode.AUTHENTICATION_ERROR, "로그인한 사용자만 가능합니다.");
        return ResponseEntity.ok(chatRoomService.findChatRoomDesc(user));
    }


}
