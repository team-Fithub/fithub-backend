package com.fithub.fithubbackend.domain.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class Message {
    @Id
    @Column(name = "ROOM_ID")
    private Long roomId;

}
