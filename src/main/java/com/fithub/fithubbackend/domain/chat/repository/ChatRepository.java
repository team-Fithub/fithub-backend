package com.fithub.fithubbackend.domain.chat.repository;

import com.fithub.fithubbackend.domain.chat.domain.Chat;
import com.fithub.fithubbackend.domain.chat.domain.ChatPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat findByChatPK(ChatPK chatPK);
}
