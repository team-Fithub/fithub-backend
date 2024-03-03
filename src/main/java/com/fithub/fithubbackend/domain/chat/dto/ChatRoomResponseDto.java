package com.fithub.fithubbackend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import com.fithub.fithubbackend.domain.chat.repository.ChatRoomRepository;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoomResponseDto {
    private Long roomId;
    private String roomName; // 채팅 상대 이름
    private String createdDate;
    private List<ChatMessage> chatMessageList;

    ChatRoomRepository chatRoomRepository;

    public ChatRoomResponseDto(ChatRoom entity) {
        this.roomId = entity.getRoomId();
        this.createdDate = String.valueOf(entity.getCreatedDate());
    }


}
