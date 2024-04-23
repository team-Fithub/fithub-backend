package com.fithub.fithubbackend.domain.chat.repository;

import com.fithub.fithubbackend.domain.chat.domain.Chat;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatPK_UserIdAndDeletedFalse(Long userId);

    @Query("SELECT e.chatPK.user FROM Chat e WHERE e.chatPK.chatRoom.roomId = :roomId")
    List<User> findUsersByRoomId(@Param("roomId") Long roomId);

    List<Chat> findByChatPK_ChatRoomRoomId(Long roomId);

    Optional<Chat> findByChatPK_ChatRoomRoomIdAndChatPK_UserIdNot(Long roomId, Long userId);

}
