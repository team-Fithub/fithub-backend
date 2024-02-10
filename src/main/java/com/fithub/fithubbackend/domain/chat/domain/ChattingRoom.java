package com.fithub.fithubbackend.domain.chat.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

public class ChattingRoom {
    @Id
    @Column(name = "ROOM_ID")
    private Long roomId;

    @CreationTimestamp
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "chattingRoom", cascade = CascadeType.REMOVE)
    private List<Message> chatMessageList;
}
