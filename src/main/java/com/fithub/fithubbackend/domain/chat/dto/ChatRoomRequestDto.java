package com.fithub.fithubbackend.domain.chat.dto;

import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomRequestDto {
    private String roomName;

    @Builder
    public ChatRoomRequestDto(String roomName) {
        this.roomName = roomName;
    }

    public ChatRoom toEntity() {
        return ChatRoom

    }
}
