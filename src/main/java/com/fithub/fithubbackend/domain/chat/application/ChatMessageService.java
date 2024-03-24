package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.dto.ChatMessageRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageResponseDto;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.List;

public interface ChatMessageService {
    ChatMessageResponseDto findById(final Long chatMessageId);

    Long save(final ChatMessageRequestDto requestDto, final User user);

    void delete(final Long chatMessageId);

    List<ChatMessageResponseDto> findAllByChatRoomIdDesc(final Long chatRoomId);

}
