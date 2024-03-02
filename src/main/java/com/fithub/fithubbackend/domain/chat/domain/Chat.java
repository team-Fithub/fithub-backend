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

    @Column(name = "room_name")
    private String chatRoomName;

    @Builder
    public Chat(ChatRoom chatRoom, String chatRoomName, User user) {
        this.chatPK = new ChatPK();
        this.chatRoomName = chatRoomName;
        this.chatPK.setChatRoom(chatRoom);
        this.chatPK.setUser(user);
    }
}
