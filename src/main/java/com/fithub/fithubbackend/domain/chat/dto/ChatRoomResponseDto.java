package com.fithub.fithubbackend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fithub.fithubbackend.domain.chat.domain.Chat;
import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.global.domain.Document;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatRoomResponseDto {

    @Schema(description = "채팅방 id")
    private Long roomId;

    @Schema(description = "채팅상대 이름(채팅방 이름)")
    private String roomName; // 채팅 상대 이름

    @Schema(description = "채팅상대 프로필 이미지")
    private Document senderProfileImg;

    @Schema(description = "채팅방 수정일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastMessageDate;

    @Schema(description = "마지막 채팅 내용")
    private String lastMessage;

    @Schema(description = "안읽은 채팅 개수")
    private int unreadChatCount;



    public ChatRoomResponseDto(Chat entity) {
        this.roomId = entity.getChatPK().getChatRoom().getRoomId();
        this.roomName = entity.getChatRoomName();
        this.senderProfileImg = entity.getChatPK().getUser().getProfileImg();

        int messageListSize = entity.getChatPK().getChatRoom().getChatMessageList().size();
        ChatMessage lastChatMessage = entity.getChatPK().getChatRoom().getChatMessageList().get(messageListSize - 1);
        this.lastMessageDate = lastChatMessage.getCreatedDate();
        this.lastMessage = lastChatMessage.getMessage();

        this.unreadChatCount = 0;
    }


}
