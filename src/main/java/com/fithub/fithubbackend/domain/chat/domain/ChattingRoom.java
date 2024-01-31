package com.fithub.fithubbackend.domain.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class ChattingRoom {
    @Id
    @Column(name = "ROOM_ID")
    private Long roomId;

    @CreationTimestamp
    private LocalDateTime createAt;
}
