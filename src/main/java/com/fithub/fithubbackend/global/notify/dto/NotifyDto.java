package com.fithub.fithubbackend.global.notify.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fithub.fithubbackend.global.domain.Notify;
import com.fithub.fithubbackend.global.notify.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "알림 dto")
public class NotifyDto {

    @NotNull
    @Schema(description = "알림 primary key")
    private Long id;

    @NotNull
    @Schema(description = "알림 내용")
    private String content;

    @NotNull
    @Schema(description = "클릭 시 이동할 주소")
    private String url;

    @NotNull
    @Schema(description = "알림 타입")
    private NotificationType type;

    @NotNull
    @Schema(description = "알림 읽음 / 안 읽음")
    private boolean read;

    @Schema(description = "알림 생성일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @Schema(description = "알림 수정일 -> 알림 읽은 시간")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedDate;

    @Builder
    public NotifyDto (Notify notify) {
        this.id = notify.getId();
        this.content = notify.getContent();
        this.url = notify.getUrl();
        this.type = notify.getType();
        this.read = notify.getIsRead();
        this.createDate = notify.getCreatedDate();
        this.modifiedDate = notify.getModifiedDate();
    }
}
