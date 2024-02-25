package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import com.fithub.fithubbackend.domain.chat.dto.ChatRoomRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatRoomResponseDto;
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
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    @Override
    public ChatRoomResponseDto findById(final Long id) {
        ChatRoom entity = this.chatRoomRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, "채팅룸이 존재하지 않음"));
        return new ChatRoomResponseDto(entity);
    }

    @Transactional
    @Override
    public List<ChatRoomResponseDto> findAllDesc() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        List<ChatRoom> chatRoomList = this.chatRoomRepository.findAll(sort);
        return chatRoomList.stream().map(ChatRoomResponseDto::new).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Long save(ChatRoomRequestDto requestDto) {
        return this.chatRoomRepository.save(requestDto.toEntity()).getRoomId();
    }

    @Transactional
    @Override
    public void delete(Long id) {
        ChatRoom entity = this.chatRoomRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, "채팅룸이 존재하지 않음"));
        this.chatRoomRepository.delete(entity);
    }
}
