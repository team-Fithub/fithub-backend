package com.fithub.fithubbackend.domain.trainer.domain;

import com.fithub.fithubbackend.global.domain.Document;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerLicenseTempImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TrainerCertificationRequest trainerCertificationRequest;

    @OneToOne(optional = false, cascade = CascadeType.PERSIST)
    private Document document;

    @Builder
    public TrainerLicenseTempImg(Document document) {
        this.document = document;
    }

    public void updateRequest(TrainerCertificationRequest request) {
        this.trainerCertificationRequest = request;
        request.getLicenseTempImgList().add(this);
    }
}
