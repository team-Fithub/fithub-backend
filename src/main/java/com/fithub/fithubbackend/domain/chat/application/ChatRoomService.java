package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.dto.ChatRoomResponseDto;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.List;

public interface ChatRoomService {

    /* ChatRoom 목록 조회 - 최신순 */
    List<ChatRoomResponseDto> findChatRoomDesc(User user);

    /* ChatRoom 생성 */
    Long save(final User user, final Long receiverId);

    /* ChatRoom 삭제 */
    void deleteChatRoom(Long userId, Long roomId);

    Long hasChatRoom(final Long userId, final Long receiverId);

    Boolean isReceiverExists(Long userId, Long roomId);
}
