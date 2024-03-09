package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.dto.ChatMessageRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageResponseDto;

import java.util.List;

public interface ChatMessageService {
    public ChatMessageResponseDto findById(final Long chatMessageId);

    public Long save(final Long chatRoomId, final ChatMessageRequestDto requestDto);

    public void delete(final Long chatMessageId);

    public List<ChatMessageResponseDto> findAllByChatRoomIdDesc(final Long chatRoomId);

}
