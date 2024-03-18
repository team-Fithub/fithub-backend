package com.fithub.fithubbackend.domain.chat.dto;

import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ChatMessageRequestDto {

    @Schema(description = "채팅 메세지")
    private String message;

    @Schema(description = "채팅방 id")
    private Long roomId;

    public ChatMessageRequestDto(String message, Long roomId) {
        this.message = message;
        this.roomId = roomId;
    }

}

