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
public class TrainerExpertise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Trainer trainer;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category expertise;

    @Builder
    public TrainerExpertise(Trainer trainer, Category expertise) {
        this.trainer = trainer;
        this.expertise = expertise;
    }
}
