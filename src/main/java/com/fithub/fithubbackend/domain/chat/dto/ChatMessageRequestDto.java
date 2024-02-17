package com.fithub.fithubbackend.domain.chat.dto;

import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;

public class ChatMessageRequestDto {

    private String message;
    private ChatRoom chatRoom;

    public ChatMessageRequestDto(String message, ChatRoom chatRoom) {
        this.message = message;
        this.chatRoom = chatRoom;
    }

    public ChatMessage toEntity() {
        return ChatMessage.builder()
                .message(this.message)
                .chatRoom(this.chatRoom)
                .build();
    }
}

