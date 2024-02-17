package com.fithub.fithubbackend.domain.chat.dto;

import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;

import java.util.List;

public class ChatRoomResponseDto {
    private Long roomId;
    private String roomName; // 채팅 상대 이름
    private String createdDate;
    private List<ChatMessage> chatMessageList;

    public ChatRoomResponseDto(ChatRoom entity) {
        this.roomId = entity.getRoomId();
        this.roomName = "roomName... 추가 작업 예정...";
    }
}
