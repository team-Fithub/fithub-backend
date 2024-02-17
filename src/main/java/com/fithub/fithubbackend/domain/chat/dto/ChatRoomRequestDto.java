package com.fithub.fithubbackend.domain.chat.dto;

import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;

// 채팅에 입장 요청에 사용되는 DTO
public class ChatRoomRequestDto {
    private Long roomId;

    public ChatRoom toEntity() {
        return ChatRoom.builder()
                .roomId(this.roomId)
                .build();
    }
}
