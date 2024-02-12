package com.fithub.fithubbackend.domain.chat.domain;

import com.fithub.fithubbackend.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {
    @EmbeddedId
    private ChatPK chatPK;
}
