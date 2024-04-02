package com.fithub.fithubbackend.global.domain;

import com.fithub.fithubbackend.domain.user.domain.User;
import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import com.fithub.fithubbackend.global.notify.NotificationType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notify extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User receiver;

    @NotNull
    private String content;

    @NotNull
    private String url;

    @Enumerated(EnumType.STRING)
    @NotNull
    private NotificationType type;

    @NotNull
    private Boolean isRead;

    @Builder
    public Notify(User receiver, String content, String url, NotificationType type) {
        this.receiver = receiver;
        this.content = content;
        this.url = url;
        this.type = type;
        this.isRead = false;
    }
}
