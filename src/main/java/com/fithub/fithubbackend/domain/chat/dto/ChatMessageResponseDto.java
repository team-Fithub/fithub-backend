package com.fithub.fithubbackend.domain.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.global.domain.Document;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
public class ChatMessageResponseDto {

    @Schema(description = "메세지 id")
    private Long messageId;

    @Schema(description = "발신자 id")
    private Long senderId;

    @Schema(description = "발신자 닉네임")
    private String senderNickname;

    @Schema(description = "발신자 프로필이미지")
    private Document senderProfileImg;

    @Schema(description = "메세지 내용")
    private String message;

    @Schema(description = "메세지 생성일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;


    public ChatMessageResponseDto(ChatMessage entity) {
        this.messageId = entity.getMessageId();
        this.senderId = entity.getSender().getId();
        this.senderNickname = entity.getSender().getNickname();
        this.senderProfileImg = entity.getSender().getProfileImg();
        this.message = entity.getMessage();
        this.createdDate = entity.getCreatedDate();
    }
}
