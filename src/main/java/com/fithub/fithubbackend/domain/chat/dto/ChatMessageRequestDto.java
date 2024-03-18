package com.fithub.fithubbackend.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ChatMessageRequestDto {

    @Schema(description = "채팅 메세지")
    private String message;

    @Schema(description = "채팅방 id")
    private Long roomId;
}

