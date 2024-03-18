package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.dto.ChatMessageRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageResponseDto;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.List;

public interface ChatMessageService {
    public ChatMessageResponseDto findById(final Long chatMessageId);

    public Long save(final ChatMessageRequestDto requestDto, final User user);

    public void delete(final Long chatMessageId);

    public List<ChatMessageResponseDto> findAllByChatRoomIdDesc(final Long chatRoomId);

}
