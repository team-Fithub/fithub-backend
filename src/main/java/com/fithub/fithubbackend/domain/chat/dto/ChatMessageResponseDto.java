package com.fithub.fithubbackend.domain.chat.dto;

import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.time.format.DateTimeFormatter;

public class ChatMessageResponseDto {
    private Long messageId;
    private User sender;
    private String message;
    private String createdDate;


    public ChatMessageResponseDto(ChatMessage entity) {
        this.messageId = entity.getMessageId();
        this.sender = entity.getSender();
        this.message = entity.getMessage();
        this.createdDate = entity.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
    }
}
