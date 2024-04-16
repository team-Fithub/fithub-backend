package com.fithub.fithubbackend.domain.chat.application;

import com.fithub.fithubbackend.domain.chat.dto.ChatRoomResponseDto;
import com.fithub.fithubbackend.domain.user.domain.User;

import java.util.List;

public interface ChatRoomService {

    /* ChatRoom 조회 */
    public ChatRoomResponseDto findById(final Long id);

    /* ChatRoom 목록 조회 - 최신순 */
    public List<ChatRoomResponseDto> findChatRoomDesc(User user);

    /* ChatRoom 생성 */
    Long save(final User user, final Long receiverId);

    /* ChatRoom 삭제 */
    public void delete(final Long id);

    public Long hasChatRoom(final Long userId, final Long receiverId);

    public long findRoomIdByUserId(final long userId);
}
