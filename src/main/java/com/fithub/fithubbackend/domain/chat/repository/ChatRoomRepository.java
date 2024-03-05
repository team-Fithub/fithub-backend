package com.fithub.fithubbackend.domain.chat.repository;

import com.fithub.fithubbackend.domain.chat.domain.ChatRoom;
import com.fithub.fithubbackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findByRoomId(Long id);


}
