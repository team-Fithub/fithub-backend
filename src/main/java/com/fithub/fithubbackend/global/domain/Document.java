package com.fithub.fithubbackend.global.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Comment("s3 주소")
    private String url;

    @NotNull
    @Comment("입력 받은 파일 이름")
    private String inputName;

    @NotNull
    @Comment("파일 경로")
    private String path;

    @Builder
    public Document(String url, String inputName, String path) {
        this.url = url;
        this.inputName = inputName;
        this.path = path;
    }
}
