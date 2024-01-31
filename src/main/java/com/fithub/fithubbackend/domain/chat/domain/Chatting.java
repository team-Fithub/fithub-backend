package com.fithub.fithubbackend.domain.chat.domain;

import jakarta.persistence.*;

@Entity
public class Chatting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_ID")
    private Long roomId;

    @Column(name = "ID")
    private Long id;

}
