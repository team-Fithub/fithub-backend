package com.fithub.fithubbackend.domain.trainer.domain;


import com.fithub.fithubbackend.global.common.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrainerExpertiseTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private TrainerCertificationRequest trainerCertificationRequest;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category expertise;

    @Builder
    public TrainerExpertiseTemp(Category expertise) {
        this.expertise = expertise;
    }

    public void updateRequest(TrainerCertificationRequest request) {
        this.trainerCertificationRequest = request;
        request.getExpertiseTempList().add(this);
    }

}
