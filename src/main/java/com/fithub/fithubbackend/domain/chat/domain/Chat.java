package com.fithub.fithubbackend.domain.chat.domain;

import com.fithub.fithubbackend.domain.user.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "해당 채팅방에서 나갔는지, 안 나갔는지")
    private boolean deleted;

    @Builder
    public Chat(ChatRoom chatRoom, String chatRoomName, User user) {
        this.chatPK = new ChatPK(chatRoom, user);
        this.chatRoomName = chatRoomName;
        deleted = false;
    }

    public long getChatRoomId() {
        return this.chatPK.getChatRoom().getRoomId();
    }

    public void deleteChat() {
        this.deleted = true;
    }
}
