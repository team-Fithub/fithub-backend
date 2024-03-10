package com.fithub.fithubbackend.domain.Training.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class TrainingCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.MERGE, optional = false)
    @JsonIgnore
    private Training training;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    @Builder
    public TrainingCategory(Training training, Category category) {
        this.training = training;
        this.category = category;
    }
}
