package com.fithub.fithubbackend.domain.chat.domain;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(optional = false)
    private ChatRoom chatRoom;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "CHECK")
    private boolean check;

    public void setUser(User user) {
        this.user = user;
    }

    @Builder
    public ChatMessage(User user, ChatRoom chatRoom, String message) {
        this.user = user;
        this.chatRoom = chatRoom;
        this.message = message;
    }

}
