package com.fithub.fithubbackend.domain.chat.domain;

import com.fithub.fithubbackend.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {
    @EmbeddedId
    private ChatPK chatPK;

    private String chatRoomName;

    @Builder
    public Chat(ChatRoom chatRoom, String chatRoomName, User user) {
        this.chatPK = new ChatPK(chatRoom, user);
        this.chatRoomName = chatRoomName;
    }

    public long getChatRoomId() {
        return this.chatPK.getChatRoom().getRoomId();
    }
}
