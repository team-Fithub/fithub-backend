package com.fithub.fithubbackend.domain.chat.dto;

import com.fithub.fithubbackend.domain.chat.domain.Chat;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import com.fithub.fithubbackend.domain.chat.repository.ChatRoomRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ChatRequestDto {
    private ChatRoom chatRoom;
    private String chatRoomName;
    private User user;

    public Chat toEntity() {
        return Chat.builder()
                .chatRoom(this.chatRoom)
                .chatRoomName(this.chatRoomName)
                .user(this.user)
                .build();
    }

}
