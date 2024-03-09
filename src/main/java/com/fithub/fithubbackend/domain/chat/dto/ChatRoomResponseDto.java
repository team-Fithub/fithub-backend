package com.fithub.fithubbackend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fithub.fithubbackend.domain.chat.domain.Chat;
import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import com.fithub.fithubbackend.domain.chat.repository.ChatRoomRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoomResponseDto {

    @Schema(description = "채팅방 id")
    private Long roomId;

    @Schema(description = "채팅상대 이름")
    private String roomName; // 채팅 상대 이름

    @Schema(description = "채팅방 수정일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;


    public ChatRoomResponseDto(Chat entity) {
        this.roomId = entity.getChatPK().getChatRoom().getRoomId();
        this.roomName = entity.getChatRoomName();
    }


}
