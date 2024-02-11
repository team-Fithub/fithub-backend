package com.fithub.fithubbackend.domain.chat.domain;

import com.fithub.fithubbackend.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChattingRoom extends BaseTimeEntity {
    @Id
    @Column(name = "ROOM_ID")
    private Long roomId;

    @OneToMany(mappedBy = "chattingRoom", cascade = CascadeType.REMOVE)
    private List<Message> chatMessageList;
}
