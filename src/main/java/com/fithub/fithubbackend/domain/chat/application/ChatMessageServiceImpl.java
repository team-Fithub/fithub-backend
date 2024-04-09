package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.domain.ChatMessage;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatMessageResponseDto;
import com.fithub.fithubbackend.domain.chat.repository.ChatMessageRepository;
import com.fithub.fithubbackend.domain.chat.repository.ChatRepository;
import com.fithub.fithubbackend.domain.chat.repository.ChatRoomRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageServiceImpl implements ChatMessageService{

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;


    @Transactional
    @Override
    public ChatMessageResponseDto findById(Long chatMessageId) {
        ChatMessage chatMessageEntity = this.chatMessageRepository.findById(chatMessageId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, "채팅 메세지가 존재하지 않음"));
        return new ChatMessageResponseDto(chatMessageEntity);
    }

    @Transactional
    @Override
    public Long save(ChatMessageRequestDto requestDto, User sender) {
        ChatMessage message = ChatMessage.builder()
                    .chatRoom(chatRoomRepository.findByRoomId(requestDto.getRoomId()))
                    .message(requestDto.getMessage())
                    .sender(sender)
                    .build();
        chatMessageRepository.save(message);

        // 메세지 알림
        Long roomId = message.getChatRoom().getRoomId();
        List<User> users = chatRepository.findUsersByRoomId(roomId);
        users.remove(sender);
        Long receiverId = users.get(0).getId();
        simpMessagingTemplate.convertAndSend("/topic/alarm/" + receiverId, "새로운 채팅!");

        return message.getMessageId();
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
    public List<ChatMessageResponseDto> findAllByChatRoomId(Long chatRoomId) {
        ChatRoom chatRoomEntity = this.chatRoomRepository.findById(chatRoomId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, "채팅방이 존재하지 않음"));
        List<ChatMessage> chatMessageList = this.chatMessageRepository.findAllByChatRoomOrderByCreatedDateDesc(chatRoomEntity);
        return  chatMessageList.stream().map(ChatMessageResponseDto::new).collect(Collectors.toList());
    }

}
