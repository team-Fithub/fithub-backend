package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.domain.ChatPK;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import com.fithub.fithubbackend.domain.chat.dto.ChatRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatRoomRequestDto;
import com.fithub.fithubbackend.domain.chat.dto.ChatRoomResponseDto;
import com.fithub.fithubbackend.domain.chat.repository.ChatRepository;
import com.fithub.fithubbackend.domain.chat.repository.ChatRoomRepository;
import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.domain.user.repository.UserRepository;
import com.fithub.fithubbackend.global.exception.CustomException;
import com.fithub.fithubbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ChatRoomResponseDto findById(final Long id) {
        ChatRoom entity = this.chatRoomRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, "채팅룸이 존재하지 않음"));
        return new ChatRoomResponseDto(entity);
    }

    @Transactional
    @Override
    public List<ChatRoomResponseDto> findAllDesc(User user) {
        Sort sort = Sort.by(Sort.Direction.DESC, "modifiedDate");
        List<ChatRoom> chatRoomList = this.chatRoomRepository.findAll(sort);
        return chatRoomList.stream()
                .map(chatRoom -> {
                    // ChatRoomResponseDto 객체 생성해준 뒤, 채팅룸 이름 추가
                    ChatRoomResponseDto dto = new ChatRoomResponseDto(chatRoom);
                    dto.setRoomName(chatRepository.findByChatPK(new ChatPK(chatRoom, user)).getChatRoomName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Long save(User user, String receiverNickname) {
        // chatRoom 테이블에 저장
        ChatRoomRequestDto chatRoomRequestDto = new ChatRoomRequestDto();
        Long roomId = this.chatRoomRepository.save(chatRoomRequestDto.toEntity()).getRoomId();

        // chatRequestDto 생성 후 chat 테이블에 저장 (chatRoom과 user의 연관관계)
        Optional<ChatRoom> chatRoomOptional = this.chatRoomRepository.findById(roomId);
        ChatRoom chatRoom = chatRoomOptional.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "채팅룸이 존재하지 않음"));

        // 채팅룸ID-본인ID
        ChatRequestDto chatRequestDto = new ChatRequestDto(chatRoom, receiverNickname, user);
        this.chatRepository.save(chatRequestDto.toEntity());

        // 채팅룸ID-상대ID
        Optional<User> receiverUserOptional = userRepository.findByNickname(receiverNickname);
        User receiverUser = receiverUserOptional.orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND, "상대 유저가 존재하지 않음"));
        chatRequestDto = new ChatRequestDto(chatRoom, "새로운 채팅", receiverUser);
        this.chatRepository.save(chatRequestDto.toEntity());

        return roomId;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        ChatRoom entity = this.chatRoomRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND, "채팅룸이 존재하지 않음"));
        this.chatRoomRepository.delete(entity);
    }
}
