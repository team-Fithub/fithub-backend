package com.fithub.fithubbackend.domain.chat.repository;

import com.fithub.fithubbackend.domain.chat.domain.Chat;
import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByChatPK_UserId(Long userId);

    @Query("SELECT e.chatPK.user FROM Chat e WHERE e.chatPK.chatRoom.roomId = :roomId")
    List<User> findUsersByRoomId(@Param("roomId") Long roomId);

    Chat findChatByChatPK_UserId(Long userId);

    List<Chat> findByChatPK_ChatRoom(ChatRoom chatRoom);

}
