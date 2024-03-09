package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageResponseDto;
import com.fithub.fithubbackend.domain.chat.repository.ChatMessageRepository;
import com.fithub.fithubbackend.domain.chat.repository.ChatRoomRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatMessageServiceImpl implements ChatMessageService{

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    @Override
    public ChatMessageResponseDto findById(Long chatMessageId) {
        ChatMessage chatMessageEntity = this.chatMessageRepository.findById(chatMessageId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, "채팅 메세지가 존재하지 않음"));
        return new ChatMessageResponseDto(chatMessageEntity);
    }

    @Transactional
    @Override
    public Long save(Long chatRoomId, ChatMessageRequestDto requestDto) {
        return null;
    }

    @Transactional
    @Override
    public void delete(Long chatMessageId) {
        ChatMessage chatMessageEntity = this.chatMessageRepository.findById(chatMessageId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, "채팅 메세지가 존재하지 않음"));
        this.chatMessageRepository.delete(chatMessageEntity);
    }

    @Transactional
    @Override
    public List<ChatMessageResponseDto> findAllByChatRoomIdDesc(Long chatRoomId) {
        return null;
    }
}
