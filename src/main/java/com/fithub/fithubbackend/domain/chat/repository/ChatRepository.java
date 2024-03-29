package com.fithub.fithubbackend.domain.chat.repository;

import com.fithub.fithubbackend.domain.chat.domain.Chat;
import com.fithub.fithubbackend.domain.chat.domain.ChatPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("SELECT e FROM Chat e WHERE e.chatPK.user.id = :id")
    List<Chat> findChatsById(Long id);
}
